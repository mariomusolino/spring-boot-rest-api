package com.odissey.tour.exception;

import lombok.Getter;

@Getter
public class Exception400 extends RuntimeException{

    private final String errMsg;

    public Exception400(String errMsg){
        super(String.format(errMsg));
        this.errMsg = errMsg;
    }
}
