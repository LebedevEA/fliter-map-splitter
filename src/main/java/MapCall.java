import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapCall that = (MapCall) o;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
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
