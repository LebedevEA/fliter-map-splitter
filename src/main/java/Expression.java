import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Expression {
    public final Literal element;
    public final BinaryExpression binExpr;
    public final ConstantExpression constExpr;

    // optimization...
    private final static Map<StringLeftover, Boolean> attempted = new HashMap<>();
    private final static Map<StringLeftover, ParsePair<Expression>> done = new HashMap<>();

    public Expression(@NotNull Literal element) {
        if (!element.literal.equals("element"))
            throw new RuntimeException("Expressions' literal is not an \"element\"");
        this.element = element;
        this.binExpr = null;
        this.constExpr = null;
    }

    public Expression(@NotNull BinaryExpression binExpr) {
        this.element = null;
        this.binExpr = binExpr;
        this.constExpr = null;
    }

    public Expression(@NotNull ConstantExpression constExpr) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expression that = (Expression) o;
        return Objects.equals(element, that.element) &&
                Objects.equals(binExpr, that.binExpr) && Objects.equals(constExpr, that.constExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, binExpr, constExpr);
    }

    @NotNull
    public Expression subst(@NotNull Expression expression) {
        if (element != null) return expression;
        if (binExpr != null) return new Expression(binExpr.subst(expression));
        if (constExpr != null) return this;
        throw new RuntimeException("Expression is not correct: none of three members presented");
    }

    public boolean typeCheck(@NotNull Type type) {
        if (element != null) return type.equals(Type.INTEGER);
        if (binExpr != null) return binExpr.typeCheck(type);
        if (constExpr != null) return type.equals(Type.INTEGER);
        throw new RuntimeException("Expression is not correct: none of three members presented");
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        Boolean prev = attempted.get(toParse);
        if (prev != null) return prev;
        var rv = Literal.canParse(toParse, "element") ||
                BinaryExpression.canParse(toParse) || ConstantExpression.canParse(toParse);
        attempted.put(toParse, rv);
        return rv;
    }

    @NotNull
    public static ParsePair<Expression> parse(@NotNull StringLeftover toParse) throws ParseException {
        var prev = done.get(toParse);
        if (prev != null) return prev;
        if (Literal.canParse(toParse, "element")) {
            var pair = Literal.parse(toParse, "element");
            var rv = new ParsePair<>(new Expression(pair.parsed()), pair.leftover());
            done.put(toParse, rv);
            return rv;
        } else if (BinaryExpression.canParse(toParse)) {
            var pair = BinaryExpression.parse(toParse);
            var rv = new ParsePair<>(new Expression(pair.parsed()), pair.leftover());
            done.put(toParse, rv);
            return rv;
        } else if (ConstantExpression.canParse(toParse)) {
            var pair = ConstantExpression.parse(toParse);
            var rv = new ParsePair<>(new Expression(pair.parsed()), pair.leftover());
            done.put(toParse, rv);
            return rv;
        } else {
            throw new ParseException("Could not parse expression", toParse.offset());
        }
    }
}
