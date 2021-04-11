import java.text.ParseException;

public class Number {
    public final int number;

    public Number(int number) {
        this.number = number;
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
            int merged = pair2.parsed().number + pair1.parsed().digit * Util.powerOfTen(sizeOf2);
            return new ParsePair<>(new Number(merged), pair2.leftover());
        } else {
            return new ParsePair<>(new Number(pair1.parsed().digit), pair1.leftover());
        }
    }
}
