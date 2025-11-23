/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime.misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogManager {
    protected List<Record> records;

    public void log(String component, String msg) {
        Record r = new Record();
        r.component = component;
        r.msg = msg;
        if (this.records == null) {
            this.records = new ArrayList<Record>();
        }
        this.records.add(r);
    }

    public void log(String msg) {
        this.log(null, msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save(String filename) throws IOException {
        FileWriter fw = new FileWriter(filename);
        try (BufferedWriter bw = new BufferedWriter(fw);){
            bw.write(this.toString());
        }
    }

    public String save() throws IOException {
        String dir = ".";
        String defaultFilename = dir + "/antlr-" + new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date()) + ".log";
        this.save(defaultFilename);
        return defaultFilename;
    }

    public String toString() {
        if (this.records == null) {
            return "";
        }
        String nl = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder();
        for (Record r : this.records) {
            buf.append(r);
            buf.append(nl);
        }
        return buf.toString();
    }

    public static void main(String[] args) throws IOException {
        LogManager mgr = new LogManager();
        mgr.log("atn", "test msg");
        mgr.log("dfa", "test msg 2");
        System.out.println(mgr);
        mgr.save();
    }

    protected static class Record {
        long timestamp = System.currentTimeMillis();
        StackTraceElement location = new Throwable().getStackTrace()[0];
        String component;
        String msg;

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(this.timestamp)));
            buf.append(" ");
            buf.append(this.component);
            buf.append(" ");
            buf.append(this.location.getFileName());
            buf.append(":");
            buf.append(this.location.getLineNumber());
            buf.append(" ");
            buf.append(this.msg);
            return buf.toString();
        }
    }
}

