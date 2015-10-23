package org.randomcoder.content;

import org.xml.sax.*;

/**
 * {@link ErrorHandler} implementation used to derive line and column numbers.
 * 
 * <pre>
 * Copyright (c) 2006-2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public class XHTMLErrorHandler implements ErrorHandler
{
	private int lineNumber = 1;
	private int columnNumber = 1;
	private String message = null;

	/**
	 * Gets the line number where processing failed.
	 * @return line number
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * Gets the column number where processing failed.
	 * @return column number
	 */
	public int getColumnNumber()
	{
		return columnNumber;
	}

	/**
	 * Gets the error message.
	 * @return error message
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Handles SAX warnings.
	 * @param ex SAX exception to handle.
	 * @throws SAXException the passed-in exception
	 */
	@Override
	public void warning(SAXParseException ex) throws SAXException
	{
		handle(ex);
		throw ex;
	}

	/**
	 * Handles SAX errors.
	 * @param ex SAX exception to handle.
	 * @throws SAXException the passed-in exception
	 */
	@Override
	public void error(SAXParseException ex) throws SAXException
	{
		handle(ex);
		throw ex;
	}
	
	/**
	 * Handles SAX fatal errors.
	 * @param ex SAX exception to handle.
	 * @throws SAXException the passed-in exception
	 */
	@Override
	public void fatalError(SAXParseException ex) throws SAXException
	{
		handle(ex);
		throw ex;
	}
	
	private void handle(SAXParseException ex)
	{
		lineNumber = ex.getLineNumber();
		columnNumber = ex.getColumnNumber();
		message = ex.getMessage();

		// account for prefix
		if (lineNumber == 1)
			columnNumber -= XHTMLFilter.PREFIX.length();
	}	
}
