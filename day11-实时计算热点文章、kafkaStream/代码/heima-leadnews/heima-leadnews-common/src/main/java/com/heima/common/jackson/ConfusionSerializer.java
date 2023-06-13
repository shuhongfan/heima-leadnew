package com.heima.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ConfusionSerializer extends JsonSerializer<Object> {

    @Override
    public  void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        try {
            if (value != null) {
                jsonGenerator.writeString(value.toString());
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        serializers.defaultSerializeValue(value, jsonGenerator);
    }
}
