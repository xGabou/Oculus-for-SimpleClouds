/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.TokenStream;
import oculus.org.antlr.v4.runtime.misc.Interval;

public class TokenStreamRewriter {
    public static final String DEFAULT_PROGRAM_NAME = "default";
    public static final int PROGRAM_INIT_SIZE = 100;
    public static final int MIN_TOKEN_INDEX = 0;
    protected final TokenStream tokens;
    protected final Map<String, List<RewriteOperation>> programs;
    protected final Map<String, Integer> lastRewriteTokenIndexes;

    public TokenStreamRewriter(TokenStream tokens) {
        this.tokens = tokens;
        this.programs = new HashMap<String, List<RewriteOperation>>();
        this.programs.put(DEFAULT_PROGRAM_NAME, new ArrayList(100));
        this.lastRewriteTokenIndexes = new HashMap<String, Integer>();
    }

    public final TokenStream getTokenStream() {
        return this.tokens;
    }

    public void rollback(int instructionIndex) {
        this.rollback(DEFAULT_PROGRAM_NAME, instructionIndex);
    }

    public void rollback(String programName, int instructionIndex) {
        List<RewriteOperation> is = this.programs.get(programName);
        if (is != null) {
            this.programs.put(programName, is.subList(0, instructionIndex));
        }
    }

    public void deleteProgram() {
        this.deleteProgram(DEFAULT_PROGRAM_NAME);
    }

    public void deleteProgram(String programName) {
        this.rollback(programName, 0);
    }

    public void insertAfter(Token t, Object text) {
        this.insertAfter(DEFAULT_PROGRAM_NAME, t, text);
    }

    public void insertAfter(int index, Object text) {
        this.insertAfter(DEFAULT_PROGRAM_NAME, index, text);
    }

    public void insertAfter(String programName, Token t, Object text) {
        this.insertAfter(programName, t.getTokenIndex(), text);
    }

    public void insertAfter(String programName, int index, Object text) {
        InsertAfterOp op = new InsertAfterOp(index, text);
        List<RewriteOperation> rewrites = this.getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
    }

    public void insertBefore(Token t, Object text) {
        this.insertBefore(DEFAULT_PROGRAM_NAME, t, text);
    }

    public void insertBefore(int index, Object text) {
        this.insertBefore(DEFAULT_PROGRAM_NAME, index, text);
    }

    public void insertBefore(String programName, Token t, Object text) {
        this.insertBefore(programName, t.getTokenIndex(), text);
    }

    public void insertBefore(String programName, int index, Object text) {
        InsertBeforeOp op = new InsertBeforeOp(index, text);
        List<RewriteOperation> rewrites = this.getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
    }

    public void replace(int index, Object text) {
        this.replace(DEFAULT_PROGRAM_NAME, index, index, text);
    }

    public void replace(int from, int to, Object text) {
        this.replace(DEFAULT_PROGRAM_NAME, from, to, text);
    }

    public void replace(Token indexT, Object text) {
        this.replace(DEFAULT_PROGRAM_NAME, indexT, indexT, text);
    }

    public void replace(Token from, Token to, Object text) {
        this.replace(DEFAULT_PROGRAM_NAME, from, to, text);
    }

    public void replace(String programName, int from, int to, Object text) {
        if (from > to || from < 0 || to < 0 || to >= this.tokens.size()) {
            throw new IllegalArgumentException("replace: range invalid: " + from + ".." + to + "(size=" + this.tokens.size() + ")");
        }
        ReplaceOp op = new ReplaceOp(from, to, text);
        List<RewriteOperation> rewrites = this.getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
    }

    public void replace(String programName, Token from, Token to, Object text) {
        this.replace(programName, from.getTokenIndex(), to.getTokenIndex(), text);
    }

    public void delete(int index) {
        this.delete(DEFAULT_PROGRAM_NAME, index, index);
    }

    public void delete(int from, int to) {
        this.delete(DEFAULT_PROGRAM_NAME, from, to);
    }

    public void delete(Token indexT) {
        this.delete(DEFAULT_PROGRAM_NAME, indexT, indexT);
    }

    public void delete(Token from, Token to) {
        this.delete(DEFAULT_PROGRAM_NAME, from, to);
    }

    public void delete(String programName, int from, int to) {
        this.replace(programName, from, to, null);
    }

    public void delete(String programName, Token from, Token to) {
        this.replace(programName, from, to, null);
    }

    public int getLastRewriteTokenIndex() {
        return this.getLastRewriteTokenIndex(DEFAULT_PROGRAM_NAME);
    }

    protected int getLastRewriteTokenIndex(String programName) {
        Integer I = this.lastRewriteTokenIndexes.get(programName);
        if (I == null) {
            return -1;
        }
        return I;
    }

    protected void setLastRewriteTokenIndex(String programName, int i) {
        this.lastRewriteTokenIndexes.put(programName, i);
    }

    protected List<RewriteOperation> getProgram(String name) {
        List<RewriteOperation> is = this.programs.get(name);
        if (is == null) {
            is = this.initializeProgram(name);
        }
        return is;
    }

    private List<RewriteOperation> initializeProgram(String name) {
        ArrayList<RewriteOperation> is = new ArrayList<RewriteOperation>(100);
        this.programs.put(name, is);
        return is;
    }

    public String getText() {
        return this.getText(DEFAULT_PROGRAM_NAME, Interval.of(0, this.tokens.size() - 1));
    }

    public String getText(String programName) {
        return this.getText(programName, Interval.of(0, this.tokens.size() - 1));
    }

    public String getText(Interval interval) {
        return this.getText(DEFAULT_PROGRAM_NAME, interval);
    }

    public String getText(String programName, Interval interval) {
        List<RewriteOperation> rewrites = this.programs.get(programName);
        int start = interval.a;
        int stop = interval.b;
        if (stop > this.tokens.size() - 1) {
            stop = this.tokens.size() - 1;
        }
        if (start < 0) {
            start = 0;
        }
        if (rewrites == null || rewrites.isEmpty()) {
            return this.tokens.getText(interval);
        }
        StringBuilder buf = new StringBuilder();
        Map<Integer, RewriteOperation> indexToOp = this.reduceToSingleOperationPerIndex(rewrites);
        int i = start;
        while (i <= stop && i < this.tokens.size()) {
            RewriteOperation op = indexToOp.get(i);
            indexToOp.remove(i);
            Token t = this.tokens.get(i);
            if (op == null) {
                if (t.getType() != -1) {
                    buf.append(t.getText());
                }
                ++i;
                continue;
            }
            i = op.execute(buf);
        }
        if (stop == this.tokens.size() - 1) {
            for (RewriteOperation op : indexToOp.values()) {
                if (op.index < this.tokens.size() - 1) continue;
                buf.append(op.text);
            }
        }
        return buf.toString();
    }

    protected Map<Integer, RewriteOperation> reduceToSingleOperationPerIndex(List<RewriteOperation> rewrites) {
        List<ReplaceOp> prevReplaces;
        RewriteOperation op;
        int i;
        for (i = 0; i < rewrites.size(); ++i) {
            op = rewrites.get(i);
            if (op == null || !(op instanceof ReplaceOp)) continue;
            ReplaceOp rop = (ReplaceOp)rewrites.get(i);
            List<InsertBeforeOp> inserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
            for (InsertBeforeOp insertBeforeOp : inserts) {
                if (insertBeforeOp.index == rop.index) {
                    rewrites.set(insertBeforeOp.instructionIndex, null);
                    rop.text = insertBeforeOp.text.toString() + (rop.text != null ? rop.text.toString() : "");
                    continue;
                }
                if (insertBeforeOp.index <= rop.index || insertBeforeOp.index > rop.lastIndex) continue;
                rewrites.set(insertBeforeOp.instructionIndex, null);
            }
            prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
            for (ReplaceOp prevRop : prevReplaces) {
                boolean disjoint;
                if (prevRop.index >= rop.index && prevRop.lastIndex <= rop.lastIndex) {
                    rewrites.set(prevRop.instructionIndex, null);
                    continue;
                }
                boolean bl = disjoint = prevRop.lastIndex < rop.index || prevRop.index > rop.lastIndex;
                if (prevRop.text == null && rop.text == null && !disjoint) {
                    rewrites.set(prevRop.instructionIndex, null);
                    rop.index = Math.min(prevRop.index, rop.index);
                    rop.lastIndex = Math.max(prevRop.lastIndex, rop.lastIndex);
                    System.out.println("new rop " + rop);
                    continue;
                }
                if (disjoint) continue;
                throw new IllegalArgumentException("replace op boundaries of " + rop + " overlap with previous " + prevRop);
            }
        }
        for (i = 0; i < rewrites.size(); ++i) {
            op = rewrites.get(i);
            if (op == null || !(op instanceof InsertBeforeOp)) continue;
            InsertBeforeOp iop = (InsertBeforeOp)rewrites.get(i);
            List<InsertBeforeOp> prevInserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
            for (InsertBeforeOp insertBeforeOp : prevInserts) {
                if (insertBeforeOp.index != iop.index) continue;
                if (InsertAfterOp.class.isInstance(insertBeforeOp)) {
                    iop.text = this.catOpText(insertBeforeOp.text, iop.text);
                    rewrites.set(insertBeforeOp.instructionIndex, null);
                    continue;
                }
                if (!InsertBeforeOp.class.isInstance(insertBeforeOp)) continue;
                iop.text = this.catOpText(iop.text, insertBeforeOp.text);
                rewrites.set(insertBeforeOp.instructionIndex, null);
            }
            prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
            for (ReplaceOp rop : prevReplaces) {
                if (iop.index == rop.index) {
                    rop.text = this.catOpText(iop.text, rop.text);
                    rewrites.set(i, null);
                    continue;
                }
                if (iop.index < rop.index || iop.index > rop.lastIndex) continue;
                throw new IllegalArgumentException("insert op " + iop + " within boundaries of previous " + rop);
            }
        }
        HashMap<Integer, RewriteOperation> m = new HashMap<Integer, RewriteOperation>();
        for (int i2 = 0; i2 < rewrites.size(); ++i2) {
            RewriteOperation op2 = rewrites.get(i2);
            if (op2 == null) continue;
            if (m.get(op2.index) != null) {
                throw new Error("should only be one op per index");
            }
            m.put(op2.index, op2);
        }
        return m;
    }

    protected String catOpText(Object a, Object b) {
        String x = "";
        String y = "";
        if (a != null) {
            x = a.toString();
        }
        if (b != null) {
            y = b.toString();
        }
        return x + y;
    }

    protected <T extends RewriteOperation> List<? extends T> getKindOfOps(List<? extends RewriteOperation> rewrites, Class<T> kind, int before) {
        ArrayList<RewriteOperation> ops = new ArrayList<RewriteOperation>();
        for (int i = 0; i < before && i < rewrites.size(); ++i) {
            RewriteOperation op = rewrites.get(i);
            if (op == null || !kind.isInstance(op)) continue;
            ops.add((RewriteOperation)kind.cast(op));
        }
        return ops;
    }

    class ReplaceOp
    extends RewriteOperation {
        protected int lastIndex;

        public ReplaceOp(int from, int to, Object text) {
            super(from, text);
            this.lastIndex = to;
        }

        @Override
        public int execute(StringBuilder buf) {
            if (this.text != null) {
                buf.append(this.text);
            }
            return this.lastIndex + 1;
        }

        @Override
        public String toString() {
            if (this.text == null) {
                return "<DeleteOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ">";
            }
            return "<ReplaceOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ":\"" + this.text + "\">";
        }
    }

    class InsertAfterOp
    extends InsertBeforeOp {
        public InsertAfterOp(int index, Object text) {
            super(index + 1, text);
        }
    }

    class InsertBeforeOp
    extends RewriteOperation {
        public InsertBeforeOp(int index, Object text) {
            super(index, text);
        }

        @Override
        public int execute(StringBuilder buf) {
            buf.append(this.text);
            if (TokenStreamRewriter.this.tokens.get(this.index).getType() != -1) {
                buf.append(TokenStreamRewriter.this.tokens.get(this.index).getText());
            }
            return this.index + 1;
        }
    }

    public class RewriteOperation {
        protected int instructionIndex;
        protected int index;
        protected Object text;

        protected RewriteOperation(int index) {
            this.index = index;
        }

        protected RewriteOperation(int index, Object text) {
            this.index = index;
            this.text = text;
        }

        public int execute(StringBuilder buf) {
            return this.index;
        }

        public String toString() {
            String opName = this.getClass().getName();
            int $index = opName.indexOf(36);
            opName = opName.substring($index + 1, opName.length());
            return "<" + opName + "@" + TokenStreamRewriter.this.tokens.get(this.index) + ":\"" + this.text + "\">";
        }
    }
}

