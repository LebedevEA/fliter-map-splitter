import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Objects;

public class ConstantExpression {
    public final int constant;

    public ConstantExpression(int constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return String.valueOf(constant);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantExpression that = (ConstantExpression) o;
        return constant == that.constant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constant);
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return Number.canParse(toParse) || Symbol.canParse(toParse, '-') && Number.canParse(toParse.skip(1));
    }

    @NotNull
    public static ParsePair<ConstantExpression> parse(@NotNull StringLeftover toParse) throws ParseException  {
        if (Number.canParse(toParse)) {
            var pair = Number.parse(toParse);
            return new ParsePair<>(new ConstantExpression(Integer.parseInt(pair.parsed().number())), pair.leftover());
        } else if (Symbol.canParse(toParse, '-')) {
            var pairM = Symbol.parse(toParse);
            var pair = Number.parse(pairM.leftover());
            return new ParsePair<>(new ConstantExpression(-Integer.parseInt(pair.parsed().number())), pair.leftover());
        } else {
            throw new ParseException("Could not parse constant expression", toParse.offset());
        }
    }
}
