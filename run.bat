@echo off
title MediCare Spring Boot & iText 7 Launcher
color 03
cd /d "%~dp0"

echo =========================================================
echo       Starting MediCare (Spring Boot 3.2 Platform)
echo =========================================================
echo.

where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [NOTICE] Maven or Java JDK 17 is not yet installed in system PATH!
    echo.
    echo To run the Spring Boot Java backend:
    echo   1. Install Java JDK 17 and Maven (or run setup_environment.bat)
    echo.
    echo Opening instant hospital frontend client in your browser right now...
    start "" "frontend\index.html"
    pause
    exit /b
)

echo Compiling and starting Spring Boot application on port 8081...
start "" "frontend\index.html"
mvn spring-boot:run
pause
