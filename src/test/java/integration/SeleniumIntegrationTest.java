/**
 * Copyright 2019 XEBIALABS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package integration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.testcontainers.containers.DockerComposeContainer;

import integration.util.SeleniumTestHelper;

public class SeleniumIntegrationTest {
    
    @ClassRule
    public static DockerComposeContainer docker =
        new DockerComposeContainer(new File("build/resources/test/docker/docker-compose.yml"))
            .withLocalCompose(true);

    @BeforeClass
    public static void initialize() throws IOException, InterruptedException {
        SeleniumTestHelper.initializeXLR();
    }

    // Tests

    @Test
    public void testSelenium() throws Exception {
        String theResult = SeleniumTestHelper.getSeleniumReleaseResult();
        //System.out.println("RESULT:\n"+theResult);

        assertTrue(theResult != null);

        String expected = SeleniumTestHelper.readFile(SeleniumTestHelper.getResourceFilePath("testExpected/singleMulti.txt"));
        //System.out.println("EXPECTED:\n"+expected);
        JSONParser jsonParser = new JSONParser();
        
        Object obj = jsonParser.parse(theResult);

        JSONObject varJson = (JSONObject) obj;

        JSONArray single = (JSONArray) jsonParser.parse((String)varJson.get("${singleTestJson}"));
        JSONArray multiple = (JSONArray) jsonParser.parse((String)varJson.get("${multipleTestJson}"));

        assertTrue(single != null);
        assertTrue(multiple != null);
        assertTrue(single.size() == 1);
        assertTrue(multiple.size() == 3);

        for (Object selTest : single)  
        { 
            JSONObject selJObj = (JSONObject) selTest;
            assertTrue(((String) selJObj.get("result")).equals("Passed"));
        } 

        for (Object selTest : multiple)  
        { 
            JSONObject selJObj = (JSONObject) selTest;
            assertTrue(((String) selJObj.get("result")).equals("Passed"));
        } 
        System.out.println("");
        System.out.println("testSelenium passed");

    }

    private static void parseSelTestObject(JSONObject selTest) {

        // Get testName
        String testName = (String) selTest.get("testName");
        System.out.println(testName);
    }
}

