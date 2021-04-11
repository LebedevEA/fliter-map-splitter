import java.util.*;

public class Compressor {
    private Compressor() {}

    static Call defaultFilter = Objects.requireNonNull(ArrayOperationsParser.parse("filter{(0=0)}")).head;
    static Call defaultMap = Objects.requireNonNull(ArrayOperationsParser.parse("map{element}")).head;

    public static CallChain compress(CallChain chain) {
        Call curElement = defaultMap;
        Call curFilter = defaultFilter;

        while (chain != null) {
            if (chain.head.mapCall != null) {
                curElement = new Call(chain.head.mapCall.subst(curElement.mapCall.expression));
            } else if (chain.head.filterCall != null) {
                var curFilterExpr = curFilter.filterCall.expression;
                var curHeadExpr = chain.head.filterCall.expression;
                curFilter = new Call(new FilterCall(new Expression(
                        new BinaryExpression(curFilterExpr, Util.AND, curHeadExpr.subst(curElement.mapCall.expression))
                )));
            } else {
                throw new RuntimeException("Call is not correct: none of two members presented");
            }
            chain = chain.tail;
        }
        return new CallChain(curFilter, curElement);
    }
}
