import java.text.ParseException;

public class Digit {
    private final int digit;

    public Digit(int digit) {
        if (digit < 0 || digit > 9)
            throw new RuntimeException("Digit is not a digit.");
        this.digit = digit;
    }

    public int digit() {
        return digit;
    }

    public static boolean canParse(StringLeftover toParse) {
        return toParse.left() > 0 && Character.isDigit(toParse.charAt(0));
    }

    public static ParsePair<Digit> parse(StringLeftover toParse) throws ParseException {
        if (toParse.left() <= 0)
            throw new ParseException("Trying to parse digit in empty string", toParse.offset());
        if (Character.isDigit(toParse.charAt(0)))
            throw new ParseException("Digit is not actually a digit", toParse.offset());

        return new ParsePair<>(new Digit(Character.getNumericValue(toParse.charAt(0))), toParse.skip(1));
    }
}
