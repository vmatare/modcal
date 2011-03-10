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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Timer;

public class Hydrus1DController extends Model<Hydrus1DInput> {
	
	private final String dataPath, h1dExe;
	private long timeout;
	private Process h1dProcess;
	private Timer h1dTimer;

	private static final long serialVersionUID = 3012972185334340279L;
	
	public Hydrus1DController(String dataPath) {
		this.dataPath = dataPath;
		h1dExe = Settings.getString("Hydrus1DController.path");
		timeout = Long.valueOf(Settings.getString("Hydrus1DController.timeout")) * 1000;
	}
	
	public void init() throws IOException {
		File h1dFile = new File(h1dExe);
		if (!h1dFile.canExecute()) throw new FileNotFoundException(
				h1dExe + ": Not an executable file.");
		setInput(new Hydrus1DInput(dataPath));
	}	
	
	public ModelOutput call() throws IOException, InterruptedException {
		/*
		String cmd[] = {
			"c:\\windows\\system32\\cmd.exe",
			"/C",
			"cd " + dataPath +
			" && echo | \"" + h1dExe + "\" \"" + dataPath + 
			"\" 1> h1d.stdout 2> h1d.stderr"
		};
		h1dProcess = Runtime.getRuntime().exec(cmd); //*/
		ProcessBuilder pb = new ProcessBuilder(h1dExe, dataPath);
		pb.directory(new File(dataPath));
		pb.redirectErrorStream(true);
		h1dProcess = pb.start();
		
		KillHydrus1D killer = new KillHydrus1D(h1dProcess);
		h1dTimer = new Timer("Hydrus1DTimer");
		h1dTimer.schedule(killer, timeout);

		//*
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(h1dProcess.getOutputStream()));
		w.newLine();
		w.flush();
		// We have to pick up all the output or the thing won't exit, even if we write a newline.
		BufferedReader r = new BufferedReader(new InputStreamReader(h1dProcess.getInputStream()));
		while (r.readLine() != null);
		r.close();//*/
		w.close();//*/
		
		h1dProcess.waitFor();
		if (killer.killed()) return new EmptyModelOutput();
		h1dTimer.cancel();
		return new Hydrus1DOutput(dataPath);
	}

}
