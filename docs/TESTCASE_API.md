# 测试用例管理 API 文档

## 概述

通过 MySQL 数据库存储测试用例，支持动态创建、执行测试用例，无需编写代码。

## API 端点

### 测试用例管理

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/testcase` | POST | 创建测试用例 |
| `/api/testcase` | GET | 获取所有用例 |
| `/api/testcase/{id}` | GET | 获取单个用例 |
| `/api/testcase/{id}` | PUT | 更新用例 |
| `/api/testcase/{id}` | DELETE | 删除用例 |
| `/api/testcase/{id}/toggle` | PATCH | 启用/禁用用例 |
| `/api/testcase/{id}/run` | POST | 执行用例 |
| `/api/testcase/status/{taskId}` | GET | 查询执行状态 |
| `/api/testcase/{id}/history` | GET | 获取执行历史 |

---

## 测试步骤格式

### 支持的操作类型

| Action | 说明 | 必需参数 | 可选参数 |
|--------|------|----------|----------|
| `open` | 打开URL | - | `value` (URL) |
| `input` / `type` | 输入文本 | `locator`, `value` | - |
| `click` | 点击元素 | `locator` | - |
| `select` | 下拉选择 | `locator`, `value` | - |
| `wait` | 固定等待 | - | `timeout` (秒) |
| `wait_for` | 等待元素出现 | `locator` | `timeout` |
| `assert` | 断言元素状态 | `locator`, `condition` | `timeout` |
| `assert_text` | 断言文本内容 | `locator`, `value` | - |
| `script` | 执行JS | `value` (JS代码) | - |
| `screenshot` | 截图 | - | - |
| `refresh` | 刷新页面 | - | - |
| `back` | 后退 | - | - |
| `forward` | 前进 | - | - |

### 定位器格式

| 格式 | 示例 | 说明 |
|------|------|------|
| `id=xxx` | `id=username` | ID选择器 |
| `name=xxx` | `name=email` | name属性 |
| `css=xxx` | `css=.btn-primary` | CSS选择器 |
| `#xxx` | `#login-btn` | CSS ID简写 |
| `.xxx` | `.container` | CSS class简写 |
| `xpath=xxx` | `xpath=//div[@class="test"]` | XPath |
| `//xxx` | `//button[text()="登录"]` | XPath简写 |
| `class=xxx` | `class=btn` | class属性 |
| `tag=xxx` | `tag=input` | 标签名 |
| `link=xxx` | `link=点击这里` | 链接文本 |
| `partial=xxx` | `partial=点击` | 部分链接文本 |

### 断言条件

| Condition | 说明 |
|-----------|------|
| `visible` / `displayed` | 元素可见 |
| `hidden` / `not_displayed` | 元素隐藏 |
| `present` / `exist` | 元素存在 |
| `clickable` | 元素可点击 |
| `selected` | 元素被选中 |

---

## 使用示例

### 1. 创建测试用例

```bash
curl -X POST http://106.55.57.182:8080/api/testcase \
  -H "Content-Type: application/json" \
  -d '{
    "name": "百度搜索测试",
    "description": "验证百度搜索功能",
    "browser": "chrome",
    "url": "https://www.baidu.com",
    "steps": [
      {"action": "open", "description": "打开百度首页"},
      {"action": "input", "locator": "#kw", "value": "Selenium", "description": "输入搜索关键词"},
      {"action": "click", "locator": "#su", "description": "点击搜索按钮"},
      {"action": "assert", "locator": ".result", "condition": "visible", "timeout": 10, "description": "验证搜索结果"}
    ],
    "enabled": true
  }'
```

### 2. 获取所有测试用例

```bash
curl http://106.55.57.182:8080/api/testcase
```

### 3. 执行测试用例

```bash
curl -X POST http://106.55.57.182:8080/api/testcase/1/run
```

### 4. 查询执行状态

```bash
curl http://106.55.57.182:8080/api/testcase/status/{taskId}
```

### 5. 更新测试用例

```bash
curl -X PUT http://106.55.57.182:8080/api/testcase/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "百度搜索测试-更新版",
    "description": "验证百度搜索功能",
    "browser": "chrome",
    "url": "https://www.baidu.com",
    "steps": [
      {"action": "open"},
      {"action": "input", "locator": "#kw", "value": "Java"},
      {"action": "click", "locator": "#su"},
      {"action": "assert", "locator": ".result", "condition": "visible"}
    ],
    "enabled": true
  }'
```

### 6. 删除测试用例

```bash
curl -X DELETE http://106.55.57.182:8080/api/testcase/1
```

---

## 完整示例：创建并执行一个测试用例

```bash
# 1. 创建测试用例
RESPONSE=$(curl -s -X POST http://106.55.57.182:8080/api/testcase \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bing搜索Selenium",
    "browser": "chrome",
    "url": "https://www.bing.com",
    "steps": [
      {"action": "open"},
      {"action": "input", "locator": "#sb_form_q", "value": "Selenium"},
      {"action": "click", "locator": "#search_icon > svg"},
      {"action": "assert", "locator": ".b_algo", "condition": "visible", "timeout": 10}
    ]
  }')

echo "创建结果: $RESPONSE"

# 2. 获取用例ID
TESTCASE_ID=$(echo $RESPONSE | jq -r '.data.id')
echo "用例ID: $TESTCASE_ID"

# 3. 执行测试
TASK_RESPONSE=$(curl -s -X POST http://106.55.57.182:8080/api/testcase/$TESTCASE_ID/run)
TASK_ID=$(echo $TASK_RESPONSE | jq -r '.data')
echo "任务ID: $TASK_ID"

# 4. 等待并查询结果
sleep 15
curl -s http://106.55.57.182:8080/api/testcase/status/$TASK_ID | jq .
```

---

## 数据库部署

### Docker 部署 MySQL

```bash
docker run -d \
  --name selenium-mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=selenium_test \
  -p 3306:3306 \
  mysql:8.0
```

### 环境变量配置

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_HOST` | localhost | 数据库主机 |
| `DB_PORT` | 3306 | 数据库端口 |
| `DB_NAME` | selenium_test | 数据库名 |
| `DB_USER` | root | 用户名 |
| `DB_PASS` | root | 密码 |
| `GRID_URL` | http://localhost:4444/wd/hub | Selenium Grid 地址 |
