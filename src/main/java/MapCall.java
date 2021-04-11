import java.text.ParseException;

public class MapCall {
    public final Expression expression;

    public MapCall(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "map{" + expression + "}";
    }

    public MapCall subst(Expression expression1) {
        return new MapCall(expression.subst(expression1));
    }

    public boolean validate() {
        return expression.typeCheck(Type.INTEGER);
    }

    public static boolean canParse(StringLeftover toParse) {
        return Util.canParseXCall(toParse, "map");
    }

    public static ParsePair<MapCall> parse(StringLeftover toParse) throws ParseException {
        var pair = Util.parseXCall(toParse, "map");
        return new ParsePair<>(new MapCall(pair.parsed()), pair.leftover());
    }
}
