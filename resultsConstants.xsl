<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <!-- Misc Constants used within results viewing stylesheets -->
    <xsl:variable name="hash-marker">#hash</xsl:variable>
    <xsl:variable name="file-results-marker">#fr</xsl:variable>

    <!-- Constants file for XML results files - copied from Java sources -->

    <!-- public static final constants from org.apache.qetest.Logger.java -->
    <xsl:variable name="ERRORMSG">10</xsl:variable>
    <xsl:variable name="FAILSONLY">20</xsl:variable>
    <xsl:variable name="WARNINGMSG">30</xsl:variable>
    <xsl:variable name="STATUSMSG">40</xsl:variable>
    <xsl:variable name="INFOMSG">50</xsl:variable>
    <xsl:variable name="TRACEMSG">60</xsl:variable>
    <xsl:variable name="DEFAULT_LOGGINGLEVEL">STATUSMSG</xsl:variable>
    <xsl:variable name="INCP_RESULT">0</xsl:variable>
    <xsl:variable name="INCP">Incp</xsl:variable>
    <xsl:variable name="PASS_RESULT">2</xsl:variable>
    <xsl:variable name="PASS">Pass</xsl:variable>
    <xsl:variable name="AMBG_RESULT">5</xsl:variable>
    <xsl:variable name="AMBG">Ambg</xsl:variable>
    <xsl:variable name="FAIL_RESULT">8</xsl:variable>
    <xsl:variable name="FAIL">Fail</xsl:variable>
    <xsl:variable name="ERRR_RESULT">9</xsl:variable>
    <xsl:variable name="ERRR">Errr</xsl:variable>
    <xsl:variable name="DEFAULT_RESULT">INCP_RESULT</xsl:variable>

    <!-- public static final constants from org.apache.qetest.XMLFileLogger.java -->
    <xsl:variable name="ELEM_RESULTSFILE">resultsfile</xsl:variable>
    <xsl:variable name="ELEM_TESTFILE">testfile</xsl:variable>
    <xsl:variable name="ELEM_FILERESULT">fileresult</xsl:variable>
    <xsl:variable name="ELEM_TESTCASE">testcase</xsl:variable>
    <xsl:variable name="ELEM_CASERESULT">caseresult</xsl:variable>
    <xsl:variable name="ELEM_CHECKRESULT">checkresult</xsl:variable>
    <xsl:variable name="ELEM_STATISTIC">statistic</xsl:variable>
    <xsl:variable name="ELEM_LONGVAL">longval</xsl:variable>
    <xsl:variable name="ELEM_DOUBLEVAL">doubleval</xsl:variable>
    <xsl:variable name="ELEM_MESSAGE">message</xsl:variable>
    <xsl:variable name="ELEM_ARBITRARY">arbitrary</xsl:variable>
    <xsl:variable name="ELEM_HASHTABLE">hashtable</xsl:variable>
    <xsl:variable name="ELEM_HASHITEM">hashitem</xsl:variable>
    <xsl:variable name="ATTR_LEVEL">level</xsl:variable>
    <xsl:variable name="ATTR_DESC">desc</xsl:variable>
    <xsl:variable name="ATTR_TIME">time</xsl:variable>
    <xsl:variable name="ATTR_RESULT">result</xsl:variable>
    <xsl:variable name="ATTR_KEY">key</xsl:variable>
    <xsl:variable name="OPT_LOGFILE">logFile</xsl:variable>
    <xsl:variable name="ATTR_FILENAME">logFile</xsl:variable>

    <!-- public static final constants from org.apache.qetest.Reporter.java -->
    <!-- Note manually added the PERF_SEPARATOR on the end of each 
         one for simplicity's sake -->
    <xsl:variable name="TEST_START">TSrt;</xsl:variable>
    <xsl:variable name="TEST_STOP">TStp;</xsl:variable>
    <xsl:variable name="CASE_START">CSrt;</xsl:variable>
    <xsl:variable name="CASE_STOP">CStp;</xsl:variable>
    <xsl:variable name="USER_TIMER">UTmr;</xsl:variable>
    <xsl:variable name="USER_TIMESTAMP">UTim;</xsl:variable>
    <xsl:variable name="USER_MEMORY">UMem;</xsl:variable>
    <xsl:variable name="PERF_SEPARATOR">;</xsl:variable>

    <!-- public static final constants from org.apache.qetest.xsl.PerformanceTest.java -->
    <xsl:variable name="PERF_PRELOAD">UPre</xsl:variable>
    <xsl:variable name="PERF_ITERATION">UItr</xsl:variable>
    <xsl:variable name="PERF_AVERAGE">UAvg</xsl:variable>
    <xsl:variable name="PERF_MEMORY">UMem</xsl:variable>

</xsl:stylesheet>
