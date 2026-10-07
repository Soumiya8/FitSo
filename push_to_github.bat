@echo off
echo =========================================
echo   Pushing FitSo to GitHub
echo =========================================

echo.
echo Initializing Git repository...
git init

echo.
echo Setting remote to https://github.com/Soumiya8/FitSo.git...
git remote add origin https://github.com/Soumiya8/FitSo.git

echo.
echo Adding files...
git add .

echo.
echo Committing files...
git commit -m "Initial commit: Complete FitSo UI rebuild and gamification features"

echo.
echo Forcing push to GitHub (main branch) to override conflicts...
git branch -M main
git push -u origin main --force

echo.
echo =========================================
echo   Done!
echo =========================================
pause
