import java.text.ParseException;

public class FilterCall {
    public final Expression expression;

    public FilterCall(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "filter{" + expression + "}";
    }

    public boolean validate() {
        return expression.typeCheck(Type.BOOLEAN);
    }

    public static boolean canParse(StringLeftover toParse) {
        return Util.canParseXCall(toParse, "filter");
    }

    public static ParsePair<FilterCall> parse(StringLeftover toParse) throws ParseException {
        var pair = Util.parseXCall(toParse, "filter");
        return new ParsePair<>(new FilterCall(pair.parsed()), pair.leftover());
    }
}
