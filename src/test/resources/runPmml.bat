@echo off
setlocal enabledelayedexpansion
for %%i in (*.jar) do  set CLASSPATH=!CLASSPATH!;%%i

java -cp %classpath%  com.pingan.bank.jpmml.ui.PmmlCalcSwing
