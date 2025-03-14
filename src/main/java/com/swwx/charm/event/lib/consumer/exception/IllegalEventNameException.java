package com.swwx.charm.event.lib.consumer.exception;

import com.swwx.charm.commons.exception.SystemErrorException;

public class IllegalEventNameException extends SystemErrorException {

    private static final long serialVersionUID = 1L;

    public IllegalEventNameException() {
        super();
    }

    public IllegalEventNameException(String msg) {
        super(msg);
    }

    public IllegalEventNameException(String msg, Throwable th) {
        super(msg, th);
    }

    public IllegalEventNameException(Throwable th) {
        super(th);
    }

}
