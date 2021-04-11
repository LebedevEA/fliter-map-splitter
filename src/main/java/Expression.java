import java.text.ParseException;

public class Expression {
    public final Literal element;
    public final BinaryExpression binExpr;
    public final ConstantExpression constExpr;

    public Expression(Literal element) {
        if (!element.literal.equals("element"))
            throw new RuntimeException("Expressions' literal is not an \"element\"");
        this.element = element;
        this.binExpr = null;
        this.constExpr = null;
    }

    public Expression(BinaryExpression binExpr) {
        this.element = null;
        this.binExpr = binExpr;
        this.constExpr = null;
    }


    public Expression(ConstantExpression constExpr) {
        this.element = null;
        this.binExpr = null;
        this.constExpr = constExpr;
    }

    @Override
    public String toString() {
        if (element != null) return element.toString();
        if (binExpr != null) return binExpr.toString();
        if (constExpr != null) return constExpr.toString();
        throw new RuntimeException("Expression is not correct: none of three members presented");
    }

    public Expression subst(Expression expression) {
        if (element != null) return expression;
        if (binExpr != null) return new Expression(binExpr.subst(expression));
        if (constExpr != null) return this;
        throw new RuntimeException("Expression is not correct: none of three members presented");
    }

    public boolean typeCheck(Type type) {
        if (element != null) return type.equals(Type.INTEGER);
        if (binExpr != null) return binExpr.typeCheck(type);
        if (constExpr != null) return type.equals(Type.INTEGER);
        throw new RuntimeException("Expression is not correct: none of three members presented");
    }

    public static boolean canParse(StringLeftover toParse) {
        return Literal.canParse(toParse, "element") ||
                BinaryExpression.canParse(toParse) || ConstantExpression.canParse(toParse);
    }

    public static ParsePair<Expression> parse(StringLeftover toParse) throws ParseException {
        if (Literal.canParse(toParse, "element")) {
            var pair = Literal.parse(toParse, "element");
            return new ParsePair<>(new Expression(pair.parsed()), pair.leftover());
        } else if (BinaryExpression.canParse(toParse)) {
            var pair = BinaryExpression.parse(toParse);
            return new ParsePair<>(new Expression(pair.parsed()), pair.leftover());
        } else if (ConstantExpression.canParse(toParse)) {
            var pair = ConstantExpression.parse(toParse);
            return new ParsePair<>(new Expression(pair.parsed()), pair.leftover());
        } else {
            throw new ParseException("Could not parse expression", toParse.offset());
        }
    }
}
