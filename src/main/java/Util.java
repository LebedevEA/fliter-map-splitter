import java.text.ParseException;

// static object just to have some functions
class Util {
    private Util() {}

    // just a bit faster
    static final int[] POWERS_OF_10 = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
    static int powerOfTen(int pow) {
        return POWERS_OF_10[pow];
    }

    static boolean canParseXCall(StringLeftover toParse, String x) {
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

    static ParsePair<Expression> parseXCall(StringLeftover toParse, String x) throws ParseException {
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
