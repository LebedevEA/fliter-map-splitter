import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class MapCall {
    @NotNull
    public final Expression expression;

    public MapCall(@NotNull Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "map{" + expression + "}";
    }

    @NotNull
    public MapCall subst(@NotNull Expression expression1) {
        return new MapCall(expression.subst(expression1));
    }

    public boolean validate() {
        return expression.typeCheck(Type.INTEGER);
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return Util.canParseXCall(toParse, "map");
    }

    @NotNull
    public static ParsePair<MapCall> parse(@NotNull StringLeftover toParse) throws ParseException {
        var pair = Util.parseXCall(toParse, "map");
        return new ParsePair<>(new MapCall(pair.parsed()), pair.leftover());
    }
}
