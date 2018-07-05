package io.github.sweehaw.websupports.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author sweehaw
 */
public class ISOException extends Exception {
    private static final long serialVersionUID = -777216335204861186L;
    Throwable nested = null;

    public ISOException() {
    }

    public ISOException(String s) {
        super(s);
    }

    public ISOException(Throwable nested) {
        super(nested.toString());
        this.nested = nested;
    }

    public ISOException(String s, Throwable nested) {
        super(s);
        this.nested = nested;
    }

    public Throwable getNested() {
        return this.nested;
    }

    protected String getTagName() {
        return "iso-exception";
    }

    public void dump(PrintStream p, String indent) {
        String inner = indent + "  ";
        p.println(indent + "<" + this.getTagName() + ">");
        p.println(inner + this.getMessage());
        if (this.nested != null) {
            if (this.nested instanceof ISOException) {
                ((ISOException) this.nested).dump(p, inner);
            } else {
                p.println(inner + "<nested-exception>");
                p.print(inner);
                this.nested.printStackTrace(p);
                p.println(inner + "</nested-exception>");
            }
        }

        p.print(inner);
        this.printStackTrace(p);
        p.println(indent + "</" + this.getTagName() + ">");
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        if (this.nested != null) {
            buf.append(" (");
            buf.append(this.nested.toString());
            buf.append(")");
        }

        return buf.toString();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (this.nested != null) {
            System.err.print("Nested:");
            this.nested.printStackTrace();
        }

    }

    @Override
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (this.nested != null) {
            ps.print("Nested:");
            this.nested.printStackTrace(ps);
        }

    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (this.nested != null) {
            pw.print("Nested:");
            this.nested.printStackTrace(pw);
        }

    }
}
