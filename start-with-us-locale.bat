@echo off
echo Starting TreadX with forced US locale...
echo.

REM Force JVM locale to US
set JAVA_OPTS=-Duser.language=en -Duser.country=US -Duser.variant= -Dfile.encoding=UTF-8 -Dsun.locale.formatasdefault=true

echo JVM Options: %JAVA_OPTS%
echo.

REM Start the application
mvn spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"

pause
