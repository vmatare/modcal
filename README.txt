############################################################################
#          ModCal 0.9  -  Copyleft 2011 by Victor Mataré                   #
#                                                                          #
#           this program is distributed under the terms                    #
#              of the GNU Public License, Version 3.                       #
#                See LICENSE.txt for full details.                         #
############################################################################


CONTENTS OF THIS FILE
=====================

0. Disclaimer
1. About this program
2. How to install
3. How to use
4. Technical details / Extensibility
5. Support and bugs
6. Credits



0. Disclaimer
=============

This program is provided to you AS-IS. The author(s) take no responsibility for
any material, economic or personal damage that may arise from using it. If this
program steals your car, pees on your carpet or shoots your dog...
tough luck - you can't sue anyone about it.

By running this program you state your agreement with these terms.



1. About this program
=====================

ModCal stands for Model Calibration. It is used to "brute-force" the input
parameters of a numeric model for which only the desired output is known.

ModCal uses SUFI2_LH_sample.exe from the SUFI2 package to generate samples of
input data which are run through a numeric model (at the moment, only Hydrus 1D
is implemented). Each simulated result from the model is then compared to
previously-measured values. The input sample that generates the best-matching
simulation output could then be assumed to match the real conditions most
closely.

Note that the archive you downloaded also includes the Mule Enterprise Service
Bus (see below), which uses a different license. You can find the mule
distribution along with its license in the subdirectory called
mule-standalone-3.1.0.



2. How to install
=================

Prerequisites:
- A working Java Runtime Environment
- SUFI2_LH_sample.exe including required input files
- Hydrus 1D

ModCal has only been tested with Java 6. You can't compile it on versions
earlier than 5 because it uses generics, but it may still be able to run.

You need to tell ModCal where it can find the SUFI2 sampler and which Hydrus 1D
executable it should use. To do this, edit the file modcal.properties and
change the variables Sufi2Sampler.path and Hydrus1DController.path to match
your setup.

Note that Sufi2Sampler.path refers to a directory, while
Hydrus1DController.path refers to the executable file which generates the model
output. Also bear in mind that you may need to change this depending on the
type of model you want to run. See the Hydrus 1D manual to learn about the
differences between the executables.



3. How to use
=============

3.1 Prepare your Hydrus 1D input
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
See the Hydrus 1D manual for how to do that. In the end you need a suitable
file called Selector.in and you need to know which Hydrus 1D executable to use
with it (as mentioned above).


3.2 Edit par_inf.sf2
^^^^^^^^^^^^^^^^^^^^
The names of all variables must be suffixed with a dot followed by an integer
number >= 1. This number designates the horizon into which the parameter is to
be inserted. Horizons are counted from top to bottom.

Example:

A line in par_inf.sf2:

 Alfa.2  0.01  0.3

Means that the values generated for Alfa.2 are inserted as Alfa values in the
second horizon of the Hydrus 1D input file. Suppose Alfa.2 gets a value of
0.24242. Then in the Hydrus 1D input file (Selector.in)

  thr     ths    Alfa      n         Ks       l
  0.09    0.29   0.26375   1.1675    0.8425   0.5 
  0       1      0.011     1.001     0.01     0.5

would become

  thr     ths    Alfa      n         Ks       l
  0.09    0.29   0.26375   1.1675    0.8425   0.5 
  0       1      0.24242     1.001     0.01     0.5


3.3 Provide an observed (measured) time series
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
This needs to be a tab-delimited table with exactly two columns. The first
column must contain the times. The interval it covers must be a sub-interval of
the simulated time series. The second column contains the observed values.

IMPORTANT: The first row must specify the parameter (column) names. The name of
the first column must be "Time", while the name of the second column determines
which parameter in the Hydrus 1D output file the observed series is matched
against.

For example, the first row

Time	sum(vBot)
0	0
10	0.5
...	...

means that ModCal will pick the column titled sum(vBot) from the Hydrus 1D
output to match it against these observed values.

NOTE: You can export any table into a tab-delimited text file from most popular
spreadsheet programs. But don't forget to delete all but the first two columns.


3.4 Make it run
^^^^^^^^^^^^^^^
Open a command prompt and cd to the modcal directory. Execute "modcal" to get
a brief help message. From here, everything should be straightforward. You
need to specify the location of your Hydrus 1D input data and the path to the
table with your observed data as commandline parameters.

ModCal should then run the model once with each sample and tell you the
estimated quality of the generated output (in relation to your observed
values) after each iteration.

After all samples have been used, ModCal will store the results in modcal.out
in the current directory (this is also customizable in modcal.properties).

The output file contains two blocks, each containing the results from all
iterations.
In the first block the rows are ordered according to the Nash-Sutcliffe
coefficient, and in the second block they're ordered by the coefficient of
determination R^2.



4. Technical details / Extensibility
====================================

4.1 Intersection/Interpolation of simulated and observed values
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Normally, the times at which you measured won't exactly match the times in the
simulated series. Thus, ModCal computes a linear interpolation of the two
simulated values before and after each measurement. This interpolated value is
then matched against the measured value.


4.2 The Mule Enterprise Service Bus
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
At the heart of ModCal is the Mule ESB. See http://www.mulesoft.org for more
info on Mule. Essentially, Mule is a very versatile messaging framework that
can transport messages over a wide variety of protocols, including HTTP,
TCP/IP, SSL, XMPP and even e-Mail. This means that the entire calibration
process is modeled as an exchange of messages between the sampler, the model
and the user interface. This in turn leads to great flexibility in terms of
deployment and control flow, which leads us to:


4.3 Deployment options
^^^^^^^^^^^^^^^^^^^^^^
So far this has never been tested, but in principle it should require only
minimal hacking to have each component of the calibration process run on a
different machine:

                   ------------------
                   | User Interface |
                   ------------------
                           |n
                           |
                           |
                           |1
-----------          ------------            ---------
|         |1        1|          |1       1..n|       |
| Sampler |----------| Mule ESB |------------| Model |
|         |          |          |            |       |
-----------          ------------            ---------

The multiplicities suggest options for:


4.4 Parallelization
^^^^^^^^^^^^^^^^^^^
Besides complete network transparency, the inversion of control principle
imposed by Mule also facilitates parallelization. The software can be easily
extended to dispatch multiple parameter samples to multiple instances of the
model in parallel. In this scenario, your use of computing power is only
limited by the messaging throughput your Mule server can handle. Note that this
is not yet implemented, but fairly easy to achieve. 


4.5 Use of other samplers or models
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ModCal operates the sampler and the model through abstract interfaces. This
means that plugging different samplers or models into ModCal is mainly a matter
of adapting them to the very simple Sampler or Model interfaces used by ModCal.



5. Support and bugs
===================

If you think you've found a defect or have a technical question regarding ModCal,
send a mail to my last name at lih dot rwth dash aachen dot de (without the
accent of course). 


6. Credits
==========
The development of ModCal was conceived and funded by Christoph Neukum of the
Chair of Engineering Geology and Hydrogeology:

http://www.lih.rwth-aachen.de

Using the Mule ESB as a control framework was suggested by Oliver Post.

Technical realization by Victor Mataré.
