package org.zhengjiale.testcase.dto;

import org.zhengjiale.testcase.entity.TestStep;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 创建/更新测试用例请求
 */
public class TestCaseRequest {

    @NotBlank(message = "用例名称不能为空")
    private String name;

    private String description;

    private String browser = "chrome";

    private String url;

    @NotEmpty(message = "测试步骤不能为空")
    private List<TestStep> steps;

    private Boolean enabled = true;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TestStep> steps) {
        this.steps = steps;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
