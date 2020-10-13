package lk.chathurabuddi.json.schema.filter;

import lk.chathurabuddi.json.schema.constants.FreeFormAction;
import lk.chathurabuddi.json.schema.exceptions.InvalidSchemaException;
import lk.chathurabuddi.json.schema.exceptions.JsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

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

class JsonSchemaFilterTest {

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/remove_nodes.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Remove nodes that are not in the JSON schema")
    void test_removeNodes(String testName, String schema, String json, String expected) throws Exception {
        JsonSchemaFilter jsonSchemaFilter = new JsonSchemaFilter(schema, json);
        JSONAssert.assertEquals(expected, jsonSchemaFilter.filter(), JSONCompareMode.STRICT);
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/attach_free_form_objects.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Attach property less objects (free-form) if no free-form-action defined")
    void test_freeFormObjects_whenNoActionDefined(String testName, String schema, String json, String expected) throws Exception {
        JsonSchemaFilter jsonSchemaFilter = new JsonSchemaFilter(schema, json);
        JSONAssert.assertEquals(expected, jsonSchemaFilter.filter(), JSONCompareMode.STRICT);
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/attach_free_form_objects.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Attach property less objects (free-form) if free-form-action is ATTACH")
    void test_freeFormObjects_whenActionIsAttach(String testName, String schema, String json, String expected) throws Exception {
        JsonSchemaFilter jsonSchemaFilter = new JsonSchemaFilter(schema, json, FreeFormAction.ATTACH);
        JSONAssert.assertEquals(expected, jsonSchemaFilter.filter(), JSONCompareMode.STRICT);
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/detach_free_form_objects.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Attach property less objects (free-form) if free-form-action is DETACH")
    void test_freeFormObjects_whenActionIsDetach(String testName, String schema, String json, String expected) throws Exception {
        JsonSchemaFilter jsonSchemaFilter = new JsonSchemaFilter(schema, json, FreeFormAction.DETACH);
        JSONAssert.assertEquals(expected, jsonSchemaFilter.filter(), JSONCompareMode.STRICT);
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/incorrect_schema_format.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Throw an exception when JSON schema not in the correct format")
    void test_incorrectlyFormattedSchema(String testName, String schema, String json) {
        Assertions.assertThrows(InvalidSchemaException.class, () -> new JsonSchemaFilter(schema, json).filter());
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/invalid_json.csv", delimiter = ';', numLinesToSkip = 1)
    @DisplayName("Throw an exception for invalid JSONs which cannot parsed")
    void test_invalidJson(String testName, String schema, String json) {
        Assertions.assertThrows(JsonParseException.class, () -> new JsonSchemaFilter(schema, json).filter());
    }
}