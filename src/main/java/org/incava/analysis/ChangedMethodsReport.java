package org.incava.analysis;

import java.io.IOException;
import java.io.Writer;
import java.util.Set; 
import java.util.Collection;

import org.incava.diffj.app.MethodDiff;

public class ChangedMethodsReport extends Report {

    public ChangedMethodsReport(Writer writer) {
        super(writer);
    }

     /**
     * Returns the given difference, in brief format.
     */
    protected String toString(FileDiff fdiff) {
        StringBuilder sb = new StringBuilder();
        sb.append(fdiff.toDiffSummaryString());
        sb.append(": ");
        sb.append(fdiff.getMessage());
        sb.append(EOLN);
        
        return sb.toString();
    }

    public void writeDifferences() {
        try {
            Set<String> diffs = MethodDiff.instance().getChangedMethods();
            for (String m: diffs) {
                writer.write(m + "\n");
            }
            // we can't close STDOUT
            writer.flush();
        }
        catch (IOException ioe) {
            tr.Ace.log("ioe", ioe);
        }
    }
}