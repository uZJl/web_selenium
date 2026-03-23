-- 示例测试用例数据

-- Bing 搜索测试
INSERT INTO test_cases (name, description, browser, url, steps, enabled) VALUES
('Bing搜索测试', '验证Bing搜索功能', 'chrome', 'https://www.bing.com',
 '[
   {"action": "open", "description": "打开Bing首页"},
   {"action": "input", "locator": "#sb_form_q", "value": "Selenium WebDriver", "description": "输入搜索关键词"},
   {"action": "click", "locator": "#search_icon > svg", "description": "点击搜索按钮"},
   {"action": "wait_for", "locator": ".b_algo", "timeout": 10, "description": "等待搜索结果"},
   {"action": "assert", "locator": ".b_algo", "condition": "visible", "description": "验证搜索结果显示"}
 ]', true);

-- GitHub 搜索测试
INSERT INTO test_cases (name, description, browser, url, steps, enabled) VALUES
('GitHub搜索测试', '验证GitHub搜索功能', 'chrome', 'https://github.com',
 '[
   {"action": "open", "description": "打开GitHub首页"},
   {"action": "input", "locator": "[data-testid=\"nav-search-input\"]", "value": "selenium", "description": "输入搜索关键词"},
   {"action": "click", "locator": "[data-testid=\"nav-search-input\"]", "description": "触发搜索"},
   {"action": "wait_for", "locator": "[data-testid=\"results-repo\"]", "timeout": 10, "description": "等待搜索结果"},
   {"action": "assert", "locator": "[data-testid=\"results-repo\"]", "condition": "visible", "description": "验证搜索结果显示"}
 ]', true);

-- 登录测试示例
INSERT INTO test_cases (name, description, browser, url, steps, enabled) VALUES
('登录流程测试示例', '登录功能测试（需修改实际定位器）', 'chrome', 'https://example.com/login',
 '[
   {"action": "open", "description": "打开登录页面"},
   {"action": "input", "locator": "#username", "value": "testuser", "description": "输入用户名"},
   {"action": "input", "locator": "#password", "value": "password123", "description": "输入密码"},
   {"action": "click", "locator": "#login-button", "description": "点击登录按钮"},
   {"action": "assert", "locator": ".welcome-message", "condition": "visible", "timeout": 10, "description": "验证登录成功"}
 ]', false);
