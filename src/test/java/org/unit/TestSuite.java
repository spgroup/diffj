package org.incava.unit;

import org.junit.Test;
import org.junit.Assert;

import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.Arrays;

import org.incava.diffj.app.DiffJ;
import org.incava.diffj.app.Options;
import org.incava.diffj.app.MethodDiff;
import org.incava.analysis.ReportType;



public class TestSuite {

    private final static String LEFT = "diffj/simple/Math01.java";
    private final static String RIGHT = "diffj/simple/Math02.java";

    private URL seek(String name) {
        return ClassLoader.getSystemResource(name);
    }
    @Test
    public void testSimpleCase() throws Exception {
        File left = new File(seek(LEFT).toURI());
        File right = new File(seek(RIGHT).toURI());

        List<String> opts = Arrays.asList(new String[]{left.getAbsolutePath(), right.getAbsolutePath()});
        Options options = new Options();
        options.process(opts);

        DiffJ diff = new DiffJ(ReportType.ChangedMethodsOnly, true, options.highlightOutput(),
                options.recurse(), "left", options.getFromSource(), "right", options.getToSource());

        diff.processNames(opts); 
    }
}