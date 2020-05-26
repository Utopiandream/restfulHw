package com.charter.restfulHw.exception;

public class NoDataFoundException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 2051992637159910941L;

    public NoDataFoundException() {

        super("No data found");
    }
}