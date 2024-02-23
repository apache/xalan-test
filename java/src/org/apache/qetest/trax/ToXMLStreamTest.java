/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */

/*
 *
 * ToXMLStreamTest.java
 *
 */
package org.apache.qetest.trax;

import java.io.IOException;

import org.apache.xml.serializer.ToStream;
import org.apache.xml.serializer.ToXMLStream;
import org.xml.sax.SAXException;

//-------------------------------------------------------------------------

/**
 * Verify how stream output works.
 * @author jesper@selskabet.org
 * @version $Id$
 */
public class ToXMLStreamTest extends ToStreamTest
{

    /** Just initialize test name, comment, numTestCases. */
    public ToXMLStreamTest()
    {
        testName = "ToXMLStreamTest";
        testComment = "Testing tricky Unicode encoding issues for XML output";
    }

    protected ToStream makeStream(String encodingName) {
		ToXMLStream stream = new ToXMLStream();
		stream.setEncoding(encodingName);
		
		return stream;
	}

    /**
	 * Verify outputting simple text in a different encodings
	 * @return false if we should abort the test; true otherwise
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public boolean testCase1() throws SAXException, IOException
	{
		reporter.testCaseInit("Verify handling of UTF-8 encoding in ToStream");
	
		String actual1 = outputCharacters(makeStream("UTF-8"), "abc");
		reporter.check(actual1, "abc", "Simple characters should come out unscathed");
		String actual1a = outputAttrValue(makeStream("UTF-8"), "abc");
		reporter.check(actual1a, "abc", "Simple characters should come out unscathed (as attribute value)");
	
		String AELIG_OSLASH_ARING = "\u00e6\u00f8\u00e5"; // \
		String actual2 = outputCharacters(makeStream("UTF-8"), AELIG_OSLASH_ARING);
		reporter.check(actual2, AELIG_OSLASH_ARING, "ISO-8859-1 characters should come out unscathed");
		String actual2a = outputAttrValue(makeStream("UTF-8"), AELIG_OSLASH_ARING);
		reporter.check(actual2a, AELIG_OSLASH_ARING, "ISO-8859-1 characters should come out unscathed (as attribute value)");
	
		String CHINESE = "\u95c9\u6d77\u4e95"; // \
		String actual3 = outputCharacters(makeStream("UTF-8"), CHINESE);
		reporter.check(actual3, CHINESE, "BMP characters should come out unscathed");
		String actual3a = outputAttrValue(makeStream("UTF-8"), CHINESE);
		reporter.check(actual3a, CHINESE, "BMP characters should come out unscathed (as attribute value)");
	
		String utf16String = buildUtf16String(new int[] { 0x010030, 0xa4, 0x010032});
		reporter.check(utf16String.length(), 5, "String with two astral characters and one in the BMP should have length of 5 UTF-16 code units");
		
		String actual4 = outputCharacters(makeStream("UTF-8"), utf16String);
		reporter.check(actual4, utf16String, "Astral characters should come out unscathed");
		String actual4b = outputCharacters(makeStream("UTF-8"), utf16String, utf16String.length() - 1);
		reporter.check(actual4b, utf16String, "Astral characters should come out unscathed (in split buffer)");
		String actual4a = outputAttrValue(makeStream("UTF-8"), utf16String);
		reporter.check(actual4a, utf16String, "Astral characters should come out unscathed (as attribute value)");
		
	    reporter.testCaseClose();
	    return true;
	}

	/**
     * Verify outputting simple text in simpler 
     * @return false if we should abort the test; true otherwise
     * @throws SAXException 
     * @throws IOException 
     */
    public boolean testCase2() throws SAXException, IOException
    {
    	reporter.testCaseInit("Verify handling of ISO-8859-1 encoding in ToStream");

		String actual1 = outputCharacters(makeStream("ISO-8859-1"), "abc");
		reporter.check(actual1, "abc", "Simple characters should come out unscathed");
    	String actual1a = outputAttrValue(makeStream("ISO-8859-1"), "abc");
		reporter.check(actual1a, "abc", "Simple characters should come out unscathed (as attribute value)");

		String AELIG_OSLASH_ARING = "\u00e6\u00f8\u00e5";
    	String actual2 = outputCharacters(makeStream("ISO-8859-1"), AELIG_OSLASH_ARING);
		reporter.check(actual2, AELIG_OSLASH_ARING, "ISO-8859-1 characters should come out unscathed (in encoding)");
    	String actual2a = outputAttrValue(makeStream("ISO-8859-1"), AELIG_OSLASH_ARING);
		reporter.check(actual2a, AELIG_OSLASH_ARING, "ISO-8859-1 characters should come out unscathed (as attribute value)");

		String CHINESE = "\u95c9\u6d77\u4e95";
    	String actual3 = outputCharacters(makeStream("ISO-8859-1"), CHINESE);
		reporter.check(actual3, "&#38345;&#28023;&#20117;", "BMP characters should come out as NCRs");
    	String actual3a = outputAttrValue(makeStream("ISO-8859-1"), CHINESE);
		reporter.check(actual3a, "&#38345;&#28023;&#20117;", "BMP characters should come out as NCRs (as attribute value)");

		String utf16String = buildUtf16String(new int[] { 0x010030, 0xa4, 0x010032});
		reporter.check(utf16String.length(), 5, "String with two astral characters and one in the BMP should have length of 5 UTF-16 code units");
		
    	String actual4 = outputCharacters(makeStream("ISO-8859-1"), utf16String);
		reporter.check(actual4, "&#65584;\u00a4&#65586;", "Astral characters should come out as NCRs");
    	String actual4a = outputAttrValue(makeStream("ISO-8859-1"), utf16String);
		reporter.check(actual4a, "&#65584;\u00a4&#65586;", "Astral characters should come out as NCRs (as attribute value)");
		
        reporter.testCaseClose();
        return true;
    }

    /**
     * Verify outputting simple text in simpler 
     * @return false if we should abort the test; true otherwise
     * @throws SAXException 
     * @throws IOException 
     */
    public boolean testCase3() throws SAXException, IOException
    {
    	reporter.testCaseInit("Verify handling of ASCII encoding in ToStream");

    	String actual1 = outputCharacters(makeStream("ASCII"), "abc");
		reporter.check(actual1, "abc", "Simple characters should come out unscathed");
		String actual1a = outputAttrValue(makeStream("ASCII"), "abc");
		reporter.check(actual1a, "abc", "Simple characters should come out unscathed (as attribute value)");

		String AELIG_OSLASH_ARING = "\u00e6\u00f8\u00e5"; // \
    	String actual2 = outputCharacters(makeStream("ASCII"), AELIG_OSLASH_ARING);
		reporter.check(actual2, "&#230;&#248;&#229;","ISO-8859-1 characters should come out as NCRs");
    	String actual2a = outputAttrValue(makeStream("ASCII"), AELIG_OSLASH_ARING);
		reporter.check(actual2a, "&#230;&#248;&#229;","ISO-8859-1 characters should come out as NCRs (as attribute value)");

		String CHINESE = "\u95c9\u6d77\u4e95"; // \
    	String actual3 = outputCharacters(makeStream("ASCII"), CHINESE);
		reporter.check(actual3, "&#38345;&#28023;&#20117;", "BMP characters should come out as NCRs");
    	String actual3a = outputAttrValue(makeStream("ASCII"), CHINESE);
		reporter.check(actual3a, "&#38345;&#28023;&#20117;", "BMP characters should come out as NCRs (as attribute value)");

		String utf16String = buildUtf16String(new int[] { 0x010030, 0xa4, 0x010032});
		reporter.check(utf16String.length(), 5, "String with two astral characters and one in the BMP should have length of 5 UTF-16 code units");		
    	String actual4 = outputCharacters(makeStream("ASCII"), utf16String);
		reporter.check(actual4, "&#65584;&#164;&#65586;", "Astral characters should come out as NCRs");
    	String actual4a = outputAttrValue(makeStream("ASCII"), utf16String);
		reporter.check(actual4a, "&#65584;&#164;&#65586;", "Astral characters should come out as NCRs (as attribute value)");

        reporter.testCaseClose();
        return true;
    }

    /**
     * Convenience method to print out usage information - update if needed.  
     * @return String denoting usage of this test class
     */
    public String usage()
    {
        return ("Common [optional] options supported by ToXMLStreamTest:\n"
                + super.usage());   // Grab our parent classes usage as well
    }


    /**
     * Main method to run test from the command line - can be left alone.  
     * @param args command line argument array
     */
    public static void main(String[] args)
    {

        ToXMLStreamTest app = new ToXMLStreamTest();

        app.doMain(args);
    }
}
