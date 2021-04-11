import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BinaryExpression {
    @NotNull
    public final Expression left;
    @NotNull
    public final Operation operation;
    @NotNull
    public final Expression right;

    private final static Map<StringLeftover, Boolean> attempted = new HashMap<>();
    private final static Map<StringLeftover, ParsePair<BinaryExpression>> done = new HashMap<>();

    public BinaryExpression(@NotNull Expression left, @NotNull Operation operation, @NotNull Expression right) {
        this.left = left;
        this.operation = operation;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + operation + right + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryExpression that = (BinaryExpression) o;
        return left.equals(that.left) && operation.equals(that.operation) && right.equals(that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, operation, right);
    }

    @NotNull
    public BinaryExpression subst(@NotNull Expression expression) {
        return new BinaryExpression(left.subst(expression), operation, right.subst(expression));
    }

    public boolean typeCheck(@NotNull Type type) {
        return operation.returnType().equals(type) &&
                left.typeCheck(operation.argumentType()) && left.typeCheck(operation.argumentType());
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        Boolean prev = attempted.get(toParse);
        if (prev != null) return prev;
        try {
            if (!Symbol.canParse(toParse, '(')) {
                attempted.put(toParse, false);
                return false;
            }
            var pairOB = Symbol.parse(toParse);

            if (!Expression.canParse(pairOB.leftover())) {
                attempted.put(toParse, false);
                return false;
            }
            var pairE1 = Expression.parse(pairOB.leftover());

            if (!Operation.canParse(pairE1.leftover())) {
                attempted.put(toParse, false);
                return false;
            }
            var pairOP = Operation.parse(pairE1.leftover());

            if (!Expression.canParse(pairOP.leftover())) {
                attempted.put(toParse, false);
                return false;
            }
            var pairE2 = Expression.parse(pairOP.leftover());

            if (!Symbol.canParse(pairE2.leftover(), ')')) {
                attempted.put(toParse, false);
                return false;
            }
        } catch (ParseException pe) {
            var re = new RuntimeException("Parsing after toParse threw ParseException");
            re.addSuppressed(pe);
            throw re;
        }
        attempted.put(toParse, true);
        return true;
    }

    @NotNull
    public static ParsePair<BinaryExpression> parse(@NotNull StringLeftover toParse) throws ParseException {
        var prev = done.get(toParse);
        if (prev != null) return prev;
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
        var rv = new ParsePair<>(binExpr, pairC.leftover());
        done.put(toParse, rv);
        return rv;
    }
}
