package org.zhengjiale.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Selenium Grid 工具类
 */
public class GridUtils {
    private static final Logger logger = LogManager.getLogger(GridUtils.class);

    private GridUtils() {
        // 私有构造函数
    }

    /**
     * 检查 Grid 是否可用
     *
     * @param gridUrl Grid URL
     * @return true 如果 Grid 可用
     */
    public static boolean isGridAvailable(String gridUrl) {
        try {
            String statusUrl = gridUrl.replace("/wd/hub", "/status");
            URL url = new URL(statusUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                String responseBody = response.toString();
                boolean ready = responseBody.contains("\"ready\":true");
                logger.info("Grid status check - URL: {}, Ready: {}", statusUrl, ready);
                return ready;
            }
        } catch (Exception e) {
            logger.warn("Grid not available: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 获取 Grid 节点信息
     *
     * @param gridUrl Grid URL
     * @return 节点信息 JSON 字符串
     */
    public static String getGridInfo(String gridUrl) {
        try {
            String statusUrl = gridUrl.replace("/wd/hub", "/status");
            URL url = new URL(statusUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            logger.error("Failed to get Grid info: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 等待 Grid 就绪
     *
     * @param gridUrl      Grid URL
     * @param timeoutSeconds 超时时间（秒）
     * @return true 如果 Grid 在超时时间内就绪
     */
    public static boolean waitForGrid(String gridUrl, int timeoutSeconds) {
        logger.info("Waiting for Grid to be ready...");
        int elapsed = 0;
        while (elapsed < timeoutSeconds) {
            if (isGridAvailable(gridUrl)) {
                logger.info("Grid is ready after {} seconds", elapsed);
                return true;
            }
            try {
                Thread.sleep(1000);
                elapsed++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        logger.error("Grid not ready after {} seconds", timeoutSeconds);
        return false;
    }
}
