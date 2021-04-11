import java.text.ParseException;

public class BinaryExpression {
    public final Expression left;
    public final Operation operation;
    public final Expression right;

    public BinaryExpression(Expression left, Operation operation, Expression right) {
        this.left = left;
        this.operation = operation;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + operation + right + ")";
    }

    public BinaryExpression subst(Expression expression) {
        return new BinaryExpression(left.subst(expression), operation, right.subst(expression));
    }

    public boolean typeCheck(Type type) {
        return operation.returnType().equals(type) &&
                left.typeCheck(operation.argumentType()) && left.typeCheck(operation.argumentType());
    }

    public static boolean canParse(StringLeftover toParse) {
        try {
            if (!Symbol.canParse(toParse, '(')) return false;
            var pairOB = Symbol.parse(toParse);

            if (!Expression.canParse(pairOB.leftover())) return false;
            var pairE1 = Expression.parse(pairOB.leftover());

            if (!Operation.canParse(pairE1.leftover())) return false;
            var pairOP = Operation.parse(pairE1.leftover());

            if (!Expression.canParse(pairOP.leftover())) return false;
            var pairE2 = Expression.parse(pairOP.leftover());

            if (!Symbol.canParse(pairE2.leftover(), ')')) return false;
        } catch (ParseException pe) {
            var re = new RuntimeException("Parsing after toParse threw ParseException");
            re.addSuppressed(pe);
            throw re;
        }
        return true;
    }

    public static ParsePair<BinaryExpression> parse(StringLeftover toParse) throws ParseException {
        if (!Symbol.canParse(toParse, '('))
            throw new ParseException("Could not parse binary expression", toParse.offset());
        var pairOB = Symbol.parse(toParse);

        if (!Expression.canParse(pairOB.leftover()))
            throw new ParseException("Could not parse binary expression", pairOB.leftover().offset());
        var pairE1 = Expression.parse(pairOB.leftover());

        if (!Operation.canParse(pairE1.leftover()))
            throw new ParseException("Could not parse binary expression", pairE1.leftover().offset());
        var pairOP = Operation.parse(pairE1.leftover());

        if (!Expression.canParse(pairOP.leftover()))
            throw new ParseException("Could not parse binary expression", pairOP.leftover().offset());
        var pairE2 = Expression.parse(pairOP.leftover());

        if (!Symbol.canParse(pairE2.leftover(), ')'))
            throw new ParseException("Could not parse binary expression", pairE2.leftover().offset());
        var pairC = Symbol.parse(pairE2.leftover());

        var binExpr = new BinaryExpression(pairE1.parsed(), pairOP.parsed(), pairE2.parsed());
        return new ParsePair<>(binExpr, pairC.leftover());
    }
}
