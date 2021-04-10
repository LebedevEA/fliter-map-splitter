import java.text.ParseException;

public class Digit {
    public final int digit;

    public Digit(int digit) {
        if (digit < 0 || digit > 9)
            throw new RuntimeException("Digit is not a digit.");
        this.digit = digit;
    }

    public static ParsePair<Digit> parse(String toParse) throws ParseException {
        throw new ParseException("",1);
    }
}
