package org.zhengjiale.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.zhengjiale.pages.BingHomePage;
import org.zhengjiale.pages.BingSearchResultPage;

/**
 * Bing 搜索测试
 */
@Feature("Bing Search")
public class BingSearchTest extends BaseTest {

    @Test(description = "Bing 首页加载测试")
    @Description("验证 Bing 首页能够正常加载")
    @Severity(SeverityLevel.BLOCKER)
    public void testHomePageLoad() {
        BingHomePage homePage = new BingHomePage(driver);

        // 打开 Bing 首页
        homePage.open();

        // 验证页面标题
        Assert.assertTrue(getPageTitle().toLowerCase().contains("bing"),
                "Page title should contain 'bing'");
    }

    @Test(description = "Bing 搜索功能测试")
    @Description("验证 Bing 搜索功能能够正常工作")
    @Severity(SeverityLevel.CRITICAL)
    public void testSearchFunction() {
        String keyword = "Selenium WebDriver";

        BingHomePage homePage = new BingHomePage(driver);
        homePage.open();

        // 执行搜索
        BingSearchResultPage resultPage = homePage.search(keyword);

        // 验证搜索结果
        Assert.assertTrue(resultPage.getResultsCount() > 0, "Search results should not be empty");

        // 验证第一个搜索结果的标题
        String firstTitle = resultPage.getFirstResultTitle();
        Assert.assertNotNull(firstTitle, "First result title should not be null");
    }

    @Test(description = "Bing 搜索中文关键词测试")
    @Description("验证 Bing 搜索中文关键词")
    @Severity(SeverityLevel.NORMAL)
    public void testChineseSearch() {
        String keyword = "自动化测试";

        BingHomePage homePage = new BingHomePage(driver);
        homePage.open();

        BingSearchResultPage resultPage = homePage.search(keyword);

        // 验证搜索结果存在
        Assert.assertTrue(resultPage.getResultsCount() > 0,
                "Chinese search results should not be empty");
    }
}
