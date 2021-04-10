import java.text.ParseException;

public class Number {
    private final int number;

    public Number(int number) {
        this.number = number;
    }

    private int number() {
        return number;
    }

    public static boolean canParse(StringLeftover toParse) {
        return toParse.left() > 0 && Character.isDigit(toParse.charAt(0));
    }

    public static ParsePair<Number> parse(StringLeftover toParse) throws ParseException {
        if (toParse.left() <= 0)
            throw new ParseException("Trying to parse number in empty string", toParse.offset());
        if (Character.isDigit(toParse.charAt(0)))
            throw new ParseException("Number has to start with digit", toParse.offset());

        var pair1 = Digit.parse(toParse);

        if (canParse(pair1.leftover())) {
            var pair2 = Number.parse(pair1.leftover());
            int sizeOf2 = String.valueOf(pair2.parsed()).length();
            int merged = pair2.parsed().number() + pair1.parsed().digit() * powerOfTen(sizeOf2);
            return new ParsePair<>(new Number(merged), pair2.leftover());
        } else {
            return new ParsePair<>(new Number(pair1.parsed().digit()), pair1.leftover());
        }
    }

    // just a bit faster
    private static final int[] POWERS_OF_10 =
            { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
    private static int powerOfTen(int pow) {
        return POWERS_OF_10[pow];
    }
}
