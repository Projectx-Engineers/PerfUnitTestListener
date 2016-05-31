package com.emc.testlibrary.exception;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/11/16
 * Time: 7:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class PerfTestException extends Exception {
    public PerfTestException(String msg) {
        super(msg);
    }

    public PerfTestException(String msg, Throwable e) {
        super(msg, e);
    }
}
