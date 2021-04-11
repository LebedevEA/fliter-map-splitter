import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Objects;

public class Number {
    @NotNull
    private String number;

    public Number(@NotNull String number) {
        this.number = number;
    }

    public String number() {
        return number;
    }

    @Override
    public String toString() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number that = (Number) o;
        return number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return Symbol.canParse(toParse, Character::isDigit);
    }

    @NotNull
    public static ParsePair<Number> parse(@NotNull StringLeftover toParse) throws ParseException {
        if (!Symbol.canParse(toParse, Character::isDigit))
            throw new ParseException("Could not parse number", toParse.offset());

        var pair1 = Digit.parse(toParse);

        if (canParse(pair1.leftover())) {
            var pair2 = Number.parse(pair1.leftover());
            int sizeOf2 = String.valueOf(pair2.parsed()).length();
            String merged = pair1.parsed() + pair2.parsed().number;
            return new ParsePair<>(new Number(merged), pair2.leftover());
        } else {
            return new ParsePair<>(new Number(pair1.parsed().toString()), pair1.leftover());
        }
    }
}
