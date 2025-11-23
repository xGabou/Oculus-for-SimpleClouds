/*
 * Decompiled with CFR 0.152.
 */
package oculus.org.antlr.v4.runtime;

import oculus.org.antlr.v4.runtime.ParserRuleContext;

public class RuleContextWithAltNum
extends ParserRuleContext {
    public int altNum;

    public RuleContextWithAltNum() {
        this.altNum = 0;
    }

    public RuleContextWithAltNum(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public int getAltNumber() {
        return this.altNum;
    }

    @Override
    public void setAltNumber(int altNum) {
        this.altNum = altNum;
    }
}

