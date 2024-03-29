############################################################################
#          ModCal 0.9  -  Copyleft 2011 by Victor Matar�                   #
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
parameters of a numeric model for which only the desired output is known. This
method is also known as "Monte-Carlo" simulation.

ModCal can use SUFI2_LH_sample.exe from the SUFI2 package to generate samples of
input data, or communicate with an external calibration tool via a SOAP interface.

Note that the archive you downloaded also includes the Mule Enterprise Service
Bus (see below), which uses a different license. You can find the mule
distribution along with its license in the subdirectory called
mule-standalone-3.2.1.



2. How to install
=================

As you've probably already unpacked the archive, you just need to edit
modcal.properties to suit your setup.



3. How to use
=============

When you provide input parameter names in whichever sample generator you're
using, there are different special notations for Hydrus 1D and Colfrac
parameters.

3.1a Defining Hydrus 1D Parameter Names
---------------------------------------
The names of all variables must be suffixed with a dot followed by an integer
number >= 1. This number designates the horizon into which the parameter is to
be inserted. Horizons are counted from top to bottom.

Example:

A Variable named Alfa.2 with a value of 0.24242 will be inserted as Alfa values
in the second horizon of the Hydrus 1D input file. Then in the Hydrus 1D input
file (Selector.in)

  thr     ths    Alfa      n         Ks       l
  0.09    0.29   0.26375   1.1675    0.8425   0.5 
  0       1      0.011     1.001     0.01     0.5

would become

  thr     ths    Alfa      n         Ks       l
  0.09    0.29   0.26375   1.1675    0.8425   0.5 
  0       1      0.24242     1.001     0.01     0.5


3.1b Defining Colfrac Parameter Names
-------------------------------------
Due to the absurd syntax of the Colfrac input file, parameters cannot be
addressed by name. Instead, the parameter name encodes a row/column index. A
parameter named #23.2 will replace the value in the second column of the 23rd
line of the input file. Lines and columns are counted from 1, and each word
counts as a column, i.e. anything between line breaks or spaces. We don't
distinguish between numbers and letters, so you can create arbitrary garbage.


3.2 Provide an observed (measured) time series
----------------------------------------------
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


3.3 Running
-----------
Open a command prompt and cd to the modcal directory. Just running "modcal"
should do the job.


a) Using SUFI2

If you use SUFI2, ModCal should automatically run the model once with each
sample and tell you the estimated quality of the generated output (in relation
to your observed values) after each iteration.

After all samples have been used, ModCal will store the results in modcal.out
in the current directory (this is also customizable in modcal.properties).

The output file contains two blocks, each containing the results from all
iterations.
In the first block the rows are ordered according to the Nash-Sutcliffe
coefficient, and in the second block they're ordered by the coefficient of
determination R^2. To find out the input parameters that were used in a
specific iteration you have to look in par_val.sf2 in SUFI2.IN.


b) Using an external sampler

If you set "Modcal.sampler =" in modcal.properties, modcal will just say
"Waiting for external sampler". Now fire up your DREAM_ZS and have a suitable
startup configuration handy. See the DREAM_ZS startup file for details on the
configuration.



4. Technical details / Extensibility
====================================

4.1 Intersection/Interpolation of simulated and observed values
---------------------------------------------------------------
Normally, the times at which you measured won't exactly match the times in the
simulated series. Thus, ModCal computes a linear interpolation of the two
simulated values before and after each measurement. This interpolated value is
then matched against the measured value.


4.2 The Mule Enterprise Service Bus
-----------------------------------
At the heart of ModCal is the Mule ESB. See http://www.mulesoft.org for more
info on Mule. Essentially, Mule is a very versatile messaging framework that can
transport messages over a wide variety of protocols, including HTTP, TCP/IP,
SSL, XMPP and even e-Mail. This means that the entire calibration process is
modeled as an exchange of messages between the sampler and the model. This in
turn leads to great flexibility in terms of deployment and control flow, which
leads us to:


4.3 Deployment options
----------------------
So far this has never been tested, but in principle it should require only
minimal hacking to have each component of the calibration process run on a
different machine:

                   ------------------
                   | User Interface |
                   ------------------
                           |n
                           |
                           |
                           |n
-----------          ------------            ---------
|         |1..n     1|          |1       1..n|       |
| Sampler |----------| Mule ESB |------------| Model |
|         |          |          |            |       |
-----------          ------------            ---------

The multiplicities suggest options for:


4.4 Parallelization
-------------------
The only thing keeping you from unleashing the true power of your 64 core
compute server on your hydrogeological models is the lack of a decent sample
generator (i.e. calibration tool) that can dispatch multiple samples before
receiving any result. Extending DREAM_ZS to do that should be possible with
moderate effort (hint hint :-).


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

Technical realization by Victor Matar�.
