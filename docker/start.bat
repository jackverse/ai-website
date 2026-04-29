@echo off
chcp 65001 >nul
echo ========================================
echo   AI Website Docker 启动脚本
echo ========================================

REM 检查 Docker 是否运行
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker 未运行，请先启动 Docker Desktop
    pause
    exit /b 1
)

echo ✅ Docker 已运行
echo.

cd /d "%~dp0"

echo 🚀 正在启动 MySQL 和 Redis...
docker-compose up -d

echo.
echo ⏳ 等待 MySQL 启动...
timeout /t 10 /nobreak >nul

echo.
echo 📦 容器状态:
docker-compose ps

echo.
echo ========================================
echo   启动完成！
echo ========================================
echo.
echo MySQL: localhost:3306
echo   - Root 密码: 123456
echo   - 数据库: renren_security
echo   - 用户: renren / 123456
echo.
echo Redis: localhost:6379
echo.
echo 停止命令: docker-compose down
echo ========================================

pause