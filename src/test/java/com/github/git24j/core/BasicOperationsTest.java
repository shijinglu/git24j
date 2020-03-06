package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;

import static com.github.git24j.core.Repository.InitFlagT.BARE;
import static com.github.git24j.core.Repository.InitFlagT.MKPATH;
import static com.github.git24j.core.Repository.InitModeT.SHARED_GROUP;

public class BasicOperationsTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void initSimple() throws Exception {
        Path path = folder.newFolder("simpleRepo").toPath();
        try (Repository repo = Repository.init(path, false)) {
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void initWithOptions() throws IOException {
        Repository.InitOptions opts =  Repository.InitOptions.defaultOpts();
        opts.setDescription("My repository has a custom description");
        opts.setFlags(EnumSet.of(MKPATH));
        try (Repository repo = Repository.initExt(folder.newFolder("tmp").getAbsolutePath(), opts)) {
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void simpleClone() {
        
    }
}
