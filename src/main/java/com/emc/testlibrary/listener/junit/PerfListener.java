package com.emc.testlibrary.listener.junit;

import com.emc.testlibrary.annotations.Perf;
import com.emc.testlibrary.dao.impl.CSVDataSource;
import com.emc.testlibrary.dao.DataSource;
import com.emc.testlibrary.exception.PerfTestException;
import com.emc.testlibrary.model.PerfMetrics;
import org.apache.commons.lang.time.StopWatch;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/11/16
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PerfListener extends RunListener {
    // TODO: add logging
    private StopWatch stopWatch = new StopWatch();
    private List<PerfMetrics> perfMetricsList = new ArrayList<PerfMetrics>();
    private boolean skip = false;
    private boolean includeAll = false;
    private String dataSourceString;
    private String dataSourceClassName;

    public PerfListener() {
        skip = Boolean.valueOf(System.getProperty("skip")).booleanValue();
        includeAll = Boolean.valueOf(System.getProperty("includeAll")).booleanValue();
        dataSourceString = System.getProperty("dataSource");
        dataSourceClassName = System.getProperty("dataSourceClass");
        if (dataSourceClassName == null) {
            dataSourceClassName = CSVDataSource.class.getName();
        }
    }

    private Perf getPerfAnnotation(Description description) {
        for (Annotation annotation : description.getAnnotations()) {
            if (annotation instanceof Perf) {
                return (Perf) annotation;
            }
        }
        return null;
    }


    public void testStarted(Description description) {
        if (includeAll || (!skip && getPerfAnnotation(description) != null)) {
            stopWatch.reset();
            stopWatch.start();
        }
    }

    public void testFinished(Description description) {
        Perf perfAnnotation = null;
        if (!skip) {
            perfAnnotation = getPerfAnnotation(description);
            //if calibrate all methods or if method has @Perf annotation
            if (includeAll || perfAnnotation != null) {
                stopWatch.stop();
                System.out.println("Time taken by method - "
                        + description.getClassName() + "."
                        + description.getMethodName() + "-->" + stopWatch.toString());

                PerfMetrics perfMetrics = new PerfMetrics();
                perfMetrics.setClassName(description.getClassName());
                perfMetrics.setMethodName(description.getMethodName());
                perfMetrics.setTimeTaken(stopWatch.toString());
                perfMetrics.setMessage(perfAnnotation!=null ? perfAnnotation.message() : "");
                perfMetrics.setComments(perfAnnotation!=null ? perfAnnotation.comments() : "");

                synchronized (perfMetricsList){
                    perfMetricsList.add(perfMetrics);
                }
            }
        }
    }

    public void testRunFinished(Result result) throws Exception {
        DataSource dataSource = null;
        try {
            System.out.println("Writing to Data source started...");
            System.out.println("No. of rows to write..." + perfMetricsList.size());
            if (!skip) {
                Object newInst = Class.forName(dataSourceClassName).newInstance();
                if (newInst instanceof DataSource) {
                    dataSource = (DataSource) newInst;
                } else {
                    throw new PerfTestException(dataSourceClassName + " is not instance of " + DataSource.class.getName());
                }

                //initialize data source
                try {
                    System.out.println("Initializing Data source..." + dataSourceClassName);
                    dataSource.init(dataSourceString);
                } catch (Exception e) {
                    throw new PerfTestException(
                            MessageFormat.format("Exception initializing Data source with string {0} using class {1}", dataSourceString, dataSourceClassName), e);
                }

                //writing data to data source
                try {
                    dataSource.write(perfMetricsList);
                } catch (Exception e) {
                    throw new PerfTestException(
                            MessageFormat.format("Exception writing data to Data source with string {0} using class {1}", dataSourceString, dataSourceClassName), e);
                }

            }
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (Exception e) {
                    throw new PerfTestException(
                            MessageFormat.format("Exception closing Data source with string {0} using class {1}", dataSourceString, dataSourceClassName), e);
                }
            }
        }
    }

}
