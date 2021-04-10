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
}
