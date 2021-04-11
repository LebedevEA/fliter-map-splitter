import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class ConstantExpression {
    public final int constant;

    public ConstantExpression(int constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return String.valueOf(constant);
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return Number.canParse(toParse) || Symbol.canParse(toParse, "\\-") && Number.canParse(toParse.skip(1));
    }

    @NotNull
    public static ParsePair<ConstantExpression> parse(@NotNull StringLeftover toParse) throws ParseException  {
        if (Number.canParse(toParse)) {
            var pair = Number.parse(toParse);
            return new ParsePair<>(new ConstantExpression(pair.parsed().number), pair.leftover());
        } else if (Symbol.canParse(toParse, '-')) {
            var pairM = Symbol.parse(toParse);
            var pair = Number.parse(pairM.leftover());
            return new ParsePair<>(new ConstantExpression(-pair.parsed().number), pair.leftover());
        } else {
            throw new ParseException("Could not parse constant expression", toParse.offset());
        }
    }
}
