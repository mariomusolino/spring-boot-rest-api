package com.odissey.tour.exception;

import lombok.Getter;

@Getter
public class Exception422 extends RuntimeException{

    private final String errMsg;

    public Exception422(String errMsg){
        super(String.format(errMsg));
        this.errMsg = errMsg;
    }
}
