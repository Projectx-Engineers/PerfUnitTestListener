package com.emc.testlibrary.listener.testng;

import com.emc.testlibrary.annotations.Perf;
import com.emc.testlibrary.dao.impl.CSVDataSource;
import com.emc.testlibrary.dao.DataSource;
import com.emc.testlibrary.exception.PerfTestException;
import com.emc.testlibrary.model.PerfMetrics;
import org.apache.commons.lang.time.StopWatch;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/19/16
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class PerfListener implements IInvokedMethodListener,IReporter {

    private StopWatch stopWatch = new StopWatch();
    private List<PerfMetrics> perfMetricsList = new ArrayList<PerfMetrics>();
    private boolean skip = false;
    private boolean includeAll = false;
    private String dataSourceString;
    private String dataSourceClassName;

    public PerfListener(){
        skip = Boolean.valueOf(System.getProperty("skip")).booleanValue();
        includeAll = Boolean.valueOf(System.getProperty("includeAll")).booleanValue();
        dataSourceString = System.getProperty("dataSource");
        dataSourceClassName = System.getProperty("dataSourceClass");
        if (dataSourceClassName == null) {
            dataSourceClassName = CSVDataSource.class.getName();
        }

    }

    private Perf getPerfAnnotation(IInvokedMethod iInvokedMethod) {
        for (Annotation annotation : iInvokedMethod.getTestMethod().getConstructorOrMethod().getMethod().getAnnotations()) {
            if (annotation instanceof Perf) {
                return (Perf) annotation;
            }
        }
        return null;
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        if (includeAll || (!skip && getPerfAnnotation(iInvokedMethod) != null)) {
            stopWatch.reset();
            stopWatch.start();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
        Perf perfAnnotation = null;
        if (!skip && iInvokedMethod.isTestMethod()) {
            perfAnnotation = getPerfAnnotation(iInvokedMethod);
            String[] grps = iInvokedMethod.getTestMethod().getGroups();
            //if calibrate all methods or if method has @Perf annotation
            // or method's @Test(groups="grp1,grp2,grp3") annotation has Perf group in its list
            if (includeAll || perfAnnotation != null || (grps!=null && grps.length>0 && Arrays.asList(grps).contains("Perf"))) {
                stopWatch.stop();
                System.out.println("Time taken by method - "
                        + iInvokedMethod.getTestMethod().getConstructorOrMethod().getDeclaringClass().getName() + "."
                        + iInvokedMethod.getTestMethod().getMethodName() + "-->" + stopWatch.toString());

                PerfMetrics perfMetrics = new PerfMetrics();
                perfMetrics.setClassName(iInvokedMethod.getTestMethod().getConstructorOrMethod().getDeclaringClass().getName());
                perfMetrics.setMethodName(iInvokedMethod.getTestMethod().getMethodName());
                perfMetrics.setTimeTaken(stopWatch.toString());
                perfMetrics.setMessage(perfAnnotation!=null ? perfAnnotation.message() : "");
                perfMetrics.setComments(perfAnnotation!=null ? perfAnnotation.comments() : "");

                synchronized (perfMetricsList){
                    perfMetricsList.add(perfMetrics);
                }
            }
        }

    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> iSuites, String s) {
        DataSource dataSource = null;
        try {
            if (!skip) {
                Object newInst = Class.forName(dataSourceClassName).newInstance();
                if (newInst instanceof DataSource) {
                    dataSource = (DataSource) newInst;
                } else {
                    throw new PerfTestException(dataSourceClassName + " is not instance of " + DataSource.class.getName());
                }

                //initialize data source
                try {
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
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (Exception e) {
                    try {
                        throw new PerfTestException(
                                MessageFormat.format("Exception closing Data source with string {0} using class {1}", dataSourceString, dataSourceClassName), e);
                    } catch (PerfTestException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

    }
}
