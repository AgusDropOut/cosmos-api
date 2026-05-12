package dev.cosmos.util.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CosmosExpressionParserTest {

    @Test
    public void testBasicArithmetic() {
        // Ensures standard order of operations (PEMDAS) is never broken
        MathExpression expr1 = CosmosExpressionParser.parse("2 + 3 * 4");
        assertEquals(14.0f, expr1.evaluate(0f, 0f), 0.001f, "Multiplication must precede addition");

        MathExpression expr2 = CosmosExpressionParser.parse("(2 + 3) * 4");
        assertEquals(20.0f, expr2.evaluate(0f, 0f), 0.001f, "Parentheses must override PEMDAS");

        MathExpression expr3 = CosmosExpressionParser.parse("2^3");
        assertEquals(8.0f, expr3.evaluate(0f, 0f), 0.001f, "Exponentiation failed");
    }

    @Test
    public void testVariablesAndFunctions() {

        MathExpression expr = CosmosExpressionParser.parse("sin(u_time * 2.0)");

        float time = (float) Math.PI / 4f; // pi/4 * 2 = pi/2. sin(pi/2) = 1.0


        assertEquals(1.0f, expr.evaluate(time, 0f), 0.001f, "Trig functions with time variable failed");
    }

    @Test
    public void testBinaryFunctions() {
        // Tests the multi-argument parsing logic: min(a,b), max(a,b), pow(a,b)
        MathExpression expr = CosmosExpressionParser.parse("max(10.0, 20.0) + min(5.0, 10.0)");

        assertEquals(25.0f, expr.evaluate(0f, 0f), 0.001f, "Binary functions min/max failed");
    }

    @Test
    public void testInvalidExpressionThrowsException() {

        assertThrows(RuntimeException.class, () -> {
            CosmosExpressionParser.parse("2 + * 4");
        }, "Parser should reject malformed arithmetic");

        assertThrows(RuntimeException.class, () -> {
            CosmosExpressionParser.parse("max(10.0)");
        }, "Parser should reject incomplete binary functions");
    }
}