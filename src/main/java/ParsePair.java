import org.jetbrains.annotations.NotNull;

public class ParsePair<T> {
    @NotNull
    private final T parsed;
    @NotNull
    private final StringLeftover leftover;

    public ParsePair(@NotNull T parsed, @NotNull StringLeftover leftover) {
        this.parsed = parsed;
        this.leftover = leftover;
    }

    @NotNull
    public T parsed() {
        return parsed;
    }

    @NotNull
    public StringLeftover leftover() {
        return leftover;
    }
}
