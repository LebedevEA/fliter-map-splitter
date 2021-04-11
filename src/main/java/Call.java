import java.text.ParseException;
import java.util.Map;

public class Call {
    public final MapCall mapCall;
    public final FilterCall filterCall;

    public Call(MapCall mapCall) {
        this.mapCall = mapCall;
        this.filterCall = null;
    }

    public Call(FilterCall filterCall) {
        this.mapCall = null;
        this.filterCall = filterCall;
    }

    @Override
    public String toString() {
        if (mapCall != null) return mapCall.toString();
        if (filterCall != null) return filterCall.toString();
        throw new RuntimeException("Call is not correct: none of two members presented");
    }

    public static boolean canParse(StringLeftover toParse) {
        return MapCall.canParse(toParse) || FilterCall.canParse(toParse);
    }

    public static ParsePair<Call> parse(StringLeftover toParse) throws ParseException {
        if (MapCall.canParse(toParse)) {
            var pair = MapCall.parse(toParse);
            return new ParsePair<>(new Call(pair.parsed()), pair.leftover());
        } else if (FilterCall.canParse(toParse)) {
            var pair = FilterCall.parse(toParse);
            return new ParsePair<>(new Call(pair.parsed()), pair.leftover());
        } else {
            throw new ParseException("Could not parse call", toParse.offset());
        }
    }
}
