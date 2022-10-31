import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String inputString = "";
        while (input.hasNext()) {
            String str = input.next();
            if (str.equals("endl")) break;
            inputString += str;
        }
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(inputString);
        Token token;
        while (lexicalAnalysis.getForward() <= inputString.length()) {
            token = lexicalAnalysis.getNextToken();
            System.out.println(token);
            System.out.println("-------------------------------------------");
            if (lexicalAnalysis.getForward() == inputString.length())
                break;
        }
        for (String name : lexicalAnalysis.getSymbolTable())
            System.out.print(name + "\t");
    }
}
