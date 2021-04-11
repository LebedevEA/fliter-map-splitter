import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.function.Predicate;

public class Symbol {
    public final char symbol;

    public Symbol(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public  String toString() {
        return String.valueOf(symbol);
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return toParse.left() > 0;
    }

    public static boolean canParse(@NotNull StringLeftover toParse, char goal) {
        return toParse.left() > 0 && toParse.charAt(0) == goal;
    }

    public static boolean canParse(@NotNull StringLeftover toParse, @NotNull String regex) {
        return toParse.left() > 0 && String.valueOf(toParse.charAt(0)).matches(regex);
    }

    public static boolean canParse(@NotNull StringLeftover toParse, @NotNull Predicate<Character> p) {
        return toParse.left() > 0 && p.test(toParse.charAt(0));
    }

    @NotNull
    public static ParsePair<Symbol> parse(@NotNull StringLeftover toParse) throws ParseException {
        if (toParse.left() <= 0)
            throw new ParseException("Trying to parse symbol in empty string", toParse.offset());

        return new ParsePair<>(new Symbol(toParse.charAt(0)), toParse.skip(1));
    }
}
