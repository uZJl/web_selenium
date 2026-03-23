package org.zhengjiale.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * 百度搜索结果页 Page Object 示例
 */
public class BaiduSearchResultPage extends BasePage {
    // 元素定位器
    private final By searchResults = By.cssSelector("#content_left .result");
    private final By firstResultTitle = By.cssSelector("#content_left .result:first-child h3 a");

    public BaiduSearchResultPage(WebDriver driver) {
        super(driver);
    }

    /**
     * 获取搜索结果数量
     */
    @Step("Get search results count")
    public int getResultsCount() {
        List<WebElement> results = driver.findElements(searchResults);
        return results.size();
    }

    /**
     * 获取第一个搜索结果的标题
     */
    @Step("Get first result title")
    public String getFirstResultTitle() {
        return getText(firstResultTitle);
    }

    /**
     * 点击第一个搜索结果
     */
    @Step("Click first result")
    public void clickFirstResult() {
        click(firstResultTitle);
    }

    /**
     * 检查搜索结果是否包含指定文本
     */
    @Step("Check if results contain: {text}")
    public boolean resultsContain(String text) {
        List<WebElement> results = driver.findElements(searchResults);
        return results.stream()
                .anyMatch(result -> result.getText().toLowerCase().contains(text.toLowerCase()));
    }
}
