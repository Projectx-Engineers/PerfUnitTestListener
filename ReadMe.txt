We could write unit or integration test cases that perform a particular task so as to measure performance of a back end operation.

While we write such test cases, we could use this tool that helps in collating metrics of all cases.

This tool works well with both JUnit and TestNG and can generate a CSV or XML report.

 
---------
Usage:
---------
Attached is the testListeners-1.0.jar file with testNg and JUnit listeners which can be used to generate time taken by each test case and push it into a report(CSV or XML).

In any test(junit or testng) methods having @Test annotation you can add a @Perf annotation(this is provided by this tools library).
For TestNG test methods, you can either add a @Perf annotation or add "Perf" group as part of its groups.
Time taken for all such methods which are annotated with @Test and @Perf will be written to a CSV file by default.

The @Perf annotation supports two parameters named comments & message where you could provide appropriate details to identify the test case appropriately.

You can also set 'includeAll' property to true to measure all test methods irrespective of whether they have @Perf annotation or not.

-----------------------
Steps to use this tool:
-----------------------
•Publish the attached library into your local maven repository

mvn install:install-file -Dfile=testListeners-1.0-SNAPSHOT.jar -DgroupId=com.emc.testlibrary -DartifactId=testListeners -Dversion=1.0-SNAPSHOT -Dpackaging=jar


•Add below dependency in pom to get the @Perf annotation

        <dependency>

            <groupId>com.emc.testlibrary</groupId>

            <artifactId>testListeners</artifactId>

            <version>1.0-SNAPSHOT</version>

            <scope>test</scope>

        </dependency>

•Update maven-sure-fire plugin to configure the listener and other properties
	◦For junit use the listener value as com.emc.testlibrary.listener.junit.PerfListener
	◦For testNG use the listener value as com.emc.testlibrary.listener.testng.PerfListener


     Example:


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

 
---------------------
Supported properties
---------------------
•datasource: Full path to the file where the details have to be written. Default value is "C:\temp\Perf-Log.csv".
•dataSourceClass : Fully qualified class name. Default value is com.emc.testlibrary.dao.impl.CSVDataSource. which writes the results to a CSV file.
You can either use the other OOB provided data source com.emc.testlibrary.dao.impl.XMLDataSource class that writes to an XML Or write your own implementation of com.emc.testlibrary.dao.DataSource interface and use that to write to some other data source.
•skip: If set to true calculating performance metrics will be skipped.Default value is false.
•includeAll: If set to true, the listener will try to calibrate metrics for all methods irrespective of whether they have @Perf annotation or not.

-----------
Extensions
-----------

Out of the box, two possible reports are provided.
1.CSVDatasource which writes to a CSV file
2.XMLDataSource which writes to a XML file.

You can create your own dataSource by implementing the interface com.emc.testlibrary.dao.DataSource

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
