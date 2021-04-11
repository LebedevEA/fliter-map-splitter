import javax.swing.text.html.Option;
import java.text.ParseException;
import java.util.Optional;

public class ArrayOperationsParser {
    private final StringLeftover toParse;
    private ParsePair<CallChain> callChain = null;
    private boolean parsed = false;

    public ArrayOperationsParser(String operation) {
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

    public static CallChain parse(String operation) throws ParseException {
        var aop = new ArrayOperationsParser(operation);
        return aop.parse();
    }
}
