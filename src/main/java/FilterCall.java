import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Objects;

public class FilterCall {
    @NotNull
    public final Expression expression;

    public FilterCall(@NotNull Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "filter{" + expression + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterCall that = (FilterCall) o;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    public boolean validate() {
        return expression.typeCheck(Type.BOOLEAN);
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return Util.canParseXCall(toParse, "filter");
    }

    @NotNull
    public static ParsePair<FilterCall> parse(@NotNull StringLeftover toParse) throws ParseException {
        var pair = Util.parseXCall(toParse, "filter");
        return new ParsePair<>(new FilterCall(pair.parsed()), pair.leftover());
    }
}
