import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {
        System.out.println(
                CallChain.parse(new StringLeftover("filter{(element>0)}%>%filter{(element<0)}%>%map{(element*element)}")).parsed()
        );
    }
}
