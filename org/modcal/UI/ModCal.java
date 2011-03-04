package org.modcal.UI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.modcal.CalibrationRequest;
import org.modcal.Hydrus1DController;
import org.modcal.Settings;
import org.modcal.Sufi2Sampler;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
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
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2) {
			System.out.println(help);
			return;
		}
		
		CalibrationRequest request = new CalibrationRequest(
				new Sufi2Sampler(),
				new Hydrus1DController(args[0]),
				args[1]);

		System.out.print("Starting the Mule message bus... ");
		DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
		SpringXmlConfigurationBuilder configBuilder = new SpringXmlConfigurationBuilder(
				Settings.getString("Modcal.mule-config"));
		MuleContext muleContext = muleContextFactory.createMuleContext(configBuilder);
		muleContext.start();
		LocalMuleClient muleClient = muleContext.getClient();
		//MuleClient muleClient = new MuleClient(muleContext);
		
		System.out.println("done.");
		
		System.out.print("Sending calibration request... ");
		muleClient.dispatch("vm://UI", request, null);
		System.out.println("done.");
		
		MuleMessage reply;
		
		reply = muleClient.request("vm://Progress", Long.MAX_VALUE);
		while (reply.getPayload() instanceof CalibrationRequest) {
			System.out.println(((CalibrationRequest)reply.getPayload()
			).getCurrentOutput().shortInfo());
			reply = muleClient.request("vm://Progress", Long.MAX_VALUE);
		}
		
		if (reply.getPayload() instanceof Exception) {
			System.out.println(((Exception)reply.getPayload()).getMessage());
			((Exception)reply.getPayload()).printStackTrace();
		}
		else {
			System.out.print("Writing complete results to "
					+ Settings.getString("Modcal.output-file") + "... ");
			File outFile = new File(Settings.getString("Modcal.output-file"));
			BufferedWriter ow = new BufferedWriter(new FileWriter(outFile));
			ow.write(reply.getPayload().toString());
			ow.newLine();
			ow.close();
			System.out.println("done.");
		}
		
		muleContext.stop();
		muleContext.dispose();
	}
}
