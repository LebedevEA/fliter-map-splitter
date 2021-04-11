import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class SplitterTest {
    private final static Operation AND = new Operation('&');
    private final static Operation OR = new Operation('|');
    private final static Operation EQ = new Operation('=');
    private final static Operation LT = new Operation('<');
    private final static Operation GT = new Operation('>');
    private final static Operation PLUS = new Operation('+');
    private final static Operation MUL = new Operation('*');
    private final static Operation MINUS = new Operation('-');

    private final static Expression ELEMENT = new Expression(new Literal("element"));

    private final static Expression CNeg1 = new Expression(new ConstantExpression(-1));
    private final static Expression C0 = new Expression(new ConstantExpression(0));
    private final static Expression C1 = new Expression(new ConstantExpression(1));
    private final static Expression C10 = new Expression(new ConstantExpression(10));
    private final static Expression C20 = new Expression(new ConstantExpression(20));
    private final static Expression C100 = new Expression(new ConstantExpression(100));

    private final static Expression TRUE = binOp(C1, EQ, C1);
    private final static Expression FALSE = binOp(C1, EQ, C0);

    private static CallChain chain(Call... calls) {
        return new CallChain(calls);
    }
    private static Call filter(Expression expression) {
        return new Call(new FilterCall(expression));
    }
    private static Call map(Expression expression) {
        return new Call(new MapCall(expression));
    }
    private static Expression binOp(Expression left, Operation op, Expression right) {
        return new Expression(new BinaryExpression(left, op, right));
    }

    private final static String[] correctsRaw = {
            "filter{(element>10)}%>%filter{(element<20)}",
            "filter{((element>10)&(element<20))}%>%map{element}",
            "map{(element+10)}%>%filter{(element>10)}%>%map{(element*element)}",
            "filter{((element+10)>10)}%>%map{((element+10)*(element+10))}",
            "map{(element+10)}%>%filter{(element>10)}%>%map{(element*element)}",
            "filter{(element>0)}%>%map{((element*element)+((element*20)+100))}",
            "filter{(element>0)}%>%filter{(element<0)}%>%map{(element*element)}",
            "filter{(1=0)}%>%map{element}",

            "filter{(((1=1)&(0=0))|((10=10)&(100>100)))}%>%map{(((element*element)-element)+element)}",
            "filter{(1=1)}",
            "map{element}",
            "map{(element+(1&0))}",
            "map{-1}"
    };

    private final static CallChain[] correctsParsed = {
            chain(filter(binOp(ELEMENT, GT, C10)), filter(binOp(ELEMENT, LT, C20))),
            chain(filter(binOp(binOp(ELEMENT, GT, C10), AND, binOp(ELEMENT, LT, C20))), map(ELEMENT)),
            chain(map(binOp(ELEMENT, PLUS, C10)), filter(binOp(ELEMENT, GT, C10)), map(binOp(ELEMENT, MUL, ELEMENT))),
            chain(filter(binOp(binOp(ELEMENT, PLUS, C10), GT, C10)), map(binOp(
                    binOp(ELEMENT, PLUS, C10),
                    MUL,
                    binOp(ELEMENT, PLUS, C10)
            ))),
            chain(map(binOp(ELEMENT, PLUS, C10)), filter(binOp(ELEMENT, GT, C10)), map(binOp(ELEMENT, MUL, ELEMENT))),
            chain(filter(binOp(ELEMENT, GT, C0)), map(binOp(
                    binOp(ELEMENT, MUL, ELEMENT),
                    PLUS,
                    binOp(binOp(ELEMENT, MUL, C20), PLUS, C100)
            ))),
            chain(filter(binOp(ELEMENT, GT, C0)), filter(binOp(ELEMENT, LT, C0)), map(binOp(ELEMENT, MUL, ELEMENT))),
            chain(filter(binOp(C1, EQ, C0)), map(ELEMENT)),

            chain(filter(binOp(
                    binOp(binOp(C1, EQ, C1), AND, binOp(C0, EQ, C0)),
                    OR,
                    binOp(binOp(C10, EQ, C10), AND, binOp(C100, GT, C100))
            )), map(binOp(binOp(binOp(ELEMENT, MUL, ELEMENT), MINUS, ELEMENT), PLUS, ELEMENT))),
            chain(filter(binOp(C1, EQ, C1))),
            chain(map(ELEMENT)),
            chain(map(binOp(ELEMENT, PLUS, binOp(C1, AND, C0)))),
            chain(map(CNeg1))
    };

    private static String[] wrongSyntax = {
            "filter{(element>=10)}%>%filter{(element<20)}",
            "filter{((element>10)&&(element<20))}%>%map{element}",
            "map{(element+10)}%%filter{(element>10)}%>%map{(element*element)}",
            "filter{((element+10>10)}%>%map{((element+10)*(element+10))}",
            "map{(element+10)}%>%filter(element>10)}%>%map{(element*element)}",
            "filter{(element>0)}%>%map{((element*elem)+((element*20)+100))}",
            "filter{(element>0)}%>%filter{(element<0)}%>%map{(element*element)",
            "filter{(1=0+1)}%>%map{element}",

            "filter{(((1=1)&(0=0))|((10=10)&(100>100)))}%>%",
            "filter{(1=1-1)}",
            "map{element*e;e,emt}",
            "map{-(element+(1&0))}",
            "map{-1-}"
    };

    private final static CallChain[] typeErrors = {
            chain(filter(binOp(ELEMENT, GT, TRUE)), filter(binOp(ELEMENT, LT, C20))),
            chain(filter(binOp(binOp(ELEMENT, GT, FALSE), AND, binOp(ELEMENT, LT, C20))), map(ELEMENT)),
            chain(map(binOp(TRUE, PLUS, C10)), filter(binOp(ELEMENT, GT, C10)), map(binOp(ELEMENT, MUL, ELEMENT))),
            chain(filter(binOp(binOp(TRUE, PLUS, C10), GT, C10)), map(binOp(
                    binOp(ELEMENT, PLUS, C10),
                    MUL,
                    binOp(ELEMENT, PLUS, C10)
            ))),
            chain(map(FALSE), filter(binOp(ELEMENT, GT, C10)), map(binOp(ELEMENT, MUL, ELEMENT))),
            chain(filter(binOp(ELEMENT, GT, C0)), map(binOp(
                    FALSE,
                    PLUS,
                    binOp(binOp(ELEMENT, MUL, C20), PLUS, C100)
            ))),
            chain(filter(C100), filter(binOp(ELEMENT, LT, C0)), map(binOp(ELEMENT, MUL, ELEMENT))),
            chain(filter(binOp(C1, EQ, C0)), map(TRUE)),

            chain(filter(binOp(
                    binOp(C20, AND, binOp(C0, EQ, C0)),
                    OR,
                    binOp(binOp(C10, EQ, C10), AND, binOp(C100, GT, C100))
            )), map(binOp(binOp(binOp(ELEMENT, MUL, ELEMENT), MINUS, ELEMENT), PLUS, ELEMENT))),
            chain(filter(binOp(C1, EQ, TRUE))),
            chain(map(FALSE)),
            chain(map(binOp(ELEMENT, PLUS, binOp(FALSE, AND, C0)))),
            chain(map(TRUE))
    };

    @Test
    public void testCorrectsParser() {
        for (int i = 0; i < correctsParsed.length; i++)
            Assertions.assertEquals(correctsParsed[i], ArrayOperationsParser.parse(correctsRaw[i]));
    }

    @Test
    public void testParserStress() {
        Random rand = new Random(2021);
        for (int i = 0; i < 300; i++) {
            CallChain chain = makeRandomCallChain(rand);
            Assertions.assertNotNull(chain);
            Assertions.assertEquals(chain, ArrayOperationsParser.parse(chain.toString()));
        }
    }

    @Test
    public void testSyntaxError() {
        for (String chain : wrongSyntax) {
            Assertions.assertNull(ArrayOperationsParser.parse(chain));
        }
    }

    @Test
    public void testTypeError() {
        for (CallChain chain : typeErrors) {
            Assertions.assertFalse(chain.validate());
        }
    }

    @Test
    public void testCorrectness() {
        int[] array = { 2, 4, 6, 8, 10, 12, 14, 16 };
        for (int i = 0; i < 8; i += 2) {
            int[] expected = apply(array, ArrayOperationsParser.parse(correctsRaw[i]));
            int[] actual = apply(array, ArrayOperationsParser.parse(correctsRaw[i + 1]));
            Assertions.assertArrayEquals(expected, actual);
        }
    }

    @Test
    public void testCorrectnessStress() {
        Random rand = new Random(2021);
        for (int j = 0; j < 10; j++) {
            int[] array = makeRandomIntArray(rand);
            for (int i = 0; i < 100; i++) {
                CallChain chain = makeRandomCallChain(rand);
                Assertions.assertNotNull(chain);
                int[] expected = apply(array, chain);
                int[] actual = apply(array, chain);
                Assertions.assertArrayEquals(expected, actual);
            }
        }
    }

    private static CallChain makeRandomCallChain(Random rand) {
        int size = rand.nextInt(30) + 1;
        Call[] calls = new Call[size];
        for (int i = 0; i < size; i++) {
            calls[i] = makeRandomCall(rand);
        }
        return chain(calls);
    }

    private static Call makeRandomCall(Random rand) {
        boolean isFilter = rand.nextBoolean();
        if (isFilter) return makeRandomFilter(rand);
        else return makeRandomMap(rand);
    }

    private static Call makeRandomFilter(Random rand) {
        return new Call(new FilterCall(makeRandomExpression(rand, Type.BOOLEAN)));
    }

    private static Call makeRandomMap(Random rand) {
        return new Call(new MapCall(makeRandomExpression(rand, Type.INTEGER)));
    }

    private final static char[] boolOperators = { '>', '<', '=', '&', '|' };
    private final static char[] integerOperator = { '+', '*', '-', 'C', 'C', 'C', 'E', 'E'};
    private static Expression makeRandomExpression(Random rand, Type type) {
        char operator;
        if (type == Type.BOOLEAN) {
            operator = boolOperators[rand.nextInt(boolOperators.length)];
        } else {
            operator = integerOperator[rand.nextInt(integerOperator.length)];
        }
        if (operator == 'C') return new Expression(new ConstantExpression(rand.nextInt(300)));
        if (operator == 'E') return ELEMENT;
        Operation op = new Operation(operator);
        return binOp(
                makeRandomExpression(rand, op.argumentType()),
                op,
                makeRandomExpression(rand, op.argumentType())
        );
    }

    private static int[] makeRandomIntArray(Random rand) {
        int length = rand.nextInt(50) + 50;
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = rand.nextInt(100);
        }
        return array;
    }

    private static int[] apply(int[] target, CallChain chain) {
        var stream = Arrays.stream(target);
        while (chain != null) {
            stream = apply(stream, chain.head);
            chain = chain.tail;
        }
        return stream.toArray();
    }

    private static IntStream apply(IntStream stream, Call call) {
        if (call.mapCall != null) return stream.map(exprToFuncInt(call.mapCall.expression));
        if (call.filterCall != null) return stream.filter(exprToFuncBool(call.filterCall.expression));
        throw new RuntimeException("Call is not correct, nothing is presented");
    }

    private static IntUnaryOperator exprToFuncInt(Expression expr) {
        if (expr.element != null) return n -> n;
        if (expr.constExpr != null) return n -> expr.constExpr.constant;
        if (expr.binExpr != null) return n -> {
            var left = exprToFuncInt(expr.binExpr.left);
            var right = exprToFuncInt(expr.binExpr.right);
            if (expr.binExpr.operation.operation == '+')
                return left.applyAsInt(n) + right.applyAsInt(n);
            if (expr.binExpr.operation.operation == '*')
                return left.applyAsInt(n) * right.applyAsInt(n);
            if (expr.binExpr.operation.operation == '-')
                return left.applyAsInt(n) - right.applyAsInt(n);
            throw new RuntimeException("There is something wrong with types: " +
                    "int X int -> int  and X is not +, * or -");
        };
        throw new RuntimeException("Expression is not correct, nothing is presented");
    }

    private static IntPredicate exprToFuncBool(Expression expr) {
        if (expr.element != null) throw new RuntimeException("Filter expected but element found");
        if (expr.constExpr != null) throw new RuntimeException("Filter expected but constant found");
        if (expr.binExpr != null) return n -> {
            if (expr.binExpr.operation.argumentType() == Type.BOOLEAN) {
                var left = exprToFuncBool(expr.binExpr.left);
                var right = exprToFuncBool(expr.binExpr.right);
                if (expr.binExpr.operation.operation == '|')
                    return left.test(n) || right.test(n);
                if (expr.binExpr.operation.operation == '&')
                    return left.test(n) && right.test(n);
                throw new RuntimeException("There is something wrong with types: " +
                        "bool X bool -> bool  and X is not & or |");
            } else {
                var left = exprToFuncInt(expr.binExpr.left);
                var right = exprToFuncInt(expr.binExpr.right);
                if (expr.binExpr.operation.operation == '<')
                    return left.applyAsInt(n) < right.applyAsInt(n);
                if (expr.binExpr.operation.operation == '>')
                    return left.applyAsInt(n) > right.applyAsInt(n);
                if (expr.binExpr.operation.operation == '=')
                    return left.applyAsInt(n) == right.applyAsInt(n);
                throw new RuntimeException("There is something wrong with types: " +
                    "int X int -> Bool  and X is not >, <, or =");
            }
        };
        throw new RuntimeException("Expression is not correct, nothing is presented");
    }
}
