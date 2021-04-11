import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

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
