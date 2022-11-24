package org.randomcoder.content;

/**
 * Exception thrown when invalid content is encountered.
 *
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
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
public class InvalidContentException extends Exception {
    private static final long serialVersionUID = 106795571729597774L;

    private final int lineNumber;
    private final int columnNumber;

    /**
     * Constructs a new exception.
     *
     * @param msg          error message
     * @param lineNumber   line number
     * @param columnNumber column number
     */
    public InvalidContentException(String msg, int lineNumber, int columnNumber) {
        super(msg);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    /**
     * Gets the line number where the error occured.
     *
     * @return line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets the column number where the error occured.
     *
     * @return column number
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    private String cleanMessage(String message) {
        if (message == null)
            return null;

        // do some pattern replacements
        message = message.replaceAll("^cvc[A-Za-z0-9\\-\\.]+:\\s*", "");
        message = message.replaceAll("One of '(.)*' is expected.$", "");

        return message;
    }

    /**
     * Gets the message associated with this exception.
     *
     * @return message
     */
    @Override
    public String getMessage() {
        // try to clean it up
        return cleanMessage(super.getMessage());
    }

    /**
     * Attempts to construct a meaningful string representation of this
     * exception.
     *
     * @return string value
     */
    @Override
    public String toString() {

        String buf = "Line " +
                getLineNumber() +
                ", column " +
                getColumnNumber() +
                ": " +
                getMessage();

        return buf;
    }
}
