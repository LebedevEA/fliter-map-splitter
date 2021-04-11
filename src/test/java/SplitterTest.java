import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

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

    @Test
    public void testCorrects() {
        for (int i = 0; i < correctsParsed.length; i++)
            Assertions.assertEquals(correctsParsed[i], ArrayOperationsParser.parse(correctsRaw[i]));
    }

    @Test
    public void testCorrectStress() {
        Random rand = new Random(2021);
        for (int i = 0; i < 300; i++) {
            CallChain chain = makeRandomCallChain(rand);
            Assertions.assertNotNull(chain);
            Assertions.assertEquals(chain, ArrayOperationsParser.parse(chain.toString()));
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
}
