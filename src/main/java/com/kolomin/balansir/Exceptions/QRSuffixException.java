package com.kolomin.balansir.Exceptions;

import lombok.Data;

@Data
public class QRSuffixException extends Exception{
    private String value;
    private String suffix;

    public QRSuffixException(String value, String suffix) {
        this.value = value;
        this.suffix = suffix;
    }
}
