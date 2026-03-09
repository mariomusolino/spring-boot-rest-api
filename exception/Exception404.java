package com.odissey.tour.exception;

import lombok.Getter;

@Getter
public class Exception404 extends RuntimeException{

    private final String errMsg;

    public Exception404(String errMsg){
        super(String.format(errMsg));
        this.errMsg = errMsg;
    }
}
