package org.zhengjiale.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * GitHub 搜索结果页 Page Object
 */
public class GitHubSearchResultPage extends BasePage {
    // 元素定位器
    private final By searchResults = By.cssSelector("[data-testid='results-list'] > div");
    private final By repoList = By.cssSelector(".repo-list-item");
    private final By firstRepoTitle = By.cssSelector(".repo-list-item:first-child a");

    public GitHubSearchResultPage(WebDriver driver) {
        super(driver);
    }

    /**
     * 获取搜索结果数量
     */
    @Step("Get search results count")
    public int getResultsCount() {
        List<WebElement> repos = driver.findElements(repoList);
        return repos.size();
    }

    /**
     * 获取第一个仓库标题
     */
    @Step("Get first repository title")
    public String getFirstRepoTitle() {
        try {
            return getText(firstRepoTitle);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 点击第一个仓库
     */
    @Step("Click first repository")
    public void clickFirstRepo() {
        click(firstRepoTitle);
    }
}
