package com.emc.testlibrary.dao.impl;

import com.emc.testlibrary.model.PerfMetrics;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/11/16
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLDataSource extends CSVDataSource {
    private String defaultFilePath =  "C:\\temp\\Perf-Log.xml";

    @Override
    public String getDefaultFilePath() {
        return defaultFilePath;
    }

    @Override
    public void write(List<PerfMetrics> perfMetricsList) throws Exception{
        PrintWriter printWriter = getPrintWriter();
        printWriter.print("<testcase-metrics><testcases>");
        for (PerfMetrics perfMetrics : perfMetricsList) {
            printWriter.print("<testcase><date>");
            printWriter.print(Calendar.getInstance().getTime().toString());
            printWriter.print("</date><className>");
            printWriter.print(perfMetrics.getClassName());
            printWriter.print("</className><methodName>");
            printWriter.print(perfMetrics.getMethodName());
            printWriter.print("</methodName><message>");
            printWriter.print(perfMetrics.getMessage());
            printWriter.print("</message><comments>");
            printWriter.print(perfMetrics.getComments());
            printWriter.print("</comments><timeTaken>");
            printWriter.print(perfMetrics.getTimeTaken());
            printWriter.println("</timeTaken></testcase>");
        }
        printWriter.print("</testcases></testcase-metrics>");
    }
}
