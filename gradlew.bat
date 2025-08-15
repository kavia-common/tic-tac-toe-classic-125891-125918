@echo off
REM Wrapper script to delegate Gradle commands to the Android project under tic_tac_toe_frontend.
setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%\tic_tac_toe_frontend

if not exist "%PROJECT_DIR%\gradlew.bat" (
  echo Error: gradlew.bat not found in %PROJECT_DIR% 1>&2
  exit /b 127
)

pushd "%PROJECT_DIR%"
call gradlew.bat %*
set EXIT_CODE=%ERRORLEVEL%
popd

exit /b %EXIT_CODE%
