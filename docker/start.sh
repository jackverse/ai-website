#!/bin/bash

# AI Website Docker 启动脚本

echo "========================================"
echo "  AI Website Docker 启动脚本"
echo "========================================"

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未运行，请先启动 Docker Desktop"
    exit 1
fi

echo "✅ Docker 已运行"

# 进入当前目录
cd "$(dirname "$0")"

# 启动容器
echo ""
echo "🚀 正在启动 MySQL 和 Redis..."
docker-compose up -d

# 等待 MySQL 启动
echo ""
echo "⏳ 等待 MySQL 启动..."
sleep 10

# 检查容器状态
echo ""
echo "📦 容器状态:"
docker-compose ps

echo ""
echo "========================================"
echo "  启动完成！"
echo "========================================"
echo ""
echo "MySQL: localhost:3306"
echo "  - Root 密码: 123456"
echo "  - 数据库: renren_security"
echo "  - 用户: renren / 123456"
echo ""
echo "Redis: localhost:6379"
echo ""
echo "停止命令: docker-compose down"
echo "========================================"