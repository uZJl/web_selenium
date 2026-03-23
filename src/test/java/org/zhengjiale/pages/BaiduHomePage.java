package org.zhengjiale.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 百度首页 Page Object 示例
 */
public class BaiduHomePage extends BasePage {
    private static final String URL = "https://www.baidu.com";

    // 元素定位器
    private final By searchInput = By.id("kw");
    private final By searchButton = By.id("su");
    private final By logo = By.id("lg");

    public BaiduHomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * 打开百度首页
     */
    @Step("Open Baidu homepage")
    public BaiduHomePage open() {
        driver.get(URL);
        waitForTitle("百度");
        return this;
    }

    /**
     * 输入搜索关键词
     */
    @Step("Input search keyword: {keyword}")
    public BaiduHomePage inputSearchKeyword(String keyword) {
        sendKeys(searchInput, keyword);
        return this;
    }

    /**
     * 点击搜索按钮
     */
    @Step("Click search button")
    public BaiduSearchResultPage clickSearchButton() {
        click(searchButton);
        return new BaiduSearchResultPage(driver);
    }

    /**
     * 执行搜索
     */
    @Step("Search for: {keyword}")
    public BaiduSearchResultPage search(String keyword) {
        inputSearchKeyword(keyword);
        return clickSearchButton();
    }

    /**
     * 检查 Logo 是否显示
     */
    @Step("Check if logo is displayed")
    public boolean isLogoDisplayed() {
        return isElementDisplayed(logo);
    }
}
