import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class Literal {
    @NotNull
    public final String literal;

    public Literal(@NotNull String literal) {
        this.literal = literal;
    }

    @Override
    public String toString() {
        return literal;
    }

    public static boolean canParse(@NotNull StringLeftover toParse, @NotNull String goal) {
        int i = 0;
        while (i < goal.length() && i < toParse.left() && toParse.charAt(i) == goal.charAt(i)) {
            i++;
        }
        return i == goal.length();
    }

    @NotNull
    public static ParsePair<Literal> parse(@NotNull StringLeftover toParse, @NotNull String res) throws ParseException {
        if (!canParse(toParse, res))
            throw new ParseException("Could not parse literal equals to " + res, toParse.offset());

        return new ParsePair<>(new Literal(res), toParse.skip(res.length()));
    }
}
