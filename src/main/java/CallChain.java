import java.text.ParseException;

public class CallChain {
    public final Call head;
    public final CallChain tail;

    public CallChain(Call head) {
        this.head = head;
        this.tail = null;
    }

    public CallChain(Call head, CallChain tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public String toString() {
        if (tail == null) return head.toString();
        else return head + "%>%" + tail;
    }

    public static boolean canParse(StringLeftover toParse) {
        return Call.canParse(toParse);
    }

    public static ParsePair<CallChain> parse(StringLeftover toParse) throws ParseException {
        if (!canParse(toParse))
            throw new ParseException("Could not parse call-chain", toParse.offset());
        var pairH = Call.parse(toParse);

        if (!Literal.canParse(pairH.leftover(), "%>%"))
            return new ParsePair<>(new CallChain(pairH.parsed()), pairH.leftover());
        var pairS = Literal.parse(pairH.leftover(), "%>%");

        if (canParse(pairS.leftover())) {
            var pairT = CallChain.parse(pairS.leftover());
            return new ParsePair<>(new CallChain(pairH.parsed(), pairT.parsed()), pairT.leftover());
        } else {
            return new ParsePair<>(new CallChain(pairH.parsed()), pairH.leftover());
        }
    }
}
