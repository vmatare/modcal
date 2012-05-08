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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.modcal.data.NumericSample;
import org.modcal.data.ObservationData;
import org.modcal.model.Hydrus1DController;
import org.modcal.model.Sufi2Sampler;
import org.modcal.output.CalibrationResult;
import org.modcal.output.ModelOutput;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.client.LocalMuleClient;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;

public class ModCal {
	
	public static final String help = 
"\nUsage: modcal.bat HYDRUS_DATA OBSERVED\n\n" +
" HYDRUS_DATA The directory which contains the Hydrus 1D data files.\n\n" +
" OBSERVED    Path to the file which contains the observed (measured) data.\n" +
"             This file must be a tab- or space-delimited two-column table.\n" +
"             The first row must specify the parameter names, the first of\n" +
"             which must be \"Time\". The second is one of Hydrus 1D's output\n" +
"             parameters. The name must exactly match the name of a parameter\n" +
"             in T_LEVEL.OUT.\n\n";
	
	MuleContext muleContext;
	LocalMuleClient muleClient;
	ObservationData observation;
	
	public static void main(String[] args) throws Exception {
		System.out.println("\nModCal 1.1 - (C) 01-2012 Victor Matare");
		System.out.println(  "=======================================");
		
		if (args.length != 2) {
			System.out.println(help);
			return;
		}
		
		ModCal modcal = null;
		try {
			modcal = new ModCal(args[0], args[1]);
			if (Settings.getString("Modcal.sampler").equalsIgnoreCase("SUFI2")) modcal.run();
			else {
				System.out.println("Waiting for external sampler...");
				while (System.in.read() != -1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (modcal != null) modcal.close();
		}
	}
	
	public ModCal(String hydrusPath, String observedPath) throws Exception {
		System.out.print("Starting the Mule message bus... ");
		DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
		SpringXmlConfigurationBuilder configBuilder;
		try {
			configBuilder = new SpringXmlConfigurationBuilder(
					Settings.getString("Modcal.mule-config"));
			muleContext = muleContextFactory.createMuleContext(configBuilder);
			muleContext.start();
		} catch (MuleException e) {
			e.printStackTrace();
			throw new RuntimeException("Error starting the Mule message bus!");
		}
		System.out.println("done.");
		
		System.out.print("Initializing and registering Hydrus 1D... ");
		muleClient = muleContext.getClient();
		Map<String, Set<String>> fileParams;
		try {
			fileParams = parseFileParams(
					Settings.getString("Hydrus1D.outputParams"));
		}
		catch (ConfigException e) {
			System.out.println(Settings.getString("Hydrus1D.outputParams") + ": " + e.getMessage());
			return;
		}
		observation = new ObservationData(
				observedPath, unrollFileParams(fileParams));
		muleContext.getRegistry().registerObject(
				"Hydrus1DController", new Hydrus1DController(hydrusPath, fileParams));
		muleContext.getRegistry().registerObject(
				"ObservationData", observation);
		System.out.println("done.");
	}
	
	public void close() {
		System.out.print("Stopping the Mule message bus... ");
		try {
			muleContext.stop();
		} catch (MuleException e) {
			e.printStackTrace();
		}
		muleContext.dispose();
		System.out.println("done.");
	}
	
	
	private void run() throws Exception {
		
		Sufi2Sampler sampler = new Sufi2Sampler();
		NumericSample sample;
		CalibrationResult result = new CalibrationResult(observation);
		Object payload;
		ModelOutput output;
		int iteration = 1;
		
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
	
	private Set<String> unrollFileParams(Map<String, Set<String>> fileParams) {
		Set<String> rv = new CopyOnWriteArraySet<String>();
		
		for (Map.Entry<String, Set<String>> e : fileParams.entrySet())
			for (String param : e.getValue())
				rv.add(e.getKey() + ":" + param);
		
		return rv;
	}
}
