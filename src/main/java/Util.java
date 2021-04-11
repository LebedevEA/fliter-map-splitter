import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.LinkedList;

class Util { // static object just to have some functions
    private Util() {}

    // just a bit faster
    static final int[] POWERS_OF_10 = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
    static int powerOfTen(int pow) {
        return POWERS_OF_10[pow];
    }

    static Operation AND = new Operation('&');

    static CallChain buildChain(@NotNull LinkedList<Call> ll) {
        if (ll.isEmpty()) return null;
        Call head = ll.remove();
        CallChain tail = buildChain(ll);
        if (tail == null) return new CallChain(head);
        else return new CallChain(head, tail);
    }

    static boolean canParseXCall(@NotNull StringLeftover toParse, String x) {
        try {
            if (!Literal.canParse(toParse, x + "{")) return false;
            var pairX = Literal.parse(toParse, x + "{");

            if (!Expression.canParse(pairX.leftover())) return false;
            var pairE = Expression.parse(pairX.leftover());

            if (!Symbol.canParse(pairE.leftover(), '}')) return false;
        } catch (ParseException pe) {
            var re = new RuntimeException("Parsing after toParse threw ParseException");
            re.addSuppressed(pe);
            throw re;
        }
        return true;
    }

    @NotNull
    static ParsePair<Expression> parseXCall(@NotNull StringLeftover toParse, String x) throws ParseException {
        if (!Literal.canParse(toParse, x + "{"))
            throw new ParseException("Could not parse " + x + "-call", toParse.offset());
        var pairX = Literal.parse(toParse, x + "{");

        if (!Expression.canParse(pairX.leftover()))
            throw new ParseException("Could not parse " + x + "-call", toParse.offset());
        var pairE = Expression.parse(pairX.leftover());

        if (!Symbol.canParse(pairE.leftover(), '}'))
            throw new ParseException("Could not parse " + x + "-call", toParse.offset());
        var pairC = Symbol.parse(pairE.leftover());

        return new ParsePair<>(pairE.parsed(), pairC.leftover());
    }
}
