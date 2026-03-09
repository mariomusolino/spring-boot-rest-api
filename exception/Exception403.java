package com.odissey.tour.exception;

import lombok.Getter;

@Getter
public class Exception403 extends RuntimeException{

    private final String errMsg;

    public Exception403(String errMsg){
        super(String.format(errMsg));
        this.errMsg = errMsg;
    }
}
