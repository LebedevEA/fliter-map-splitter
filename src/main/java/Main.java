import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String operations = reader.readLine().trim();
            CallChain parsed = ArrayOperationsParser.parse(operations);
            if (parsed == null) {
                System.out.println("SYNTAX ERROR");
                return;
            }
            if (!parsed.validate()) {
                System.out.println("TYPE ERROR");
                return;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while reading from stdin, try again");
        } catch (Exception e) {
            System.out.println("Something went very wrong");
        }
    }
}
