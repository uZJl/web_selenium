package org.zhengjiale.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.zhengjiale.utils.WaitUtils;

/**
 * Selenium 官网测试
 */
@Feature("Selenium Official Site")
public class SeleniumDevTest extends BaseTest {

    @Test(description = "Selenium 官网首页加载测试")
    @Description("验证 Selenium 官网首页能够正常加载")
    @Severity(SeverityLevel.BLOCKER)
    public void testSeleniumHomePage() {
        // 打开 Selenium 官网
        openPage("https://www.selenium.dev");

        // 验证页面标题
        Assert.assertTrue(getPageTitle().toLowerCase().contains("selenium"),
                "Page title should contain 'selenium'");
    }

    @Test(description = "Selenium 文档导航测试")
    @Description("验证 Selenium 文档导航功能")
    @Severity(SeverityLevel.NORMAL)
    public void testDocumentationNavigation() {
        // 打开 Selenium 官网
        openPage("https://www.selenium.dev");

        // 点击文档链接
        WebElement docLink = WaitUtils.waitForElementVisible(driver,
                By.cssSelector("a[href='/documentation']"));
        docLink.click();

        // 验证跳转到文档页面
        WaitUtils.waitForTitleContains(driver, "Documentation");
        Assert.assertTrue(getCurrentUrl().contains("/documentation"),
                "Should navigate to documentation page");
    }

    @Test(description = "Selenium Downloads 页面测试")
    @Description("验证 Selenium Downloads 页面")
    @Severity(SeverityLevel.NORMAL)
    public void testDownloadsPage() {
        // 直接打开下载页面
        openPage("https://www.selenium.dev/downloads");

        // 验证页面标题
        Assert.assertTrue(getPageTitle().toLowerCase().contains("download"),
                "Page title should contain 'download'");
    }
}
