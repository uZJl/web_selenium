package org.zhengjiale.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 截图工具类
 */
public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "screenshots";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private ScreenshotUtils() {
        // 私有构造函数
    }

    /**
     * 截取屏幕截图
     *
     * @param driver WebDriver 实例
     * @param name   截图名称
     * @return 截图文件路径
     */
    public static String takeScreenshot(WebDriver driver, String name) {
        if (!(driver instanceof TakesScreenshot)) {
            logger.warn("Driver does not support screenshots");
            return null;
        }

        try {
            // 创建截图目录
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // 生成文件名
            String timestamp = DATE_FORMAT.format(new Date());
            String fileName = String.format("%s_%s.png", name, timestamp);
            String filePath = SCREENSHOT_DIR + File.separator + fileName;

            // 截图并保存
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File(filePath));

            logger.info("Screenshot saved: {}", filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("Failed to save screenshot", e);
            return null;
        }
    }

    /**
     * 截取屏幕截图（使用测试方法名）
     *
     * @param driver     WebDriver 实例
     * @param methodName 测试方法名
     * @return 截图文件路径
     */
    public static String takeScreenshotForTest(WebDriver driver, String methodName) {
        return takeScreenshot(driver, methodName);
    }
}
