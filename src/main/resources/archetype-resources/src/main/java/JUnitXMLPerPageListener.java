#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fitnesse.util.TimeMeasurement;

/**
 * JUnit RunListener to be used during integration test executing FitNesse pages.
 * It will create a result XML file per FitNesse page, instead of default (surefire/maven) behavior that creates only
 * 1 file per Java class (and we have only 1 class that runs all pages).
 * This allows build servers to report progress during the run.
 * The page names are used as test names, the Java class executing them is ignored.
 */
public class JUnitXMLPerPageListener extends RunListener {
    // default directory for maven-failsafe-plugin
    private final static String OUTPUT_PATH = "target/failsafe-reports/";
    private TimeMeasurement timeMeasurement;

    /**
     * Creates new.
     */
    public JUnitXMLPerPageListener() {
        new File(getOutputPath()).mkdirs();
    }

    @Override
    public void testStarted(Description description) throws Exception {
        timeMeasurement = new TimeMeasurement().start();
        super.testStarted(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
        if (!timeMeasurement.isStopped()) {
            recordTestResult(description, null, getExecutionTime());
        }
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        recordTestResult(failure.getDescription(), failure.getException(), getExecutionTime());
    }

    protected double getExecutionTime() {
        double executionTime = 0;
        if (timeMeasurement != null) {
            executionTime = timeMeasurement.elapsedSeconds();
            if (!timeMeasurement.isStopped()) {
                timeMeasurement.stop();
            }
        }
        return executionTime;
    }

    /**
     * Records result of single page (i.e. test).
     * @param description JUnit test description
     * @param exception exception from test
     * @param executionTime execution time in seconds
     * @throws IOException if unable to write XML
     */
    protected void recordTestResult(Description description, Throwable exception, double executionTime) throws IOException {
        String testName = getTestName(description);
        String resultXml = generateResultXml(testName, exception, executionTime);
        writeResult(testName, resultXml);
    }

    /**
     * Creates XML string describing test outcome.
     * @param testName name of test.
     * @param exception exception from test
     * @param executionTime execution time in seconds
     * @return XML description of test result
     */
    protected String generateResultXml(String testName, Throwable exception, double executionTime) {
        int errors = 0;
        int failures = 0;
        String failureXml = "";

        if (exception != null) {
            failureXml = "<failure type=${symbol_escape}"" + exception.getClass().getName()
                    + "${symbol_escape}" message=${symbol_escape}"" + getMessage(exception)
                    + "${symbol_escape}"></failure>";
            if (exception instanceof AssertionError)
                failures = 1;
            else
                errors = 1;
        }

        return "<testsuite errors=${symbol_escape}"" + errors + "${symbol_escape}" skipped=${symbol_escape}"0${symbol_escape}" tests=${symbol_escape}"1${symbol_escape}" time=${symbol_escape}""
                + executionTime + "${symbol_escape}" failures=${symbol_escape}"" + failures + "${symbol_escape}" name=${symbol_escape}""
                + testName + "${symbol_escape}">" + "<properties></properties>" + "<testcase classname=${symbol_escape}""
                + testName + "${symbol_escape}" time=${symbol_escape}"" + executionTime + "${symbol_escape}" name=${symbol_escape}""
                + testName + "${symbol_escape}">" + failureXml + "</testcase>" + "</testsuite>";
    }

    protected String getMessage(Throwable exception) {
        String errorMessage = exception.getMessage();
		return StringEscapeUtils.escapeXml(errorMessage);
    }

    /**
     * Writes XML result to disk.
     * @param testName name of test.
     * @param resultXml XML description of test outcome.
     * @throws IOException if unable to write result.
     */
    protected void writeResult(String testName, String resultXml) throws IOException {
        String finalPath = getXmlFileName(testName);
        Writer fw = null;
        try {
            fw = new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(finalPath),
                        "UTF-8"));
            fw.write("<?xml version=${symbol_escape}"1.0${symbol_escape}" encoding=${symbol_escape}"UTF-8${symbol_escape}"?>${symbol_escape}n");
            fw.write(resultXml);
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }

    /**
     * @param testName name of test.
     * @return file name to use.
     */
    protected String getXmlFileName(String testName) {
        // default pattern used by maven-failsafe-plugin
        return new File(getOutputPath(), "TEST-" + testName + ".xml").getAbsolutePath();
    }

    /**
     * @return directory to store test XMLs in.
     */
    protected String getOutputPath() {
        return OUTPUT_PATH;
    }

    /**
     * @param description JUnit description of test executed
     * @return name to use in report
     */
    protected String getTestName(Description description) {
        return description.getMethodName();
    }
}
