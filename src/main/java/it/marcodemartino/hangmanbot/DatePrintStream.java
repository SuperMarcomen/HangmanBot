package it.marcodemartino.hangmanbot;

import java.io.PrintStream;
import java.util.Date;

public class DatePrintStream extends PrintStream {

    PrintStream out;

    public DatePrintStream(PrintStream out1, PrintStream out2) {
        super(out1);
        out = out2;
    }

    public void write(byte buf[], int off, int len) {
        try {
            super.write(buf, off, len);
            out.write(buf, off, len);
        } catch (Exception e) {
        }
    }

    public void flush() {
        super.flush();
        out.flush();
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        Date date = new Date();
        return super.printf("[" + date.toString() + "] " + format, args);
    }

    @Override
    public void println(String string) {
        Date date = new Date();
        super.println("[" + date.toString() + "] " + string);
    }
}
