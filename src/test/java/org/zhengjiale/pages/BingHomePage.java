package org.zhengjiale.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Bing 首页 Page Object
 */
public class BingHomePage extends BasePage {
    private static final String URL = "https://www.bing.com";

    // 元素定位器
    private final By searchInput = By.name("q");
    private final By searchButton = By.id("search_icon");
    private final By logo = By.id("bLogo");

    public BingHomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * 打开 Bing 首页
     */
    @Step("Open Bing homepage")
    public BingHomePage open() {
        driver.get(URL);
        // Bing 标题可能是 "Bing" 或本地化标题（如 "Microsoft 必应"）
        try {
            waitForTitle("Bing");
        } catch (Exception e) {
            // 尝试等待本地化标题
            waitForTitle("必应");
        }
        return this;
    }

    /**
     * 输入搜索关键词
     */
    @Step("Input search keyword: {keyword}")
    public BingHomePage inputSearchKeyword(String keyword) {
        sendKeys(searchInput, keyword);
        return this;
    }

    /**
     * 点击搜索按钮
     */
    @Step("Click search button")
    public BingSearchResultPage clickSearchButton() {
        click(searchButton);
        return new BingSearchResultPage(driver);
    }

    /**
     * 执行搜索
     */
    @Step("Search for: {keyword}")
    public BingSearchResultPage search(String keyword) {
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
