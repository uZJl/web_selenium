package org.zhengjiale.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.zhengjiale.pages.GitHubHomePage;
import org.zhengjiale.pages.GitHubSearchResultPage;

/**
 * GitHub 功能测试
 */
@Feature("GitHub")
public class GitHubTest extends BaseTest {

    @Test(description = "GitHub 首页加载测试")
    @Description("验证 GitHub 首页能够正常加载")
    @Severity(SeverityLevel.BLOCKER)
    public void testHomePageLoad() {
        GitHubHomePage homePage = new GitHubHomePage(driver);

        // 打开 GitHub 首页
        homePage.open();

        // 验证 Logo 显示
        Assert.assertTrue(homePage.isLogoDisplayed(), "GitHub logo should be displayed");

        // 验证页面标题
        Assert.assertTrue(getPageTitle().contains("GitHub"),
                "Page title should contain 'GitHub'");
    }

    @Test(description = "GitHub 搜索仓库测试")
    @Description("验证 GitHub 搜索仓库功能")
    @Severity(SeverityLevel.CRITICAL)
    public void testSearchRepositories() {
        String keyword = "selenium java";

        GitHubHomePage homePage = new GitHubHomePage(driver);
        homePage.open();

        // 执行搜索
        GitHubSearchResultPage resultPage = homePage.search(keyword);

        // 验证搜索结果
        Assert.assertTrue(resultPage.getResultsCount() > 0,
                "Search results should not be empty");
    }

    @Test(description = "GitHub 搜索热门项目测试")
    @Description("验证 GitHub 搜索热门项目")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchPopularProject() {
        String keyword = "spring boot";

        GitHubHomePage homePage = new GitHubHomePage(driver);
        homePage.open();

        GitHubSearchResultPage resultPage = homePage.search(keyword);

        // 验证搜索结果存在
        Assert.assertTrue(resultPage.getResultsCount() > 0,
                "Search results should exist for popular project");
    }
}
