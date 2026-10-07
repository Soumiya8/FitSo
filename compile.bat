@echo off
echo =========================================
echo   FitSo - Compiling...
echo =========================================

if not exist bin mkdir bin

javac -d bin -cp "lib\*" ^
  src\Main.java ^
  src\model\*.java ^
  src\dao\*.java ^
  src\service\*.java ^
  src\util\*.java ^
  src\ui\components\*.java ^
  src\ui\panels\*.java ^
  src\ui\*.java

if %errorlevel% == 0 (
    echo.
    echo =========================================
    echo   BUILD SUCCESS! Run with: run.bat
    echo =========================================
) else (
    echo.
    echo =========================================
    echo   BUILD FAILED. See errors above.
    echo =========================================
)
pause
