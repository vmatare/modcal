@echo off
chcp 1252 > NUL
set CLASSPATH=%CLASSPATH%;mule-standalone-3.1.0\conf\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.1.0\lib\boot\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.1.0\lib\endorsed\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.1.0\lib\mule\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.1.0\lib\opt\*
set CLASSPATH=%CLASSPATH%;mule-standalone-3.1.0\lib\user\*

java org.modcal.ModCal %1 %2 %3 %4