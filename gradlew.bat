@echo off
set APP_HOME=%~dp0
if exist "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" (
  java -classpath "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
  exit /b %errorlevel%
)
echo Gradle wrapper JAR is missing. Install Gradle 8.13 and run: gradle wrapper --gradle-version 8.13
exit /b 1
