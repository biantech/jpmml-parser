package com.biantech.jpmml.parser.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
public class logTest {
    private Logger logger_ = LogManager.getLogger(logTest.class);

    @Test
    public void testLog(){
        logger_.info("test for log");
    }
}
