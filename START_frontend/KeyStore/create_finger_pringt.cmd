@echo off
setlocal
set KEY_TOOL=%JAVA_HOME%\bin\keytool.exe
set KEYSTORE=START.keystore
set ALIAS=START
set STOREPASS=XD4ynn4XJ4Hs3GXx
set KEYPASS=Jytw5JAd1Rtqt3xX

"%KEY_TOOL%" -exportcert -alias %ALIAS% -keystore %KEYSTORE% -storepass %STOREPASS% -list -v
