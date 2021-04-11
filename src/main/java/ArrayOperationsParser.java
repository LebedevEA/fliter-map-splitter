import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class ArrayOperationsParser {
    @NotNull
    private final StringLeftover toParse;
    private ParsePair<CallChain> callChain = null;
    private boolean parsed = false;

    public ArrayOperationsParser(@NotNull String operation) {
        toParse = new StringLeftover(operation);
    }

    public CallChain parse() throws ParseException {
        if (!parsed && CallChain.canParse(toParse)) {
            var pair = CallChain.parse(toParse);
            if (pair.leftover().left() == 0) callChain = pair;
        }
        parsed = true;
        return callChain == null ? null : callChain.parsed();
    }

    public static CallChain parse(@NotNull String operation) {
        try {
            var aop = new ArrayOperationsParser(operation);
            return aop.parse();
        } catch (ParseException e) {
            return null;
        }
    }
}
