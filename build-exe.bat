@echo off
REM ============================================================
REM  Genera el instalador .exe de Parfums House
REM  Requisitos: JDK 21 (JAVA_HOME) y WiX Toolset 3.x en el PATH
REM  Uso: doble clic o ejecutar build-exe.bat en la carpeta del proyecto
REM ============================================================
setlocal
cd /d "%~dp0"

where candle.exe >nul 2>nul || (echo [ERROR] No se encontro WiX Toolset ^(candle.exe^). Instale WiX 3.x y reinicie la terminal. & exit /b 1)
where jpackage >nul 2>nul || (echo [ERROR] No se encontro jpackage. Use un JDK 21. & exit /b 1)

echo [1/3] Compilando con Maven...
call mvnw.cmd clean package -DskipTests || (echo [ERROR] Fallo Maven & exit /b 1)

echo [2/3] Preparando carpeta de entrada...
copy /y target\perfumeria.jar target\libs\ >nul

echo [3/3] Generando instalador .exe...
if exist dist rmdir /s /q dist
jpackage ^
  --type exe ^
  --name "ParfumsHouse" ^
  --app-version 1.0.0 ^
  --vendor "Parfums House" ^
  --description "Sistema de Perfumeria" ^
  --input target\libs ^
  --main-jar perfumeria.jar ^
  --main-class pe.edu.upeu.perfumeria.Launcher ^
  --icon src\main\resources\pe\edu\upeu\perfumeria\img\logo.ico ^
  --add-modules java.base,java.sql,java.desktop,java.naming,java.xml,jdk.unsupported ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --win-upgrade-uuid eaf6464a-7ddf-4c50-a63a-f3fc62534b2d ^
  --win-menu --win-shortcut --win-dir-chooser --win-per-user-install ^
  --dest dist || (echo [ERROR] Fallo jpackage & exit /b 1)

echo.
echo Listo. Instalador en: dist\ParfumsHouse-1.0.0.exe
endlocal
