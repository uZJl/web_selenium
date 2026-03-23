package org.zhengjiale.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.zhengjiale.pages.BaiduHomePage;
import org.zhengjiale.pages.BaiduSearchResultPage;

/**
 * 百度搜索测试示例
 */
@Feature("Baidu Search")
public class BaiduSearchTest extends BaseTest {

    @Test(description = "百度首页加载测试")
    @Description("验证百度首页能够正常加载")
    @Severity(SeverityLevel.BLOCKER)
    public void testHomePageLoad() {
        BaiduHomePage homePage = new BaiduHomePage(driver);

        // 打开百度首页
        homePage.open();

        // 验证 Logo 显示
        Assert.assertTrue(homePage.isLogoDisplayed(), "Baidu logo should be displayed");

        // 验证页面标题
        Assert.assertTrue(getPageTitle().contains("百度"), "Page title should contain '百度'");
    }

    @Test(description = "百度搜索功能测试")
    @Description("验证百度搜索功能能够正常工作")
    @Severity(SeverityLevel.CRITICAL)
    public void testSearchFunction() {
        String keyword = "Selenium";

        BaiduHomePage homePage = new BaiduHomePage(driver);
        homePage.open();

        // 执行搜索
        BaiduSearchResultPage resultPage = homePage.search(keyword);

        // 验证搜索结果
        Assert.assertTrue(resultPage.getResultsCount() > 0, "Search results should not be empty");
        Assert.assertTrue(resultPage.resultsContain(keyword), "Search results should contain keyword");
    }

    @Test(description = "百度搜索结果验证测试")
    @Description("验证搜索结果的标题包含搜索关键词")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchResultTitle() {
        String keyword = "Java";

        BaiduHomePage homePage = new BaiduHomePage(driver);
        homePage.open();

        BaiduSearchResultPage resultPage = homePage.search(keyword);

        // 验证第一个搜索结果的标题
        String firstTitle = resultPage.getFirstResultTitle();
        Assert.assertNotNull(firstTitle, "First result title should not be null");
        Assert.assertTrue(firstTitle.toLowerCase().contains(keyword.toLowerCase()),
                "First result title should contain keyword");
    }
}
