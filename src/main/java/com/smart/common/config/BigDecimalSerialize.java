package com.smart.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 金额格式化
 * @author 小柒2012
 */
public class BigDecimalSerialize extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value != null && !"".equals(value)) {
            gen.writeString(value.setScale(2, BigDecimal.ROUND_HALF_DOWN) + "");
        } else {
            gen.writeString(value + "");
        }
    }
}