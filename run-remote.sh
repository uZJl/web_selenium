#!/bin/bash

# Selenium Grid 分布式测试执行脚本
# 使用方法: ./run-remote.sh <grid_url> [browser] [parallel]

GRID_URL="${1:-http://localhost:4444/wd/hub}"
BROWSER="${2:-chrome}"
PARALLEL="${3:-false}"

echo "=========================================="
echo "Selenium Grid 分布式测试执行"
echo "=========================================="
echo "Grid URL: $GRID_URL"
echo "Browser: $BROWSER"
echo "Parallel: $PARALLEL"
echo "=========================================="

# 检查 Grid 是否可用
echo "检查 Grid 连接..."
curl -s "$GRID_URL" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    # 尝试 status 端点
    STATUS_URL=$(echo "$GRID_URL" | sed 's/\/wd\/hub$/\/status/')
    curl -s "$STATUS_URL" > /dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo "警告: 无法连接到 Grid: $GRID_URL"
        echo "请确认 Grid 服务已启动"
        read -p "是否继续执行? (y/n): " continue
        if [ "$continue" != "y" ]; then
            exit 1
        fi
    fi
fi
echo "Grid 连接正常"

# 执行测试
if [ "$PARALLEL" = "true" ]; then
    echo "执行并行测试..."
    mvn clean test \
        -Dgrid.enabled=true \
        -Dgrid.url="$GRID_URL" \
        -Dbrowser="$BROWSER" \
        -DsuiteXmlFile=src/test/resources/testng-parallel.xml
else
    echo "执行顺序测试..."
    mvn clean test \
        -Dgrid.enabled=true \
        -Dgrid.url="$GRID_URL" \
        -Dbrowser="$BROWSER"
fi

# 生成报告
echo "=========================================="
echo "测试执行完成！"
echo "生成 Allure 报告..."
echo "运行 'mvn allure:serve' 查看报告"
echo "=========================================="
