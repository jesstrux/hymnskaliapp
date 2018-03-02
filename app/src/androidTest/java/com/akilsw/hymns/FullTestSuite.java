package com.akilsw.hymns;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by WAKY on 9/4/2016.
 */
public class FullTestSuite {
    public static Test suite(){
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public FullTestSuite(){
        super();
    }
}
