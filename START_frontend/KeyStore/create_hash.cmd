@echo off
setlocal
set KEY_TOOL=%JAVA_HOME%\bin\keytool.exe
set OPENSSL_TOOL=C:\openssl-0.9.8\bin\openssl.exe
set KEYSTORE=START.keystore
set ALIAS=START
set STOREPASS=XD4ynn4XJ4Hs3GXx
set KEYPASS=Jytw5JAd1Rtqt3xX

"%KEY_TOOL%" -exportcert -alias %ALIAS% androiddebugkey -keystore %KEYSTORE% | %OPENSSL_TOOL% sha1 -binary | %OPENSSL_TOOL% base64
