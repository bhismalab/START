@echo off
setlocal
set KEY_TOOL=%JAVA_HOME%\bin\keytool.exe
set KEYSTORE=START.keystore
set STOREPASS=XD4ynn4XJ4Hs3GXx
set ALIAS=START
set DNAME="CN=START Release,O=reading,C=UK"
set KEYPASS=Jytw5JAd1Rtqt3xX

if not exist "%KEYSTORE%" (
	"%KEY_TOOL%" -genkey -v -keystore %KEYSTORE% -storepass %STOREPASS% -alias %ALIAS% -keyalg RSA -dname %DNAME% -validity 10000 -keypass %KEYPASS%
)
