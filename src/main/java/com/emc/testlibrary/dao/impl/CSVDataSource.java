package com.emc.testlibrary.dao.impl;

import com.emc.testlibrary.dao.DataSource;
import com.emc.testlibrary.exception.PerfTestException;
import com.emc.testlibrary.model.PerfMetrics;

import java.io.*;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/11/16
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSVDataSource implements DataSource {
    private String filePath;
    private FileOutputStream fileOutputStream;
    private PrintWriter printWriter;
    private String defaultFilePath =  "C:\\temp\\Perf-Log.csv";

    public String getDefaultFilePath() {
        return defaultFilePath;
    }

    @Override
    public void init(String dataSourceString) throws Exception{
        filePath = dataSourceString;
        if (filePath == null || filePath.isEmpty()) {
            filePath = getDefaultFilePath();
        }
        filePath = filePath.trim();

        File file = new File(filePath);
        if (file.exists()) {
            file.renameTo(new File(filePath + "_" + System.currentTimeMillis()));
        } else {
            boolean created = false;
            try {
                created = file.createNewFile();
            } catch (IOException e) {
                throw new PerfTestException(MessageFormat.format("Could not create CSV in path {0} due to IOException", filePath), e);
            }
            if (!created) {
                throw new PerfTestException(MessageFormat.format("Could not create CSV in path {0}", filePath));
            }
        }

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new PerfTestException(MessageFormat.format("Could not open stream as file not found in path {0}", filePath));
        }
        printWriter = new PrintWriter(fileOutputStream);
    }

    @Override
    public void write(List<PerfMetrics> perfMetricsList) throws Exception{

        printHeader();

        for (PerfMetrics perfMetrics : perfMetricsList) {
            printWriter.print(Calendar.getInstance().getTime().toString());
            printWriter.print(",");
            printWriter.print(perfMetrics.getClassName());
            printWriter.print(",");
            printWriter.print(perfMetrics.getMethodName());
            printWriter.print(",");
            printWriter.print(perfMetrics.getMessage());
            printWriter.print(",");
            printWriter.print(perfMetrics.getComments());
            printWriter.print(",");
            printWriter.print(perfMetrics.getTimeTaken());
            printWriter.println("");
        }
    }

    private void printHeader(){
        printWriter.print("Date");
        printWriter.print(",");
        printWriter.print("Class Name");
        printWriter.print(",");
        printWriter.print("Method Name");
        printWriter.print(",");
        printWriter.print("Message");
        printWriter.print(",");
        printWriter.print("Comments");
        printWriter.print(",");
        printWriter.print("Time Taken");
        printWriter.println("");

    }
    protected PrintWriter getPrintWriter(){
        return printWriter;
    }

    @Override
    public void close() throws Exception{
        if (printWriter != null) {
            printWriter.close();
        }

        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                throw new PerfTestException(MessageFormat.format("Could not close opened stream for file in path {0}", filePath), e);
            }
        }
    }
}
