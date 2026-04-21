package dev.cosmos.util.math;

public class CosmosExpressionParser {

    public static MathExpression parse(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            MathExpression parse() {
                nextChar();
                MathExpression x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            MathExpression parseExpression() {
                MathExpression x = parseTerm();
                for (;;) {
                    if      (eat('+')) { MathExpression a = x, b = parseTerm(); x = (t, v) -> a.evaluate(t, v) + b.evaluate(t, v); }
                    else if (eat('-')) { MathExpression a = x, b = parseTerm(); x = (t, v) -> a.evaluate(t, v) - b.evaluate(t, v); }
                    else return x;
                }
            }

            MathExpression parseTerm() {
                MathExpression x = parseFactor();
                for (;;) {
                    if      (eat('*')) { MathExpression a = x, b = parseFactor(); x = (t, v) -> a.evaluate(t, v) * b.evaluate(t, v); }
                    else if (eat('/')) { MathExpression a = x, b = parseFactor(); x = (t, v) -> a.evaluate(t, v) / b.evaluate(t, v); }
                    else return x;
                }
            }

            MathExpression parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) { MathExpression a = parseFactor(); return (t, v) -> -a.evaluate(t, v); }

                MathExpression x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    float val = Float.parseFloat(str.substring(startPos, this.pos));
                    x = (t, v) -> val;
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);

                    // Variables
                    if (func.equals("t") || func.equals("time") || func.equals("u_time")) return (t, v) -> t;
                    if (func.equals("v") || func.equals("progress")) return (t, v) -> v;

                    // Binary Functions: min(a,b), max(a,b), pow(a,b)
                    if (func.equals("min") || func.equals("max") || func.equals("pow")) {
                        if (!eat('(')) throw new RuntimeException("Expected '(' after " + func);
                        MathExpression arg1 = parseExpression();
                        if (!eat(',')) throw new RuntimeException("Expected ',' in " + func);
                        MathExpression arg2 = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Expected ')' after " + func);

                        if (func.equals("min")) return (t, v) -> Math.min(arg1.evaluate(t, v), arg2.evaluate(t, v));
                        if (func.equals("max")) return (t, v) -> Math.max(arg1.evaluate(t, v), arg2.evaluate(t, v));
                        if (func.equals("pow")) return (t, v) -> (float) Math.pow(arg1.evaluate(t, v), arg2.evaluate(t, v));
                        return null; // Unreachable
                    }

                    // Unary Functions: sin(x), abs(x), etc.
                    MathExpression arg = parseFactor();

                    if (func.equals("sin")) return (t, v) -> (float) Math.sin(arg.evaluate(t, v));
                    else if (func.equals("cos")) return (t, v) -> (float) Math.cos(arg.evaluate(t, v));
                    else if (func.equals("abs")) return (t, v) -> Math.abs(arg.evaluate(t, v));
                    else if (func.equals("exp")) return (t, v) -> (float) Math.exp(arg.evaluate(t, v));
                    else if (func.equals("floor")) return (t, v) -> (float) Math.floor(arg.evaluate(t, v));
                    else if (func.equals("fract")) return (t, v) -> {
                        float val = arg.evaluate(t, v);
                        return val - (float) Math.floor(val);
                    };
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                // Fallback for infix exponentiation just in case (e.g. a^b)
                if (eat('^')) { MathExpression a = x, b = parseFactor(); x = (t, v) -> (float) Math.pow(a.evaluate(t, v), b.evaluate(t, v)); }
                return x;
            }
        }.parse();
    }
}