@echo off
for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk*") do set "JAVA_HOME=%%~fd"
if not defined JAVA_HOME for /d %%d in ("C:\Program Files\Java\jdk*") do set "JAVA_HOME=%%~fd"
set "PATH=%JAVA_HOME%\bin;%PATH%"
javac -cp lib/isode-x400.jar Inspect.java
java -cp ".;lib/isode-x400.jar" Inspect
