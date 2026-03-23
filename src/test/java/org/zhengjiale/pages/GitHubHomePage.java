package org.zhengjiale.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * GitHub 首页 Page Object
 */
public class GitHubHomePage extends BasePage {
    private static final String URL = "https://github.com";

    // 元素定位器
    private final By searchInput = By.name("q");
    private final By signUpButton = By.cssSelector("a[href='/signup']");
    private final By signInButton = By.cssSelector("a[href='/login']");
    private final By logo = By.cssSelector(".octicon-mark-github");

    public GitHubHomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * 打开 GitHub 首页
     */
    @Step("Open GitHub homepage")
    public GitHubHomePage open() {
        driver.get(URL);
        waitForTitle("GitHub");
        return this;
    }

    /**
     * 输入搜索关键词
     */
    @Step("Input search keyword: {keyword}")
    public GitHubHomePage inputSearchKeyword(String keyword) {
        sendKeys(searchInput, keyword);
        return this;
    }

    /**
     * 执行搜索（按 Enter）
     */
    @Step("Search for: {keyword}")
    public GitHubSearchResultPage search(String keyword) {
        inputSearchKeyword(keyword);
        driver.findElement(searchInput).submit();
        return new GitHubSearchResultPage(driver);
    }

    /**
     * 点击登录按钮
     */
    @Step("Click sign in button")
    public void clickSignIn() {
        click(signInButton);
    }

    /**
     * 点击注册按钮
     */
    @Step("Click sign up button")
    public void clickSignUp() {
        click(signUpButton);
    }

    /**
     * 检查 Logo 是否显示
     */
    @Step("Check if logo is displayed")
    public boolean isLogoDisplayed() {
        return isElementDisplayed(logo);
    }
}
