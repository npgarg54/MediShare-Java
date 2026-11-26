@echo off
setlocal
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
if not exist build\test-classes mkdir build\test-classes
dir /s /b src\main\java\*.java > build\main-sources.txt
javac --release 17 -Xlint:all -d build\classes @build\main-sources.txt
if errorlevel 1 exit /b 1
dir /s /b src\test\java\*.java > build\test-sources.txt
javac --release 17 -Xlint:all -cp build\classes -d build\test-classes @build\test-sources.txt
if errorlevel 1 exit /b 1
java -cp build\classes;build\test-classes com.medishare.AppSelfTest
endlocal
