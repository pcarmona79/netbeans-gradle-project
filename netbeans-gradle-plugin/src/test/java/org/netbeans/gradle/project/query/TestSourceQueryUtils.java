package org.netbeans.gradle.project.query;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.netbeans.api.java.queries.BinaryForSourceQuery;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import static org.junit.Assert.*;

public final class TestSourceQueryUtils {
    public static void expectSameArchive(String name, File expected, FileObject actual) {
        URI expectedUri;
        try {
            expectedUri = FileUtil.urlForArchiveOrDir(expected).toURI();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        URI actualUri = actual.toURI();
        assertEquals(name, expectedUri, actualUri);
    }

    public static File expectedSingleFile(BinaryForSourceQuery.Result result) {
        return expectedSingleFile(result.getRoots());
    }

    public static FileObject expectedSingleFile(SourceForBinaryQuery.Result result) {
        return expectedSingleFile(result.getRoots());
    }

    public static File expectedSingleFile(URL[] urls) {
        assertEquals("Expected exactly one url", 1, urls.length);
        return FileUtil.archiveOrDirForURL(urls[0]);
    }

    public static FileObject expectedSingleFile(FileObject[] fileObjs) {
        assertEquals("Expected exactly one file object", 1, fileObjs.length);
        return fileObjs[0];
    }

    private TestSourceQueryUtils() {
        throw new AssertionError();
    }
}
