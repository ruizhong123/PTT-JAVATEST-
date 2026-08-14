@ECHO off
SETLOCAL
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
cd /d "%DIRNAME%"
mvn spring-boot:run
ENDLOCAL