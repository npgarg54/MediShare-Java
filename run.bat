@echo off
setlocal
cd /d "%~dp0"
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found. Install Maven 3.8+ and add it to PATH.
  exit /b 1
)
mvn clean compile exec:java
endlocal
