package org.zhengjiale.testcase.entity;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 测试步骤
 */
public class TestStep {

    @NotBlank(message = "操作类型不能为空")
    private String action;

    private String locator;

    private String value;

    private String condition;

    private Integer timeout;

    private String description;

    // 附加参数
    private Map<String, Object> params;

    // 默认构造函数
    public TestStep() {}

    // 便捷构造函数
    public TestStep(String action) {
        this.action = action;
    }

    public TestStep(String action, String locator) {
        this.action = action;
        this.locator = locator;
    }

    public TestStep(String action, String locator, String value) {
        this.action = action;
        this.locator = locator;
        this.value = value;
    }

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "TestStep{" +
                "action='" + action + '\'' +
                ", locator='" + locator + '\'' +
                ", value='" + value + '\'' +
                ", condition='" + condition + '\'' +
                ", timeout=" + timeout +
                ", description='" + description + '\'' +
                '}';
    }
}
