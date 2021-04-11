import java.text.ParseException;

public class Number {
    private final int number;

    public Number(int number) {
        this.number = number;
    }

    public int number() {
        return number;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public static boolean canParse(StringLeftover toParse) {
        return Symbol.canParse(toParse, Character::isDigit);
    }

    public static ParsePair<Number> parse(StringLeftover toParse) throws ParseException {
        if (!Symbol.canParse(toParse, Character::isDigit))
            throw new ParseException("Could not parse number", toParse.offset());

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
