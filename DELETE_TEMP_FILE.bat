@echo off
REM This script removes the temporary FocusModeController_new.java file

echo Deleting temporary file...
del "src\main\java\com\example\javafx_project\FocusModeController_new.java"

if exist "src\main\java\com\example\javafx_project\FocusModeController_new.java" (
    echo FAILED: File still exists
    exit /b 1
) else (
    echo SUCCESS: Temporary file deleted
    exit /b 0
)
