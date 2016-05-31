Usage:
-------
This listener library can be used to generate time taken by each unit test case and push it into a report.

In any unit test(junit or testng) methods having @Test annotation you can add a @Perf annotation.
For TestNg unit test methods, you can either add a @Perf annotation or add "Perf" group as part of its groups.
Time taken for all such methods which are annotated with @Test and @Perf will be written to a CSV file by default.

Add below dependency in pom to get the @Perf annotation
------------------------------------------------------
        <dependency>
            <groupId>com.emc.testlibrary</groupId>
            <artifactId>testListeners</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>

You can update maven-sure-fire plugin to configure the listener and other properties
------------------------------------------------------------------------------------

	        <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.17</version>
                <configuration>
                    <systemProperties>
                        <property>
                            <name>dataSource</name>
                            <value>c:\logs\file</value>
                        </property>
                        <property>
                            <name>dataSourceClass</name>
                            <value>com.emc.testlibrary.dao.impl.XMLDataSource</value>
                        </property>
                        <property>
                            <name>skip</name>
                            <value>false</value>
                        </property>
                        <property>
                            <name>includeAll</name>
                            <value>false</value>
                        </property>
                    </systemProperties>
                    <properties>
                        <property>
                            <name>listener</name>
                            <value>com.emc.testlibrary.listener.junit.PerfListener</value>
                            <!-- <value>com.emc.testlibrary.listener.testng.PerfListener</value> -->
                        </property>
                    </properties>
                    <skipTests>${skipTests}</skipTests>
                    <!-- In case of testng you might have something like below to point to testng config xml
                        <suiteXmlFiles>
                            <suiteXmlFile>src/test/resources/test-config.xml</suiteXmlFile>
                        </suiteXmlFiles>
                    -->
                </configuration>
            </plugin>


Supported properties
--------------------
datasource : Full path to the file where the details have to be written. Default value is "C:\temp\Perf-Log.csv".

dataSourceClass : Fully qualified class name. Default value is com.emc.testlibrary.dao.impl.CSVDataSource.
By default it writes to a CSV, you can either use the OOB provided com.emc.testlibrary.dao.impl.XMLDataSource class to write to an XML.
Or write your own implementation of com.emc.testlibrary.dao.DataSource interface and use that to write to some other datasource.

skip: If set to true calculating performance metrics will be skipped.Default value is false.

includeAll: If set to true, the listener will try to calibrate metrics for all methods irrespective of whether they have @Perf annotation or not.

Extensions:
------------

Out of the box, two possible reports are provided.
 1. CSVDatasource which writes to a CSV file
 2. XMLDataSource which writes to a XML file.

You can create your own DataSource by implementing com.emc.testlibrary.dao.DataSource
In such cases you have to update the sure-fire plugin to add a new system property as below
                        <property>
                            <name>dataSourceClass</name>
                            <value>com.mydatasource.OracleDbDataSource</value>
                        </property>
                        <property>
                            <name>dataSource</name>
                            <value>dbhost:dbport(@username:@password)</value>
                        </property>

Since your datasource class is the one consuming the dataSource string, you can provide the dataSource property as something like above
and parse it in init() method of your datasource class to connect to appropriate source.
