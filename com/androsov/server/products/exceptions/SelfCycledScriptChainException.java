package com.androsov.server.products.exceptions;

public class SelfCycledScriptChainException extends Exception {
    public SelfCycledScriptChainException(String message) {
        super(message);
    }
}
