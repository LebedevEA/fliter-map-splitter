import java.text.ParseException;

public class Digit {
    public final int digit;

    public Digit(int digit) {
        if (digit < 0 || digit > 9)
            throw new RuntimeException("Digit is not a digit.");
        this.digit = digit;
    }

    public static ParsePair<Digit> parse(StringLeftover toParse) throws ParseException {
        int digit = Character.getNumericValue(toParse.charAt(0));
        if (digit < 0 || digit > 9)
            throw new ParseException("Digit is not actually a digit", toParse.offset());
        return new ParsePair<>(new Digit(digit), toParse.skip(1));
    }
}
