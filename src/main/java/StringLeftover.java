import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StringLeftover {
    @NotNull
    private final String base;
    private final int offset;

    public StringLeftover(@NotNull String base) {
        this.base = base;
        this.offset = 0;
    }

    public StringLeftover(@NotNull StringLeftover s, int skip) {
        base = s.base;
        offset = s.offset + skip;
    }

    @Override
    public String toString() {
        return base.substring(offset);
    }

    @Override
    public boolean equals(Object o) { // memorize
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLeftover that = (StringLeftover) o;
        return base == that.base && offset == that.offset || toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @NotNull
    public StringLeftover skip(int skip) {
        return new StringLeftover(this, skip);
    }

    public int left() {
        return base.length() - offset;
    }

    public char charAt(int pos) {
        return base.charAt(offset + pos);
    }

    public int offset() {
        return offset;
    }
}
