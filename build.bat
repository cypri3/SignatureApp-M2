@echo off
REM Définition des variables
set SRC_DIR=src
set BIN_DIR=bin
set LIB_DIR=lib
set MAIN_CLASS=Projet

REM Création du dossier bin s'il n'existe pas
if not exist "%BIN_DIR%" (
    mkdir "%BIN_DIR%"
)

REM Menu pour les options
if "%1"=="compile" goto compile
if "%1"=="run" goto run
if "%1"=="clean" goto clean
if "%1"=="help" goto help

echo Usage: build.bat [compile|run|clean|help]
goto :eof

:compile
echo Compilation en cours...
javac -d "%BIN_DIR%" -cp "%LIB_DIR%\*" "%SRC_DIR%\*.java"
if %ERRORLEVEL% == 0 (
    echo Compilation terminee avec succes.
) else (
    echo Erreur lors de la compilation.
)
goto :eof

:run
call :compile
if %ERRORLEVEL% == 0 (
    echo Execution du programme...
    java -cp "%BIN_DIR%;%LIB_DIR%\*" %MAIN_CLASS%
) else (
    echo Le programme ne peut pas etre execute en raison d'erreurs de compilation.
)
goto :eof

:clean
echo Suppression des fichiers compiles...
del /Q "%BIN_DIR%\*.class"
echo Nettoyage termine.
goto :eof

:help
echo Commandes disponibles :
echo   build.bat compile   - Compile le projet
echo   build.bat run       - Compile et execute le programme
echo   build.bat clean     - Supprime les fichiers compiles
echo   build.bat help      - Affiche cette aide
goto :eof
