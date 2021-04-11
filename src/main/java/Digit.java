import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Objects;

public class Digit {
    public final int digit;

    public Digit(int digit) {
        if (digit < 0 || digit > 9)
            throw new RuntimeException("Digit is not a digit.");
        this.digit = digit;
    }

    @Override
    public String toString() {
        return String.valueOf(digit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Digit that = (Digit) o;
        return digit == that.digit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(digit);
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return toParse.left() > 0 && Character.isDigit(toParse.charAt(0));
    }

    @NotNull
    public static ParsePair<Digit> parse(@NotNull StringLeftover toParse) throws ParseException {
        if (!Symbol.canParse(toParse, Character::isDigit))
            throw new ParseException("Could not parse digit", toParse.offset());

        var pair = Symbol.parse(toParse);
        return new ParsePair<>(new Digit(Character.getNumericValue(pair.parsed().symbol)), pair.leftover());
    }
}
