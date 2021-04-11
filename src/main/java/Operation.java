import java.text.ParseException;

public class Operation {
    private final char operation;

    public Operation(char operation) {
        if (!String.valueOf(operation).matches("[+\\-*><=&|]"))
            throw new RuntimeException("Operation symbol is not a real operation symbol");
        this.operation = operation;
    }

    public int operation() {
        return operation;
    }

    @Override
    public String toString() {
        return String.valueOf(operation);
    }

    public Type returnType() {
        if (String.valueOf(operation).matches("[+\\-*]")) return Type.INTEGER;
        else return Type.BOOLEAN;
    }

    public Type argumentType() {
        if (String.valueOf(operation).matches("[+\\-*<=>]")) return Type.INTEGER;
        else return Type.BOOLEAN;
    }

    public static boolean canParse(StringLeftover toParse) {
        return Symbol.canParse(toParse, "[+\\-*><=&|]");
    }

    public static ParsePair<Operation> parse(StringLeftover toParse) throws ParseException {
        if (!Symbol.canParse(toParse, "[+\\-*><=&|]"))
            throw new ParseException("Could not parse operation", toParse.offset());

        return new ParsePair<>(new Operation(toParse.charAt(0)), toParse.skip(1));
    }
}
