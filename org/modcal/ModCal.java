/*
 *   This file is part of ModCal.
 *
 *   ModCal is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ModCal is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ModCal.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Additional permission under GNU GPL version 3 section 7
 *   
 *   If you modify this Program, or any covered work, by linking or
 *   combining it with Mule ESB (or a modified version of Mule ESB),
 *   containing parts covered by the terms of CPAL, the licensors of
 *   this Program grant you additional permission to convey the
 *   resulting work.
 *   {Corresponding Source for a non-source form of such a combination
 *   shall include the source code for the parts of Mule ESB used as
 *   well as that of the covered work.}
 *   
 */

package org.modcal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.modcal.data.NumericSample;
import org.modcal.data.ObservationData;
import org.modcal.model.ColfracController;
import org.modcal.model.Hydrus1DController;
import org.modcal.model.Sufi2Sampler;
import org.modcal.output.CalibrationResult;
import org.modcal.output.ModelOutput;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.registry.RegistrationException;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;

public class ModCal {
	
	MuleContext muleContext;
	ObservationData h1dObservation, colfracObservation;
	
	public static void main(String[] args) throws Exception {
		System.out.println("\nModCal 1.3 - (C) 09-2012 Victor Matare");
		System.out.println("=======================================");
		
		ModCal modcal = null;
		modcal = new ModCal();
		try {
			if (Boolean.valueOf(Settings.getString("Hydrus1D.enabled")))
				modcal.initHydrus1D();
			
			if (Boolean.valueOf(Settings.getString("Colfrac.enabled")))
				modcal.initColfrac();
			
			if (Settings.getString("Modcal.sampler").equalsIgnoreCase("SUFI2")) modcal.sampleSufi2();
			else {
				System.out.println("Waiting for external sampler...");
				while (System.in.read() != -1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		modcal.cleanup();
	}
	
	public ModCal() {
		System.out.print("Starting the Mule message bus... ");
		DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
		SpringXmlConfigurationBuilder configBuilder;
		try {
			configBuilder = new SpringXmlConfigurationBuilder("mule-config.xml");
			muleContext = muleContextFactory.createMuleContext(configBuilder);
			muleContext.start();
		} catch (MuleException e) {
			e.printStackTrace();
			throw new RuntimeException("Error starting the Mule message bus!");
		}
		System.out.println("done.");
	}
		
	private void initHydrus1D() throws IOException, RegistrationException {
		System.out.print("Initializing and registering Hydrus 1D... ");
		
		Map<String, Set<String>> h1dOutputParams;
		try {
			h1dOutputParams = parseFileParams(
					Settings.getString("Hydrus1D.outputParams"));
		}
		catch (ConfigException e) {
			System.out.println(Settings.getString("Hydrus1D.outputParams") + ": " + e.getMessage());
			return;
		}
		h1dObservation = new ObservationData(
				Settings.getString("Hydrus1D.observation"),
				unrollFileParams(h1dOutputParams));
		muleContext.getRegistry().registerObject(
				"Hydrus1DController", new Hydrus1DController(
						Settings.getString("Hydrus1D.dataDir"), h1dOutputParams));
		muleContext.getRegistry().registerObject(
				"Hydrus1D.observation", h1dObservation);
		System.out.println("done.");
	}
	
	private void initColfrac() throws IOException, RegistrationException {
		System.out.print("Initializing and registering Colfrac... ");
		Set<String> colfracOutputParams = parsePlainParams(Settings.getString("Colfrac.outputParams"));
		colfracObservation = new ObservationData(
				Settings.getString("Colfrac.observation"),
				colfracOutputParams);
		muleContext.getRegistry().registerObject(
				"ColfracController", new ColfracController(
						Settings.getString("Colfrac.path"), colfracOutputParams));
		muleContext.getRegistry().registerObject(
				"Colfrac.observation", colfracObservation);
		System.out.println("done.");

	}
	
	public void cleanup() {
		System.out.print("Stopping the Mule message bus... ");
		try {
			muleContext.stop();
		} catch (MuleException e) {
			e.printStackTrace();
		}
		muleContext.dispose();
		System.out.println("done.");
	}
	
	
	private void sampleSufi2() throws Exception {
		
		Sufi2Sampler sampler = new Sufi2Sampler();
		NumericSample sample;
		CalibrationResult result = new CalibrationResult(h1dObservation);
		Object payload;
		ModelOutput output;
		int iteration = 1;
		LocalMuleClient muleClient = muleContext.getClient();
		
		while((sample = sampler.nextSample()) != null) {
			System.out.print(sample + " ... ");
			payload = muleClient.send("vm://Sampler", sample, null).getPayload();
			if (payload instanceof ModelOutput) {
				output = ((ModelOutput)payload);
				output.setIteration(iteration++);
				result.add(output);
				System.out.println(output.shortInfo());
			}
			else if (payload instanceof Exception) {
				System.out.println(((Exception)payload).getMessage());
				throw new Exception(((Exception)payload));
			}
		}
		
		System.out.print("Writing complete results to "
				+ Settings.getString("Modcal.output-file") + "... ");
		File outFile = new File(Settings.getString("Modcal.output-file"));
		BufferedWriter ow = new BufferedWriter(new FileWriter(outFile));
		ow.write(result.toString());
		ow.newLine();
		ow.close();
		System.out.println("done.");
	}
	
	/**
	 * @param line in FILENAME:param1,param2,[...] [...] syntax
	 * @return a {@link Map} with the filename as a key and the {@link Set} of
	 * parameter names in that file as a value
	 * @throws ConfigException
	 */
	private Map<String, Set<String>> parseFileParams(String line) throws ConfigException {
		Map<String, Set<String>> rv = new ConcurrentSkipListMap<String, Set<String>>();
		
		for (String entry : line.trim().split("\\s+")) {
			String[] file2params = entry.split(":");
			if (file2params.length != 2) throw new ConfigException("Invalid file:parameter format!");
			String[] params = file2params[1].split(",");
			if (params.length < 1) throw new ConfigException("Missing parameter name!");
			if (!rv.containsKey(file2params[0])) rv.put(file2params[0], new CopyOnWriteArraySet<String>());
			Set<String> ps = rv.get(file2params[0]);
			for (String parm : params) {
				if (parm.length() < 1) throw new ConfigException("Invalid form (empty parameter name)!");
				if (ps.contains(parm)) throw new ConfigException("Duplicate parameter name!");
				if (parm.equals("Time")) throw new ConfigException("Parameter name can't be \"time\"!");
				ps.add(parm);
				rv.put(file2params[0], ps);
			}
		}
		
		return rv;
	}
	
	private Set<String> parsePlainParams(String line) {
		Set<String> rv = new CopyOnWriteArraySet<String>(Arrays.asList(line.trim().split("\\s")));
		return rv;
	}
	
	private Set<String> unrollFileParams(Map<String, Set<String>> fileParams) {
		Set<String> rv = new CopyOnWriteArraySet<String>();
		
		for (Map.Entry<String, Set<String>> e : fileParams.entrySet())
			for (String param : e.getValue())
				rv.add(e.getKey() + ":" + param);
		
		return rv;
	}
}
