package com.odissey.tour.exception;

import lombok.Getter;

@Getter
public class Exception401 extends RuntimeException{

    private final String errMsg;

    public Exception401(String errMsg){
        super(String.format(errMsg));
        this.errMsg = errMsg;
    }
}
