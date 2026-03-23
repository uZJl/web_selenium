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
 * Google 搜索测试示例
 */
@Feature("Google Search")
public class GoogleSearchTest extends BaseTest {

    @Test(description = "Google 搜索功能测试")
    @Description("验证 Google 搜索功能能够正常工作")
    @Severity(SeverityLevel.CRITICAL)
    public void testGoogleSearch() {
        // 打开 Google 首页
        openPage("https://www.google.com");

        // 等待搜索框可见
        WebElement searchBox = WaitUtils.waitForElementVisible(driver, By.name("q"));

        // 输入搜索关键词
        searchBox.sendKeys("Selenium WebDriver");

        // 提交搜索
        searchBox.submit();

        // 等待搜索结果
        WaitUtils.waitForTitleContains(driver, "Selenium WebDriver");

        // 验证页面标题包含搜索关键词
        Assert.assertTrue(getPageTitle().contains("Selenium WebDriver"),
                "Page title should contain search keyword");
    }
}
