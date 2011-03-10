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

import java.io.IOException;
import java.io.Serializable;


/**
 * Generates consecutive parameter samples, each for a particular simulation run.
 * @author Victor Mataré
 *
 */
public abstract class Sampler<SampleT extends ParameterSample<?>> implements Serializable {

	private static final long serialVersionUID = -7235490018581863170L;
	
	/**
	 * This is called on each simulation run and the result used as an input for the
	 * {@link Model}.
	 * @return The next {@link ParameterSample}. Return null if there are no more samples
	 * (calibration ends).
	 * @throws IOException
	 */
	public abstract SampleT nextSample() throws IOException;
	
	
	/**
	 * This method should initialize any external resources. Don't do this
	 * in the constructor to allow the object to reach its destination before
	 * reading external data (like opening files).
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void init() throws IOException, InterruptedException;
}
