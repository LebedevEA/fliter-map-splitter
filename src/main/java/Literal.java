import java.text.ParseException;

public class Literal {
    public final String literal;

    public Literal(String literal) {
        this.literal = literal;
    }

    @Override
    public String toString() {
        return literal;
    }

    public static boolean canParse(StringLeftover toParse, String goal) {
        int i = 0;
        while (i < goal.length() && i < toParse.left() && toParse.charAt(i) == goal.charAt(i)) {
            i++;
        }
        return i == goal.length();
    }

    public static ParsePair<Literal> parse(StringLeftover toParse, String goal) throws ParseException {
        if (!canParse(toParse, goal))
            throw new ParseException("Could not parse literal equals to " + goal, toParse.offset());

        return new ParsePair<>(new Literal(goal), toParse.skip(goal.length()));
    }
}
