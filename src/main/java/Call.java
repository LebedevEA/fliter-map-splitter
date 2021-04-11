import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Call {
    public final MapCall mapCall;
    public final FilterCall filterCall;

    private final static Map<StringLeftover, Boolean> attempted = new HashMap<>();
    private final static Map<StringLeftover, ParsePair<Call>> done = new HashMap<>();

    public Call(@NotNull MapCall mapCall) {
        this.mapCall = mapCall;
        this.filterCall = null;
    }

    public Call(@NotNull FilterCall filterCall) {
        this.mapCall = null;
        this.filterCall = filterCall;
    }

    @Override
    public String toString() {
        if (mapCall != null) return mapCall.toString();
        if (filterCall != null) return filterCall.toString();
        throw new RuntimeException("Call is not correct: none of two members presented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return Objects.equals(mapCall, call.mapCall) && Objects.equals(filterCall, call.filterCall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapCall, filterCall);
    }

    public boolean validate() {
        if (mapCall != null) return mapCall.validate();
        if (filterCall != null) return filterCall.validate();
        throw new RuntimeException("Call is not correct: none of two members presented");
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        Boolean prev = attempted.get(toParse);
        if (prev != null) return prev;
        var rv = MapCall.canParse(toParse) || FilterCall.canParse(toParse);
        attempted.put(toParse, rv);
        return rv;
    }

    @NotNull
    public static ParsePair<Call> parse(@NotNull StringLeftover toParse) throws ParseException {
        var prev = done.get(toParse);
        if (prev != null) return prev;
        if (MapCall.canParse(toParse)) {
            var pair = MapCall.parse(toParse);
            var rv = new ParsePair<>(new Call(pair.parsed()), pair.leftover());
            done.put(toParse, rv);
            return rv;
        } else if (FilterCall.canParse(toParse)) {
            var pair = FilterCall.parse(toParse);
            var rv = new ParsePair<>(new Call(pair.parsed()), pair.leftover());
            done.put(toParse, rv);
            return rv;
        } else {
            throw new ParseException("Could not parse call", toParse.offset());
        }
    }
}
