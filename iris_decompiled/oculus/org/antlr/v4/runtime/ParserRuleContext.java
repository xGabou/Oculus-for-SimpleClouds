/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oculus.org.antlr.v4.runtime.Parser;
import oculus.org.antlr.v4.runtime.RecognitionException;
import oculus.org.antlr.v4.runtime.RuleContext;
import oculus.org.antlr.v4.runtime.Token;
import oculus.org.antlr.v4.runtime.misc.Interval;
import oculus.org.antlr.v4.runtime.tree.ErrorNode;
import oculus.org.antlr.v4.runtime.tree.ErrorNodeImpl;
import oculus.org.antlr.v4.runtime.tree.ParseTree;
import oculus.org.antlr.v4.runtime.tree.ParseTreeListener;
import oculus.org.antlr.v4.runtime.tree.TerminalNode;
import oculus.org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class ParserRuleContext
extends RuleContext {
    public static final ParserRuleContext EMPTY = new ParserRuleContext();
    public List<ParseTree> children;
    public Token start;
    public Token stop;
    public RecognitionException exception;

    public ParserRuleContext() {
    }

    public void copyFrom(ParserRuleContext ctx) {
        this.parent = ctx.parent;
        this.invokingState = ctx.invokingState;
        this.start = ctx.start;
        this.stop = ctx.stop;
        if (ctx.children != null) {
            this.children = new ArrayList<ParseTree>();
            for (ParseTree child : ctx.children) {
                if (!(child instanceof ErrorNode)) continue;
                this.addChild((ErrorNode)child);
            }
        }
    }

    public ParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    public void enterRule(ParseTreeListener listener) {
    }

    public void exitRule(ParseTreeListener listener) {
    }

    public <T extends ParseTree> T addAnyChild(T t) {
        if (this.children == null) {
            this.children = new ArrayList<ParseTree>();
        }
        this.children.add(t);
        return t;
    }

    public RuleContext addChild(RuleContext ruleInvocation) {
        return this.addAnyChild(ruleInvocation);
    }

    public TerminalNode addChild(TerminalNode t) {
        t.setParent(this);
        return this.addAnyChild(t);
    }

    public ErrorNode addErrorNode(ErrorNode errorNode) {
        errorNode.setParent(this);
        return this.addAnyChild(errorNode);
    }

    @Deprecated
    public TerminalNode addChild(Token matchedToken) {
        TerminalNodeImpl t = new TerminalNodeImpl(matchedToken);
        this.addAnyChild(t);
        t.setParent(this);
        return t;
    }

    @Deprecated
    public ErrorNode addErrorNode(Token badToken) {
        ErrorNodeImpl t = new ErrorNodeImpl(badToken);
        this.addAnyChild(t);
        t.setParent(this);
        return t;
    }

    public void removeLastChild() {
        if (this.children != null) {
            this.children.remove(this.children.size() - 1);
        }
    }

    @Override
    public ParserRuleContext getParent() {
        return (ParserRuleContext)super.getParent();
    }

    @Override
    public ParseTree getChild(int i) {
        return this.children != null && i >= 0 && i < this.children.size() ? this.children.get(i) : null;
    }

    public <T extends ParseTree> T getChild(Class<? extends T> ctxType, int i) {
        if (this.children == null || i < 0 || i >= this.children.size()) {
            return null;
        }
        int j = -1;
        for (ParseTree o : this.children) {
            if (!ctxType.isInstance(o) || ++j != i) continue;
            return (T)((ParseTree)ctxType.cast(o));
        }
        return null;
    }

    public TerminalNode getToken(int ttype, int i) {
        if (this.children == null || i < 0 || i >= this.children.size()) {
            return null;
        }
        int j = -1;
        for (ParseTree o : this.children) {
            TerminalNode tnode;
            Token symbol;
            if (!(o instanceof TerminalNode) || (symbol = (tnode = (TerminalNode)o).getSymbol()).getType() != ttype || ++j != i) continue;
            return tnode;
        }
        return null;
    }

    public List<TerminalNode> getTokens(int ttype) {
        if (this.children == null) {
            return Collections.emptyList();
        }
        ArrayList<TerminalNode> tokens = null;
        for (ParseTree o : this.children) {
            TerminalNode tnode;
            Token symbol;
            if (!(o instanceof TerminalNode) || (symbol = (tnode = (TerminalNode)o).getSymbol()).getType() != ttype) continue;
            if (tokens == null) {
                tokens = new ArrayList<TerminalNode>();
            }
            tokens.add(tnode);
        }
        if (tokens == null) {
            return Collections.emptyList();
        }
        return tokens;
    }

    public <T extends ParserRuleContext> T getRuleContext(Class<? extends T> ctxType, int i) {
        return (T)((ParserRuleContext)this.getChild(ctxType, i));
    }

    public <T extends ParserRuleContext> List<T> getRuleContexts(Class<? extends T> ctxType) {
        if (this.children == null) {
            return Collections.emptyList();
        }
        ArrayList<ParserRuleContext> contexts = null;
        for (ParseTree o : this.children) {
            if (!ctxType.isInstance(o)) continue;
            if (contexts == null) {
                contexts = new ArrayList<ParserRuleContext>();
            }
            contexts.add((ParserRuleContext)ctxType.cast(o));
        }
        if (contexts == null) {
            return Collections.emptyList();
        }
        return contexts;
    }

    @Override
    public int getChildCount() {
        return this.children != null ? this.children.size() : 0;
    }

    @Override
    public Interval getSourceInterval() {
        if (this.start == null) {
            return Interval.INVALID;
        }
        if (this.stop == null || this.stop.getTokenIndex() < this.start.getTokenIndex()) {
            return Interval.of(this.start.getTokenIndex(), this.start.getTokenIndex() - 1);
        }
        return Interval.of(this.start.getTokenIndex(), this.stop.getTokenIndex());
    }

    public Token getStart() {
        return this.start;
    }

    public Token getStop() {
        return this.stop;
    }

    public String toInfoString(Parser recognizer) {
        List<String> rules = recognizer.getRuleInvocationStack(this);
        Collections.reverse(rules);
        return "ParserRuleContext" + rules + "{start=" + this.start + ", stop=" + this.stop + '}';
    }
}

