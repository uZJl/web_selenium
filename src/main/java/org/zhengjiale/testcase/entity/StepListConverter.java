package org.zhengjiale.testcase.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON <-> List<TestStep> 转换器
 */
@Converter
public class StepListConverter implements AttributeConverter<List<TestStep>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<TestStep> steps) {
        if (steps == null || steps.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(steps);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting steps to JSON", e);
        }
    }

    @Override
    public List<TestStep> convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<TestStep>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to steps", e);
        }
    }
}
