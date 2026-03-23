# Selenium Grid 分布式 Web UI 自动化框架

基于 Selenium Grid 的分布式 Web UI 自动化测试框架，支持 TestNG 并行测试、Docker 部署、Allure 报告、反检测机制。

## 核心特性

- **分布式执行** - Docker Compose 一键部署 Selenium Grid 集群
- **多浏览器支持** - Chrome / Firefox / Edge
- **反检测机制** - 绕过网站自动化检测
- **并行测试** - TestNG 并行 + ThreadLocal 线程安全
- **失败重试** - 自动重试 + 截图
- **Allure 报告** - 详细测试报告
- **Page Object** - 封装页面元素和操作
- **YAML 配置** - 灵活的配置管理
- **动态测试用例** - 通过 API 创建测试用例，MySQL 存储，无需编写代码

## 项目结构

```
web_selenium/
├── config/
│   └── config.yaml              # 测试配置文件
├── src/
│   ├── main/java/org/zhengjiale/
│   │   ├── config/              # 配置管理
│   │   │   ├── ConfigManager.java
│   │   │   └── TestConfig.java
│   │   ├── driver/              # WebDriver 工厂（含反检测）
│   │   │   ├── DriverFactory.java
│   │   │   └── LocalDriverFactory.java
│   │   ├── pages/               # Page Object 基类
│   │   │   └── BasePage.java
│   │   └── utils/               # 工具类
│   │       ├── ScreenshotUtils.java
│   │       ├── WaitUtils.java
│   │       └── GridUtils.java
│   └── test/
│       ├── java/org/zhengjiale/
│       │   ├── pages/           # Page Object 实现
│       │   │   ├── BingHomePage.java
│       │   │   ├── BingSearchResultPage.java
│       │   │   ├── GitHubHomePage.java
│       │   │   ├── GitHubSearchResultPage.java
│       │   │   ├── BaiduHomePage.java
│       │   │   └── BaiduSearchResultPage.java
│       │   ├── tests/           # 测试用例
│       │   │   ├── BaseTest.java
│       │   │   ├── SeleniumDevTest.java
│       │   │   ├── BingSearchTest.java
│       │   │   ├── GitHubTest.java
│       │   │   └── BaiduSearchTest.java
│       │   └── listener/        # TestNG 监听器
│       │       ├── TestListener.java
│       │       ├── RetryListener.java
│       │       └── RetryAnnotationTransformer.java
│       └── resources/
│           ├── config.yaml      # 测试配置
│           ├── testng.xml       # 本地测试配置
│           └── testng-parallel.xml  # 分布式测试配置
├── docker-compose.yml           # 完整集群部署
├── docker-compose-standalone.yml # 单节点部署
├── docker-compose-scaling.yml   # 动态扩展部署
├── docker-compose-hub.yml       # 生产环境高可用配置
└── pom.xml
```

## 快速开始

### 1. 本地执行测试

```bash
# 安装依赖
mvn clean install

# 执行所有测试
mvn test

# 执行特定测试类
mvn test -Dtest=SeleniumDevTest

# 执行特定测试方法
mvn test -Dtest=BingSearchTest#testSearchFunction

# 指定浏览器
mvn test -Dbrowser=firefox
```

### 2. Selenium Grid 分布式执行

#### 启动 Grid

```bash
# 方式一：完整集群（Hub + 多个 Node）- 推荐
docker-compose -f docker-compose-hub.yml up -d

# 方式二：标准集群
docker-compose up -d

# 方式三：单节点模式（适合开发）
docker-compose -f docker-compose-standalone.yml up -d

# 方式四：动态扩展节点数
docker-compose -f docker-compose-scaling.yml up -d --scale chrome-node=4 --scale firefox-node=2
```

#### 访问 Grid 控制台

- **Grid Console**: http://localhost:4444/ui
- **noVNC (Standalone)**: http://localhost:7900 (密码: secret)

#### 启用 Grid 执行

修改 `config/config.yaml`:

```yaml
grid_enabled: true
grid_url: "http://localhost:4444/wd/hub"
```

#### 执行分布式测试

```bash
# 执行并行测试
mvn test -DsuiteXmlFile=src/test/resources/testng-parallel.xml
```

### 3. 远程服务器执行（重要）

框架支持连接远程 Selenium Grid 服务器执行测试，非常适合 CI/CD 场景。

#### 方式一：命令行参数（推荐）

```bash
# 连接远程 Grid 执行测试
mvn test \
    -Dgrid.enabled=true \
    -Dgrid.url="http://192.168.1.100:4444/wd/hub" \
    -Dbrowser=chrome

# 连接远程 Grid 并行执行
mvn test \
    -Dgrid.enabled=true \
    -Dgrid.url="http://192.168.1.100:4444/wd/hub" \
    -Dbrowser=chrome \
    -DsuiteXmlFile=src/test/resources/testng-parallel.xml

# 使用无头模式（服务器环境推荐）
mvn test \
    -Dgrid.enabled=true \
    -Dgrid.url="http://192.168.1.100:4444/wd/hub" \
    -Dbrowser=chrome \
    -Dheadless=true
```

#### 方式二：使用执行脚本

```bash
# 基本用法
./run-remote.sh http://192.168.1.100:4444/wd/hub

# 指定浏览器
./run-remote.sh http://192.168.1.100:4444/wd/hub chrome

# 并行执行
./run-remote.sh http://192.168.1.100:4444/wd/hub chrome true
```

#### 方式三：修改配置文件

修改 `config/config.yaml`:

```yaml
grid_enabled: true
grid_url: "http://你的服务器IP:4444/wd/hub"
browser: chrome
headless: true  # 服务器环境建议开启
```

#### 支持的命令行参数

| 参数 | 说明 | 示例 |
|------|------|------|
| `-Dgrid.enabled` | 是否启用 Grid | `-Dgrid.enabled=true` |
| `-Dgrid.url` | Grid 服务地址 | `-Dgrid.url=http://192.168.1.100:4444/wd/hub` |
| `-Dbrowser` | 浏览器类型 | `-Dbrowser=firefox` |
| `-Dheadless` | 无头模式 | `-Dheadless=true` |
| `-Dimplicit.wait` | 隐式等待(秒) | `-Dimplicit.wait=15` |
| `-Dpage.load.timeout` | 页面加载超时 | `-Dpage.load.timeout=60` |
| `-Dretry.count` | 失败重试次数 | `-Dretry.count=3` |

#### 服务器部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                      CI/CD 服务器                            │
│  (Jenkins / GitLab CI / GitHub Actions)                     │
│                                                             │
│  ┌─────────────────┐                                        │
│  │   执行测试命令   │                                        │
│  │   mvn test ...  │                                        │
│  └────────┬────────┘                                        │
│           │                                                 │
└───────────┼─────────────────────────────────────────────────┘
            │
            │ HTTP 请求
            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Selenium Grid 服务器                        │
│                  (192.168.1.100:4444)                       │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                    Hub                               │   │
│  │              (调度和分发测试任务)                      │   │
│  └─────────────────────────────────────────────────────┘   │
│           │            │            │                       │
│     ┌─────┴─────┐ ┌────┴────┐ ┌────┴────┐                 │
│     │Chrome Node│ │Firefox  │ │Edge Node│                 │
│     │   (x3)    │ │ Node    │ │  (x1)   │                 │
│     └───────────┘ └─────────┘ └─────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

#### CI/CD 集成示例

**Jenkins Pipeline:**

```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh '''
                    mvn clean test \
                        -Dgrid.enabled=true \
                        -Dgrid.url=http://selenium-grid:4444/wd/hub \
                        -Dbrowser=chrome \
                        -Dheadless=true
                '''
            }
        }
        stage('Report') {
            steps {
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }
    }
}
```

**GitLab CI:**

```yaml
test:
  stage: test
  script:
    - mvn clean test
      -Dgrid.enabled=true
      -Dgrid.url=http://selenium-grid:4444/wd/hub
      -Dbrowser=chrome
      -Dheadless=true
  artifacts:
    paths:
      - target/allure-results/
```

### 3. 生成 Allure 报告

```bash
# 生成并打开报告
mvn allure:serve

# 或直接打开
allure serve target/allure-results
```

## 配置说明

### config.yaml

| 参数 | 说明 | 默认值 |
|------|------|--------|
| grid_enabled | 是否启用 Grid | false |
| grid_url | Grid Hub 地址 | http://localhost:4444/wd/hub |
| browser | 默认浏览器 | chrome |
| headless | 无头模式 | false |
| implicit_wait | 隐式等待(秒) | 10 |
| page_load_timeout | 页面加载超时(秒) | 30 |
| retry_count | 失败重试次数 | 2 |
| screenshot_on_failure | 失败截图 | true |

### Docker Compose 配置

| 文件 | 说明 | 适用场景 |
|------|------|----------|
| docker-compose-hub.yml | 高可用集群 | **生产环境（推荐）** |
| docker-compose.yml | 标准集群 | 测试环境 |
| docker-compose-standalone.yml | 单节点 | 开发调试 |
| docker-compose-scaling.yml | 动态扩展 | 灵活扩容 |

## 反检测机制

框架内置了多种反检测配置，绕过网站的自动化检测：

```java
// Chrome 反检测配置
options.addArguments("--disable-blink-features=AutomationControlled");
options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
options.setExperimentalOption("useAutomationExtension", false);

// 设置正常 User-Agent
options.addArguments("--user-agent=Mozilla/5.0 ...");

// Firefox 反检测配置
options.addPreference("dom.webdriver.enabled", false);
options.addPreference("useAutomationExtension", false);
```

## Page Object 模式

### 创建 Page Object

```java
public class ExamplePage extends BasePage {
    private final By searchInput = By.id("search");
    private final By searchButton = By.id("btn");

    public ExamplePage(WebDriver driver) {
        super(driver);
    }

    @Step("Search for: {keyword}")
    public ExamplePage search(String keyword) {
        sendKeys(searchInput, keyword);
        click(searchButton);
        return this;
    }
}
```

### 创建测试用例

```java
@Feature("Example Feature")
public class ExampleTest extends BaseTest {

    @Test(description = "Example test")
    @Description("Test description")
    @Severity(SeverityLevel.CRITICAL)
    public void testExample() {
        ExamplePage page = new ExamplePage(driver);
        page.open().search("keyword");
        Assert.assertTrue(page.isResultDisplayed());
    }
}
```

## Grid 工具类

```java
// 检查 Grid 是否可用
boolean available = GridUtils.isGridAvailable("http://localhost:4444/wd/hub");

// 等待 Grid 就绪
GridUtils.waitForGrid("http://localhost:4444/wd/hub", 60);

// 获取 Grid 信息
String info = GridUtils.getGridInfo("http://localhost:4444/wd/hub");
```

## 常用命令

```bash
# 清理并编译
mvn clean compile

# 执行特定测试类
mvn test -Dtest=BingSearchTest

# 执行特定测试方法
mvn test -Dtest=BingSearchTest#testSearchFunction

# 指定浏览器
mvn test -Dbrowser=firefox

# 并行执行（Grid）
mvn test -DsuiteXmlFile=src/test/resources/testng-parallel.xml

# 查看 Grid 状态
curl http://localhost:4444/status

# 查看 Grid 节点信息
docker-compose -f docker-compose-hub.yml ps

# 停止 Grid
docker-compose down
```

## 扩展节点

```bash
# 动态添加 Chrome 节点
docker-compose -f docker-compose-scaling.yml up -d --scale chrome-node=6

# 动态添加 Firefox 节点
docker-compose -f docker-compose-scaling.yml up -d --scale firefox-node=4

# 查看节点状态
docker-compose -f docker-compose-scaling.yml ps
```

## 查看测试截图

测试失败时会自动截图保存到 `screenshots/` 目录：

```bash
ls -la screenshots/
```

## REST API 服务部署（推荐）

将整个测试服务部署到服务器，通过 HTTP API 触发测试执行。

### 架构

```
┌─────────────────────────────────────────────────────────┐
│                    云服务器 (8080/4444)                   │
│                                                         │
│  ┌─────────────────┐      ┌─────────────────────────┐  │
│  │  REST API 服务   │      │     Selenium Grid       │  │
│  │  (Spring Boot)  │─────▶│  (Hub + Nodes)          │  │
│  │  :8080          │      │  :4444                  │  │
│  └─────────────────┘      └─────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
         ▲
         │ HTTP API 调用
┌────────┴────────┐
│   本地/CI/CD    │
│   curl/Jenkins  │
└─────────────────┘
```

### 一键部署

```bash
# 克隆项目到服务器
git clone <repo-url>
cd web_selenium

# 一键启动 API 服务 + Selenium Grid
docker-compose -f docker-compose-api.yml up -d --build

# 查看服务状态
docker-compose -f docker-compose-api.yml ps

# 查看日志
docker-compose -f docker-compose-api.yml logs -f test-api
```

### API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/test/execute` | POST | 执行测试 |
| `/api/test/status/{id}` | GET | 查询测试状态 |
| `/api/test/tasks` | GET | 获取所有任务 |
| `/api/test/{id}` | DELETE | 取消/删除任务 |
| `/api/test/health` | GET | 健康检查 |

### API 调用示例

```bash
# 健康检查
curl http://服务器IP:8080/api/test/health

# 执行测试
curl -X POST http://服务器IP:8080/api/test/execute \
  -H "Content-Type: application/json" \
  -d '{
    "testClass": "BingSearchTest",
    "browser": "chrome",
    "headless": true
  }'

# 执行指定测试方法
curl -X POST http://服务器IP:8080/api/test/execute \
  -H "Content-Type: application/json" \
  -d '{
    "testClass": "BingSearchTest",
    "testMethod": "testSearchFunction",
    "browser": "chrome"
  }'

# 执行所有测试（并行）
curl -X POST http://服务器IP:8080/api/test/execute \
  -H "Content-Type: application/json" \
  -d '{
    "parallel": true,
    "browser": "chrome"
  }'

# 查询测试状态（返回的 taskId）
curl http://服务器IP:8080/api/test/status/abc12345

# 获取所有任务
curl http://服务器IP:8080/api/test/tasks

# 取消测试
curl -X DELETE http://服务器IP:8080/api/test/abc12345
```

### 请求参数说明

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| testClass | String | 否 | 测试类名，不指定则执行所有测试 |
| testMethod | String | 否 | 测试方法名 |
| browser | String | 否 | 浏览器类型 (chrome/firefox/edge)，默认 chrome |
| headless | Boolean | 否 | 无头模式，默认 true |
| parallel | Boolean | 否 | 并行执行，默认 false |
| retryCount | Integer | 否 | 失败重试次数，默认 2 |

### CI/CD 集成示例

**Jenkins Pipeline:**

```groovy
pipeline {
    agent any
    stages {
        stage('Run Tests') {
            steps {
                script {
                    // 触发测试
                    def response = sh(script: '''
                        curl -s -X POST http://test-server:8080/api/test/execute \
                          -H "Content-Type: application/json" \
                          -d '{"testClass":"BingSearchTest","browser":"chrome"}'
                    ''', returnStdout: true)

                    def result = readJSON text: response
                    def taskId = result.data

                    // 轮询测试状态
                    def status = "RUNNING"
                    while (status == "RUNNING" || status == "PENDING") {
                        sleep(time: 10, unit: 'SECONDS')
                        def statusResponse = sh(script: """
                            curl -s http://test-server:8080/api/test/status/${taskId}
                        """, returnStdout: true)
                        def statusResult = readJSON text: statusResponse
                        status = statusResult.data.status
                        echo "Test status: ${status}"
                    }

                    if (status != "COMPLETED") {
                        error "Tests failed or were cancelled"
                    }
                }
            }
        }
    }
}
```

### 安全组配置

在云服务器控制台添加安全组规则：

| 协议 | 端口 | 来源 | 说明 |
|------|------|------|------|
| TCP | 8080 | 0.0.0.0/0 | API 服务 |
| TCP | 4444 | 内网 | Grid Console（可选） |

### 本地开发调试

```bash
# 编译打包
mvn clean package -DskipTests

# 本地运行 API 服务
java -jar target/web_selenium-1.0-SNAPSHOT.jar

# 或使用 Maven
mvn spring-boot:run

# 访问 API
curl http://localhost:8080/api/test/health
```

## 技术栈

- **Selenium WebDriver 4.18.1** - 浏览器自动化
- **TestNG 7.9.0** - 测试框架
- **Spring Boot 2.7.18** - REST API 服务
- **MySQL 8.0** - 测试用例存储
- **WebDriverManager 5.7.0** - 驱动管理
- **Allure 2.25.0** - 测试报告
- **Log4j2** - 日志框架
- **Docker Compose** - 容器编排

## 动态测试用例管理（推荐）

通过 API 动态创建、管理、执行测试用例，用例存储在 MySQL 数据库中，无需编写代码。

### 功能特点

- **零代码创建用例** - 通过 JSON 格式定义测试步骤
- **MySQL 持久化** - 测试用例与工程分离，独立管理
- **实时执行** - 创建后立即执行，支持多种操作类型
- **执行历史追踪** - 记录每次执行结果，便于分析

### 部署 MySQL

```bash
# 启动 MySQL 容器
docker run -d --name selenium-mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=selenium_test \
  -p 3306:3306 \
  mysql:8.0

# 启动 API 服务（配置数据库连接）
export DB_HOST=172.17.0.1
export DB_NAME=selenium_test
export DB_USER=root
export DB_PASS=root123
export GRID_URL=http://localhost:4444/wd/hub

java -jar target/web_selenium-1.0-SNAPSHOT.jar
```

### 测试用例 API

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/testcase` | POST | 创建测试用例 |
| `/api/testcase` | GET | 获取所有用例 |
| `/api/testcase/{id}` | GET | 获取单个用例 |
| `/api/testcase/{id}` | PUT | 更新用例 |
| `/api/testcase/{id}` | DELETE | 删除用例 |
| `/api/testcase/{id}/run` | POST | 执行用例 |
| `/api/testcase/status/{taskId}` | GET | 查询执行状态 |
| `/api/testcase/{id}/history` | GET | 获取执行历史 |

### 支持的操作类型

| Action | 说明 | 必需参数 |
|--------|------|----------|
| `open` | 打开URL | - |
| `input` | 输入文本 | `locator`, `value` |
| `click` | 点击元素 | `locator` |
| `select` | 下拉选择 | `locator`, `value` |
| `wait` | 固定等待 | `timeout` |
| `wait_for` | 等待元素 | `locator` |
| `assert` | 断言状态 | `locator`, `condition` |
| `assert_text` | 断言文本 | `locator`, `value` |
| `script` | 执行JS | `value` |
| `screenshot` | 截图 | - |
| `refresh` | 刷新页面 | - |

### 定位器格式

| 格式 | 示例 |
|------|------|
| ID | `id=username` |
| name | `name=email` |
| CSS | `#login-btn` 或 `.btn-primary` |
| XPath | `//div[@class="test"]` |
| class | `class=btn` |
| 链接文本 | `link=点击这里` |

### 断言条件

| Condition | 说明 |
|-----------|------|
| `visible` | 元素可见 |
| `hidden` | 元素隐藏 |
| `present` | 元素存在 |
| `clickable` | 元素可点击 |

### 使用示例

```bash
# 1. 创建测试用例
curl -X POST http://服务器IP:8080/api/testcase \
  -H "Content-Type: application/json" \
  -d '{
    "name": "百度搜索测试",
    "description": "验证百度搜索功能",
    "browser": "chrome",
    "url": "https://www.baidu.com",
    "steps": [
      {"action": "open", "description": "打开百度首页"},
      {"action": "input", "locator": "#kw", "value": "Selenium\n", "description": "输入关键词并回车"},
      {"action": "wait_for", "locator": ".result", "timeout": 10, "description": "等待结果"},
      {"action": "assert", "locator": ".result", "condition": "visible", "description": "验证结果"}
    ],
    "enabled": true
  }'

# 2. 查看所有用例
curl http://服务器IP:8080/api/testcase

# 3. 执行用例
curl -X POST http://服务器IP:8080/api/testcase/1/run
# 返回: {"code":200,"data":"abc12345",...}

# 4. 查询执行状态
curl http://服务器IP:8080/api/testcase/status/abc12345

# 5. 更新用例
curl -X PUT http://服务器IP:8080/api/testcase/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"更新后的用例",...}'

# 6. 删除用例
curl -X DELETE http://服务器IP:8080/api/testcase/1
```

### 执行结果示例

```json
{
  "taskId": "abc12345",
  "status": "COMPLETED",
  "totalSteps": 4,
  "passedSteps": 4,
  "duration": 1639,
  "executedAt": "2026-03-23T19:53:45"
}
```
