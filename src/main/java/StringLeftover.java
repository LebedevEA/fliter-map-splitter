public class StringLeftover {
    private final String base;
    private final int offset;

    public StringLeftover(String base) {
        this.base = base;
        this.offset = 0;
    }

    public StringLeftover(StringLeftover s, int skip) {
        base = s.base;
        offset = s.offset + skip;
    }

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
