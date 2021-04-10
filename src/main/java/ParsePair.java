public class ParsePair<T> {
    public final T parsed;
    public final String toParse;
    public final int start;

    public ParsePair(T parsed, String leftover, pos) {
        this.parsed = parsed;
        this.leftover = leftover;
    }
}
