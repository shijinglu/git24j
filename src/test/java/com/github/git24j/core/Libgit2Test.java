package com.github.git24j.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumSet;

public class Libgit2Test {
    @BeforeClass
    public static void setUpClass() {
        Init.loadLibraries();
        Libgit2.init();
    }

    @AfterClass
    public static void tearDownClass() {
        Libgit2.shutdown();
    }

    @Test
    public void version() {
        Version v = Libgit2.version();
        Assert.assertEquals(0, v.major);
        Assert.assertEquals(28, v.minor);
        Assert.assertEquals(2, v.patch);
    }

    @Test
    public void features() {
        int features = Libgit2.features();
        Assert.assertTrue(features > 0);
        EnumSet<GitFeature> featuresSet = Libgit2.featuresSet();
        // should at least support threads
        Assert.assertTrue(featuresSet.contains(GitFeature.THTREADS));
        int res = featuresSet.stream().map(f -> f.code).reduce(0, (acc, inc) -> acc | inc);
        Assert.assertEquals(features, res);
    }
}
