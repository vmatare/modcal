
###############################################################################
#             ModCal 0.9  -  (c) 2011 by Victor Mataré                        #
###############################################################################


CONTENTS of this file

1. About this program
2. How to install
3. How to use
4. Technical details / Extensibility
5. Support and bugs



1. About this program
=====================
ModCal stands for Model Calibration. It is used to "brute-force" the input
parameters of a numeric model for which only the desired output is known.

ModCal uses SUFI2_lh_sample.exe from the
SUFI2 package to generate samples of input data which are run through a numeric
model (at the moment, only Hydrus 1D is implemented). Each simulated result from
the model is then compared to actual values that have been observed before.
The input sample that generates the best-matching simulation output could then
be assumed to match the real conditions most closely.