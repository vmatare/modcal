@echo off
set CLASSPATH=%CLASSPATH%;mule-standalone-3.2.1\conf\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.2.1\lib\boot\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.2.1\lib\endorsed\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.2.1\lib\mule\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.2.1\lib\opt\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.2.1\lib\user\*

java org.modcal.ModCal %1 %2 %3 %4