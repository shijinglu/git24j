package com.github.git24j.core;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RepositoryTest extends TestBase {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void open() {
        Path repoPath = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repo = Repository.open(repoPath.toString())) {
            Assert.assertEquals(repoPath.resolve(".git"), Paths.get(repo.getPath()));
        }
    }

    @Test
    public void openBare() {
        Path repoPath = TestRepo.SIMPLE1_BARE.tempCopy(folder);
        try (Repository repo = Repository.openBare(repoPath.toString())) {
            Assert.assertEquals(repoPath, Paths.get(repo.getPath()));
        }
    }

    @Test
    public void initOptions() {
        Repository.InitOptions initOptions = Repository.InitOptions.defaultOpts(Repository.InitOptions.VERSION);
        Assert.assertEquals(Repository.InitOptions.VERSION, initOptions.getVersion());
    }

    @Test
    public void init() {
        Path repoPath = folder.getRoot().toPath();
        try (Repository repo = Repository.init(repoPath.toString(), false)) {
            Assert.assertTrue(repo.isEmpty());
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void iniExt() {
        Path repoPath = folder.getRoot().toPath();
        String initPath = repoPath.toString();
        Repository.InitOptions opts = Repository.InitOptions.defaultOpts(Repository.InitOptions.VERSION);
        try (Repository repo = Repository.initExt(initPath, opts)) {
            Assert.assertNotNull(repo);
            Assert.assertTrue(repo.isEmpty());
            Assert.assertTrue(repo.headUnborn());
        }
        try (Repository repo = Repository.openExt(repoPath.toString(), null, null)) {
            Assert.assertNotNull(repo);
            Assert.assertTrue(repo.isEmpty());
            Assert.assertTrue(repo.headUnborn());
        }
    }

    @Test
    public void headForWorkTree() {
        Path path = TestRepo.WORKTREE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            try (Reference ref = repository.headForWorkTree("wt1")) {
                Assert.assertTrue(ref.getRawPointer() > 0);
            }
        }
    }

    @Test
    public void headDetached_headUnborn_isEmpty() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            Assert.assertFalse(repository.headDetached());
            Assert.assertFalse(repository.headUnborn());
            Assert.assertFalse(repository.isEmpty());
        }
    }

    @Test
    public void getCommondir() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            Assert.assertEquals(path.toString() + "/.git/", repository.getPath());
            Assert.assertEquals(path.toString() + "/.git/", repository.getCommondir());
        }
    }

    @Test
    public void itemPath() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            Buf buf = repository.itemPath(Repository.Item.COMMONDIR);
            Assert.assertEquals(buf.getPtr(), repository.getCommondir());
        }
    }

    @Test
    public void workdir() {
        Path path = TestRepo.WORKTREE1.tempCopy(folder);
        Path w2 = path.resolve("w2");
        Assert.assertTrue(w2.toFile().mkdirs());
        try (Repository repository = Repository.open(path.toString())) {
            Path wd = repository.workdir();
            Assert.assertEquals(path, wd);
            Assert.assertNotNull(wd);
            repository.setWorkdir(w2, true);
            wd = repository.workdir();
            Assert.assertEquals(w2, wd);
        }
    }

    @Test
    public void setWorkdirBare() {
        Path path = TestRepo.SIMPLE1_BARE.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            Assert.assertTrue(repository.isBare());
            repository.setWorkdir(path, true);
            Assert.assertFalse(repository.isBare());
        }
    }

    @Test
    public void isWorkTree() {
        Path path = TestRepo.WORKTREE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.resolve("wt1").toString())) {
            Assert.assertTrue(repository.isWorktree());
        }
    }

    @Test
    public void config() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            try(Config cfg = repository.config()){
                Assert.assertEquals("vim", cfg.getString("core.editor").orElse(""));
            }
        }
    }

    @Test
    public void configSnapshot() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            try(Config cfg = repository.configSnapshot()){
                Assert.assertEquals("vim", cfg.getString("core.editor").orElse(""));
            }
        }
    }

    @Test
    public void odb() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            try(Odb odb = repository.odb()){
                Assert.assertNotNull(odb);
            }
        }
    }

    @Test
    public void refdb() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            try(Refdb refdb = repository.refdb()) {
                Assert.assertNotNull(refdb);
            }
        }
    }

    @Test
    public void message() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            Optional<String> maybeMsg = repository.message();
            Assert.assertEquals("prepared commit message file", maybeMsg.orElse("").trim());
            repository.messageRemove();
            try{
                repository.message();
                Assert.fail("should have throw error, because original valuek");
            } catch (GitException e) {
                // ignore
            }
        }
    }

    @Test
    public void stateCleanup() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            Optional<String> maybeMsg = repository.message();
            Assert.assertEquals("prepared commit message file", maybeMsg.orElse("").trim());
            repository.stateCleanup();
            try{
                repository.message();
                Assert.fail("should have throw error, because original valuek");
            } catch (GitException e) {
                // ignore
            }
        }
    }

    @Test
    public void fetchheadForeach() {
        AtomicLong callCnt = new AtomicLong();
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            repository.fetchheadForeach(new Repository.FetchHeadForeachCb() {
                @Override
                public int call(String remoteUrl, Oid oid, boolean isMerge) {
                    Assert.assertEquals("src/test/resources/simple1", remoteUrl);
                    Assert.assertEquals("476f0c95825ef4479cab580b71f8b85f9dea4ee4", oid.toString());
                    Assert.assertTrue(isMerge);
                    callCnt.incrementAndGet();
                    return 0;
                }
            });
        }
        Assert.assertEquals(1, callCnt.get());
    }

    @Test
    public void mergeheadForeach() {
        Path path = TestRepo.MERGE1.tempCopy(folder);
        AtomicInteger callCnt = new AtomicInteger();
        try (Repository repository = Repository.open(path.toString())) {
            repository.mergeHeadForeach(new Repository.MergeheadForeachCb() {
                @Override
                public int call(Oid oid) {
                    Assert.assertEquals("476f0c95825ef4479cab580b71f8b85f9dea4ee4", oid.toString());
                    callCnt.incrementAndGet();
                    return 0;
                }
            });
        }
        Assert.assertEquals(1, callCnt.get());
    }

    @Test
    public void hashFile() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            Oid oid = repository.hashfile(path.resolve("a"), GitObject.Type.BLOB, "");
            // same value as `git rev-parse HEAD:a`
            Assert.assertEquals("78981922613b2afb6025042ff6bd878ac1994e85", oid.toString());
        }
    }

    @Test
    public void setHead() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            repository.setHead("refs/heads/feature/dev");
            Assert.assertFalse(repository.headDetached());
        }
    }

    @Test
    public void setHeadDetached() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            repository.setHeadDetached(Oid.of("565ddbe0bd55687b43286889a8ead64f68301113"));
            Assert.assertTrue(repository.headDetached());
        }
    }

    @Test
    public void detachHead() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            repository.detachHead();
            Assert.assertTrue(repository.headDetached());
        }
    }

    @Test
    public void namespace() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            repository.setNamespace("test-ns");
            Assert.assertEquals("test-ns", repository.getNamespace());
            Assert.assertFalse(repository.isShadow());
        }
    }

    @Test
    public void identity() {
        Path path = TestRepo.SIMPLE1.tempCopy(folder);
        try (Repository repository = Repository.open(path.toString())) {
            repository.setIdent("test_name", "test@example.com");
            Repository.Identity id = repository.ident();
            Assert.assertEquals("test@example.com", id.getEmail());
            Assert.assertEquals("test_name", id.getName());
        }
    }
}
