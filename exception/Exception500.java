package com.odissey.tour.exception;

import lombok.Getter;

@Getter
public class Exception500 extends RuntimeException{

    private final String errMsg;

    public Exception500(String errMsg){
        super(String.format(errMsg));
        this.errMsg = errMsg;
    }
}
