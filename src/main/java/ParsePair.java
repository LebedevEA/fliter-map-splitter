public class ParsePair<T> {
    private final T parsed;
    private final StringLeftover leftover;

    public ParsePair(T parsed, StringLeftover leftover) {
        this.parsed = parsed;
        this.leftover = leftover;
    }

    public T parsed() {
        return parsed;
    }

    public StringLeftover leftover() {
        return leftover;
    }
}
