package org.randomcoder.dao;

import net.bytebuddy.implementation.bytecode.Throw;

import java.io.Serial;

public class DataAccessException extends RuntimeException {

    @Serial
    static final long serialVersionUID = 1L;

    public DataAccessException() {
        super();
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
