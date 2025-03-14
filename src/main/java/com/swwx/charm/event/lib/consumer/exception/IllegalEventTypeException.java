package com.swwx.charm.event.lib.consumer.exception;

import com.swwx.charm.commons.exception.SystemErrorException;

public class IllegalEventTypeException extends SystemErrorException {

    private static final long serialVersionUID = 1L;

    public IllegalEventTypeException() {
        super();
    }

    public IllegalEventTypeException(String msg) {
        super(msg);
    }

    public IllegalEventTypeException(String msg, Throwable th) {
        super(msg, th);
    }

    public IllegalEventTypeException(Throwable th) {
        super(th);
    }
}
