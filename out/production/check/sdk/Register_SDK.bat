if "%PROCESSOR_ARCHITECTURE%"=="x86" goto x86
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" goto x64
exit
:x64
cd /d %~dp0
copy .\*.dll %windir%\SysWOW64\
regsvr32 %windir%\SysWOW64\zkemkeeper.dll
exit
:x86
cd /d %~dp0
copy .\*.dll %windir%\system32\
regsvr32 %windir%\system32\zkemkeeper.dll
