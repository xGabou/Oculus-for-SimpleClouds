/*
 * Decompiled with CFR 0.152.
 */
package kroppeb.stareval.parser;

import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.exception.ParseException;
import kroppeb.stareval.exception.UnexpectedCharacterException;
import kroppeb.stareval.exception.UnexpectedEndingException;
import kroppeb.stareval.parser.BinaryOp;
import kroppeb.stareval.parser.Parser;
import kroppeb.stareval.parser.ParserOptions;
import kroppeb.stareval.parser.StringReader;
import kroppeb.stareval.parser.UnaryOp;

class Tokenizer {
    private Tokenizer() {
    }

    static ExpressionElement parse(String input, ParserOptions options) throws ParseException {
        return Tokenizer.parseInternal(new StringReader(input), options);
    }

    static ExpressionElement parseInternal(StringReader input, ParserOptions options) throws ParseException {
        Parser stack = new Parser();
        ParserOptions.TokenRules tokenRules = options.getTokenRules();
        while (input.canRead()) {
            input.skipWhitespace();
            if (!input.canRead()) break;
            char c = input.read();
            if (tokenRules.isIdStart(c)) {
                String id = Tokenizer.readWhile(input, tokenRules::isIdPart);
                stack.visitId(id);
                continue;
            }
            if (c == '.' && stack.canReadAccess()) {
                input.skipWhitespace();
                if (input.canRead()) {
                    char start = input.read();
                    if (!tokenRules.isAccessStart(start)) {
                        throw new UnexpectedCharacterException("a valid accessor", start, input.getCurrentIndex());
                    }
                    String access = Tokenizer.readWhile(input, tokenRules::isAccessPart);
                    stack.visitAccess(access);
                    continue;
                }
                throw new UnexpectedEndingException("An expression can't end with '.'");
            }
            if (tokenRules.isNumberStart(c)) {
                String numberString = Tokenizer.readWhile(input, tokenRules::isNumberPart);
                stack.visitNumber(numberString);
                continue;
            }
            if (c == '(') {
                stack.visitOpeningParenthesis();
                continue;
            }
            if (c == ',') {
                stack.visitComma(input.getCurrentIndex());
                continue;
            }
            if (c == ')') {
                stack.visitClosingParenthesis(input.getCurrentIndex());
                continue;
            }
            if (stack.canReadBinaryOp()) {
                resolver = options.getBinaryOpResolver(c);
                if (resolver != null) {
                    stack.visitBinaryOperator((BinaryOp)resolver.resolve(input));
                    continue;
                }
            } else {
                resolver = options.getUnaryOpResolver(c);
                if (resolver != null) {
                    stack.visitUnaryOperator((UnaryOp)resolver.resolve(input));
                    continue;
                }
            }
            throw new UnexpectedCharacterException(c, input.getCurrentIndex());
        }
        return stack.getFinal(input.getCurrentIndex());
    }

    private static String readWhile(StringReader input, CharPredicate predicate) {
        input.mark();
        while (input.canRead() && predicate.test(input.peek())) {
            input.skipOneCharacter();
        }
        return input.substring();
    }

    private static interface CharPredicate {
        public boolean test(char var1);
    }
}

