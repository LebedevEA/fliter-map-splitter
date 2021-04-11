import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CallChain {
    @NotNull
    public final Call head;
    public final CallChain tail;

    private final static Map<StringLeftover, Boolean> attempted = new HashMap<>();
    private final static Map<StringLeftover, ParsePair<CallChain>> done = new HashMap<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallChain callChain = (CallChain) o;
        return head.equals(callChain.head) && Objects.equals(tail, callChain.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }

    public boolean validate() {
        if (tail == null) {
            return head.validate();
        } else {
            return head.validate() && tail.validate();
        }
    }

    public static boolean canParse(@NotNull StringLeftover toParse) {
        Boolean prev = attempted.get(toParse);
        if (prev != null) return prev;
        var rv = Call.canParse(toParse);
        attempted.put(toParse, rv);
        return rv;
    }

    @NotNull
    public static ParsePair<CallChain> parse(@NotNull StringLeftover toParse) throws ParseException {
        var prev = done.get(toParse);
        if (prev != null) return prev;
        if (!canParse(toParse))
            throw new ParseException("Could not parse call-chain", toParse.offset());
        var pairH = Call.parse(toParse);

        if (!Literal.canParse(pairH.leftover(), "%>%")) {
            var rv = new ParsePair<>(new CallChain(pairH.parsed()), pairH.leftover());
            done.put(toParse, rv);
            return rv;
        }
        var pairS = Literal.parse(pairH.leftover(), "%>%");

        if (canParse(pairS.leftover())) {
            var pairT = CallChain.parse(pairS.leftover());
            var rv = new ParsePair<>(new CallChain(pairH.parsed(), pairT.parsed()), pairT.leftover());
            done.put(toParse, rv);
            return rv;
        } else {
            var rv = new ParsePair<>(new CallChain(pairH.parsed()), pairH.leftover());
            done.put(toParse, rv);
            return rv;
        }
    }
}
