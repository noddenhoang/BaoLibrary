@echo off
echo Installing dependencies for Library Management System Frontend...
npm install

echo.
echo Installation complete!
echo.
echo To start the development server, run:
echo npm run dev
echo.
echo Press any key to start the development server...
pause > nul
npm run dev 