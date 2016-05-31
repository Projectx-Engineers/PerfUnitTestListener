package com.emc.testlibrary.dao;

import com.emc.testlibrary.model.PerfMetrics;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yedids
 * Date: 5/11/16
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DataSource {
    public void init(String dataSourceString) throws Exception;
    public void write(List<PerfMetrics> perfMetricsList) throws Exception;
    public void close() throws Exception;
}
