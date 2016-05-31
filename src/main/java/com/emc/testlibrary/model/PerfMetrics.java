package com.emc.testlibrary.model;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/11/16
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PerfMetrics {
    private String className;
    private String methodName;
    private String timeTaken;
    private String message;
    private String comments;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
