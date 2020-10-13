package lk.chathurabuddi.json.schema.filter;

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Chathura Buddhika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import lk.chathurabuddi.json.schema.constants.SchemaKeyWord;
import lk.chathurabuddi.json.schema.exceptions.InvalidSchemaException;
import lk.chathurabuddi.json.schema.exceptions.JsonParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonSchemaFilter {

    private final String schemaStr;
    private final String sourceJsonStr;

    public JsonSchemaFilter(String schema, String sourceJson) {
        this.schemaStr = schema;
        this.sourceJsonStr = sourceJson;
    }

    public String filter() throws InvalidSchemaException, JsonParseException {
        JSONParser parser = new JSONParser();
        try {
            JSONObject schema = (JSONObject) parser.parse(schemaStr);
            if (SchemaKeyWord.OBJECT.value().equals(schema.get(SchemaKeyWord.TYPE.value()))) {
                return filter(schema, (JSONObject) parser.parse(sourceJsonStr)).toJSONString();
            } else if (SchemaKeyWord.ARRAY.value().equals(schema.get(SchemaKeyWord.TYPE.value()))) {
                return filter(schema, (JSONArray) parser.parse(sourceJsonStr)).toJSONString();
            } else {
                throw new InvalidSchemaException();
            }
        } catch (ParseException exception) {
            throw new JsonParseException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private JSONObject filter(JSONObject schema, JSONObject sourceJson) {
        JSONObject resultJson = new JSONObject();
        // check for property-less object (free-form)
        if (!schema.containsKey(SchemaKeyWord.PROPERTIES.value()) && !sourceJson.keySet().isEmpty()) {
            return sourceJson;
        }
        JSONObject schemaProperties = (JSONObject) schema.get(SchemaKeyWord.PROPERTIES.value());
        schemaProperties.keySet().forEach(key -> {
            Object sourceProperty = sourceJson.get(key);
            if (sourceProperty == null) {
                if (sourceJson.containsKey(key)) {
                    resultJson.put(key, null);
                }
            } else {
                JSONObject schemaProperty = (JSONObject) schemaProperties.get(key);
                String schemaPropertyType = (String) schemaProperty.get(SchemaKeyWord.TYPE.value());
                if (SchemaKeyWord.OBJECT.value().equals(schemaPropertyType)) {
                    resultJson.put(key, filter(schemaProperty, (JSONObject) sourceProperty));
                } else if (SchemaKeyWord.ARRAY.value().equals(schemaPropertyType)) {
                    resultJson.put(key, filter(schemaProperty, (JSONArray) sourceProperty));
                } else {
                    resultJson.put(key, sourceProperty);
                }
            }
        });
        return resultJson;
    }

    @SuppressWarnings("unchecked")
    private JSONArray filter(JSONObject schema, JSONArray sourceJson) {
        JSONArray resultJson = new JSONArray();
        JSONObject arrayItems = (JSONObject)schema.get(SchemaKeyWord.ITEMS.value());
        if (SchemaKeyWord.OBJECT.value().equals(arrayItems.get(SchemaKeyWord.TYPE.value()))) {
            for (Object item : sourceJson) {
                resultJson.add(filter(arrayItems, (JSONObject) item));
            }
        } else if (SchemaKeyWord.ARRAY.value().equals(arrayItems.get(SchemaKeyWord.TYPE.value()))){
            for (Object item : sourceJson) {
                resultJson.add(filter(arrayItems, (JSONArray) item));
            }
        } else if (arrayItems.get(SchemaKeyWord.TYPE.value()) != null){
            resultJson.addAll(sourceJson);
        }
        return resultJson;
    }
}
