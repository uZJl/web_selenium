package org.zhengjiale.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.zhengjiale.utils.WaitUtils;

/**
 * Page Object 基类 - 所有页面类都应继承此类
 */
public abstract class BasePage {
    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * 等待元素可见
     *
     * @param locator 元素定位器
     * @return WebElement
     */
    protected WebElement waitAndGetElement(By locator) {
        return WaitUtils.waitForElementVisible(driver, locator);
    }

    /**
     * 点击元素
     *
     * @param locator 元素定位器
     */
    protected void click(By locator) {
        WebElement element = WaitUtils.waitForElementClickable(driver, locator);
        element.click();
    }

    /**
     * 输入文本
     *
     * @param locator 元素定位器
     * @param text    要输入的文本
     */
    protected void sendKeys(By locator, String text) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * 获取元素文本
     *
     * @param locator 元素定位器
     * @return 元素文本
     */
    protected String getText(By locator) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator);
        return element.getText();
    }

    /**
     * 获取输入框的值
     *
     * @param locator 元素定位器
     * @return 输入框的值
     */
    protected String getValue(By locator) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator);
        return element.getAttribute("value");
    }

    /**
     * 检查元素是否显示
     *
     * @param locator 元素定位器
     * @return true 如果元素显示
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            return WaitUtils.waitForElementVisible(driver, locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查元素是否启用
     *
     * @param locator 元素定位器
     * @return true 如果元素启用
     */
    protected boolean isElementEnabled(By locator) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator);
        return element.isEnabled();
    }

    /**
     * 检查元素是否选中
     *
     * @param locator 元素定位器
     * @return true 如果元素选中
     */
    protected boolean isElementSelected(By locator) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator);
        return element.isSelected();
    }

    /**
     * 选择下拉框选项（通过可见文本）
     *
     * @param locator 元素定位器
     * @param text    选项文本
     */
    protected void selectByText(By locator, String text) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator);
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    /**
     * 选择下拉框选项（通过值）
     *
     * @param locator 元素定位器
     * @param value   选项值
     */
    protected void selectByValue(By locator, String value) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator);
        Select select = new Select(element);
        select.selectByValue(value);
    }

    /**
     * 切换到 iframe
     *
     * @param locator iframe 定位器
     */
    protected void switchToFrame(By locator) {
        WebElement frame = WaitUtils.waitForElementPresent(driver, locator);
        driver.switchTo().frame(frame);
    }

    /**
     * 切换到主文档
     */
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    /**
     * 执行 JavaScript
     *
     * @param script JavaScript 代码
     * @param args   参数
     * @return 执行结果
     */
    protected Object executeScript(String script, Object... args) {
        return ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script, args);
    }

    /**
     * 滚动到元素
     *
     * @param locator 元素定位器
     */
    protected void scrollToElement(By locator) {
        WebElement element = WaitUtils.waitForElementPresent(driver, locator);
        executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * 等待页面标题包含指定文本
     *
     * @param title 标题文本
     * @return true 如果标题包含指定文本
     */
    protected boolean waitForTitle(String title) {
        return WaitUtils.waitForTitleContains(driver, title);
    }

    /**
     * 获取当前页面 URL
     *
     * @return 页面 URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * 获取页面标题
     *
     * @return 页面标题
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
}
