import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CallChain {
    @NotNull
    public final Call head;
    public final CallChain tail;

    public CallChain(@NotNull Call head) {
        this.head = head;
        this.tail = null;
    }

    public CallChain(@NotNull Call head, @NotNull CallChain tail) {
        this.head = head;
        this.tail = tail;
    }

    public CallChain(@NotNull Call... calls) {
        if (calls.length == 0) throw new IllegalArgumentException("Cannot create empty CallChain");
        LinkedList<Call> ll = Arrays.stream(calls).collect(Collectors.toCollection(LinkedList::new));
        CallChain cc = Util.buildChain(ll);
        if (cc == null) throw new RuntimeException("buildChain did not build chain but had to");
        head = cc.head;
        tail = cc.tail;
    }

    @Override
    public String toString() {
        if (tail == null) return head.toString();
        else return head + "%>%" + tail;
    }

    public boolean validate() {
        if (tail == null) {
            return head.validate();
        } else {
            return head.validate() && tail.validate();
        }
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        return Call.canParse(toParse);
    }

    @NotNull
    public static ParsePair<CallChain> parse(@NotNull StringLeftover toParse) throws ParseException {
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
