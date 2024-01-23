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
 * ToStreamTest.java
 *
 */
package org.apache.qetest.trax;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.qetest.FileBasedTest;
import org.apache.xml.serializer.ToStream;
import org.apache.xml.serializer.ToXMLStream;
import org.xml.sax.SAXException;

//-------------------------------------------------------------------------

/**
 * Verify how stream output works.
 * @author jesper@selskabet.org
 * @version $Id$
 */
public abstract class ToStreamTest extends FileBasedTest
{

    /** Just initialize test name, comment, numTestCases. */
    public ToStreamTest()
    {
        numTestCases = 3;  // REPLACE_num
    }

    protected String buildUtf16String(int[] codepoints) {

		StringBuffer sb = new StringBuffer();
		for (int i=0; i<codepoints.length; ++i) {
			if (codepoints[i] > 0xFFFF) {
				sb.append(hiSurrogate(codepoints[i]));
				sb.append(lowSurrogate(codepoints[i]));
			} else {
				sb.append((char)codepoints[i]);
			}
		}
		String utf16String = sb.toString();
		return utf16String;
	}

    public static final char MIN_HIGH_SURROGATE = '\uD800';
    public static final char MIN_LOW_SURROGATE  = '\uDC00';
    public static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;

    private char lowSurrogate(int codepoint) {
    	int offset = codepoint - MIN_SUPPLEMENTARY_CODE_POINT;
    	return (char)((offset & 0x3ff) + MIN_LOW_SURROGATE);
    }

    private char hiSurrogate(int codepoint) {
    	int offset = codepoint - MIN_SUPPLEMENTARY_CODE_POINT;
    	return (char)((offset >>> 10) + MIN_HIGH_SURROGATE);
    }

	abstract protected ToStream makeStream(String encodingName);
    
	protected String outputCharacters(ToStream stream, String input) throws SAXException {
		StringWriter writer = new StringWriter();
		stream.setOmitXMLDeclaration(true);
		stream.setWriter(writer);
		stream.characters(input);
		stream.flushPending();
		return writer.getBuffer().toString();
	}

	protected String outputAttrValue(ToStream stream, String input) throws SAXException, IOException {
		StringWriter writer = new StringWriter();
		stream.setOmitXMLDeclaration(true);
		stream.setWriter(writer);
		stream.writeAttrString(writer, input, "???");
		stream.flushPending();
		return writer.getBuffer().toString();
	}

}
