package com.odissey.tour.exception;

import lombok.Getter;

@Getter
public class Exception409 extends RuntimeException{

    private final String errMsg;

    public Exception409(String errMsg){
        super(String.format(errMsg));
        this.errMsg = errMsg;
    }
}
