package org.zhengjiale.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * 等待工具类 - 提供各种等待策略
 */
public class WaitUtils {
    private static final int DEFAULT_TIMEOUT = 10;
    private static final int POLLING_INTERVAL = 500;

    private WaitUtils() {
        // 私有构造函数
    }

    /**
     * 等待元素可见
     *
     * @param driver WebDriver 实例
     * @param by     元素定位器
     * @return WebElement
     */
    public static WebElement waitForElementVisible(WebDriver driver, By by) {
        return waitForElementVisible(driver, by, DEFAULT_TIMEOUT);
    }

    /**
     * 等待元素可见（指定超时时间）
     *
     * @param driver  WebDriver 实例
     * @param by      元素定位器
     * @param timeout 超时时间（秒）
     * @return WebElement
     */
    public static WebElement waitForElementVisible(WebDriver driver, By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * 等待元素可点击
     *
     * @param driver WebDriver 实例
     * @param by     元素定位器
     * @return WebElement
     */
    public static WebElement waitForElementClickable(WebDriver driver, By by) {
        return waitForElementClickable(driver, by, DEFAULT_TIMEOUT);
    }

    /**
     * 等待元素可点击（指定超时时间）
     *
     * @param driver  WebDriver 实例
     * @param by      元素定位器
     * @param timeout 超时时间（秒）
     * @return WebElement
     */
    public static WebElement waitForElementClickable(WebDriver driver, By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * 等待元素存在（DOM中）
     *
     * @param driver WebDriver 实例
     * @param by     元素定位器
     * @return WebElement
     */
    public static WebElement waitForElementPresent(WebDriver driver, By by) {
        return waitForElementPresent(driver, by, DEFAULT_TIMEOUT);
    }

    /**
     * 等待元素存在（指定超时时间）
     *
     * @param driver  WebDriver 实例
     * @param by      元素定位器
     * @param timeout 超时时间（秒）
     * @return WebElement
     */
    public static WebElement waitForElementPresent(WebDriver driver, By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * 等待元素消失
     *
     * @param driver WebDriver 实例
     * @param by     元素定位器
     * @return true 如果元素消失
     */
    public static boolean waitForElementInvisible(WebDriver driver, By by) {
        return waitForElementInvisible(driver, by, DEFAULT_TIMEOUT);
    }

    /**
     * 等待元素消失（指定超时时间）
     *
     * @param driver  WebDriver 实例
     * @param by      元素定位器
     * @param timeout 超时时间（秒）
     * @return true 如果元素消失
     */
    public static boolean waitForElementInvisible(WebDriver driver, By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    /**
     * 等待页面标题包含指定文本
     *
     * @param driver WebDriver 实例
     * @param title  标题文本
     * @return true 如果标题包含指定文本
     */
    public static boolean waitForTitleContains(WebDriver driver, String title) {
        return waitForTitleContains(driver, title, DEFAULT_TIMEOUT);
    }

    /**
     * 等待页面标题包含指定文本（指定超时时间）
     *
     * @param driver  WebDriver 实例
     * @param title   标题文本
     * @param timeout 超时时间（秒）
     * @return true 如果标题包含指定文本
     */
    public static boolean waitForTitleContains(WebDriver driver, String title, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.titleContains(title));
    }

    /**
     * Fluent Wait - 灵活的等待方式
     *
     * @param driver   WebDriver 实例
     * @param condition 等待条件
     * @param timeout  超时时间（秒）
     * @param <T>      返回类型
     * @return 条件满足时的返回值
     */
    public static <T> T fluentWait(WebDriver driver, Function<WebDriver, T> condition, int timeout) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofMillis(POLLING_INTERVAL))
                .ignoring(Exception.class);
        return wait.until(condition);
    }
}
