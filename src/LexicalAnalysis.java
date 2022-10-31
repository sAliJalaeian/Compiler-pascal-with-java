import java.util.ArrayList;
import java.util.Objects;

public class LexicalAnalysis {
    private String inputString;
    private int forward, lexemeBegin;
    private ArrayList<String> symbolTable;

    public LexicalAnalysis(String inputString) {
        this.inputString = inputString;
        this.forward = 0;
        this.lexemeBegin = 0;
        this.symbolTable = new ArrayList<>();
    }

    public ArrayList<String> getSymbolTable() {return symbolTable;}

    public void setSymbolTable(ArrayList<String> symbolTable) {this.symbolTable = symbolTable;}

    public int getLexemeBegin() {return lexemeBegin;}

    public void setLexemeBegin(int lexemeBegin) {this.lexemeBegin = lexemeBegin;}

    public String getInputString() {return inputString;}

    public void setInputString(String inputString) {this.inputString = inputString;}

    public int getForward() {return forward;}

    public void setForward(int forward) {this.forward = forward;}

    private char nextChar() {
        char c = inputString.charAt(forward);
        forward++;
        return c;
    }

    public Token getNextToken() {
        int start = 0;
        Token token;
        while (true) {
            switch (start) {
                case 0:
                    token = getRelop();
                    if (token != null) return token;
                    start = 9;
                    break;
                case 9:
                    token = getKeywordAndID();
                    if (token != null) return token;
                    start = 12;
                    break;
                case 12:
                    token = getNumber();
                    if (token != null) return token;
                    start = 22;
                    break;
                case 22:
                    token = whiteSpace();
                    if (token == null)
                        start = 25;
                    else
                        start = 0;
                    break;
                case 25:
                    token = getOperator();
                    if (token != null) return token;
                    start = 28; //TODO
                    break;
                case 28:
                    token = getQuotation();
                    if (token != null) return token;
                    break;
            }
        }
    }

    private Token getQuotation() {
        int state = 28;
        char c;
        while (true)
            switch (state) {
                case 28:
                    c = nextChar();
                    if (c == '\'') state = 29;
                    else return fail();
                    break;
                case 29:
                    c = nextChar();
                    if (c == '\'') {
                        forward++;
                        retract();
                        return new Token("quot", "\'\'");
                    }
                    else state = 29;
                    break;
            }
    }

    private Token getOperator() {
        int state = 25;
        char c = ' ';
        while (true)
            switch (state) {
                case 25:
                    c = nextChar();
                    if (c == '+' || c == '-' || c == '*' || c == '/' || c == ',' || c == '.' || c == '[' || c == ']' || c == '(' || c == ')' || c == ';') state = 27;
                    else if (c == ':') state = 26;
                    else return fail();
                    break;
                case 26:
                    c = nextChar();
                    if (c == '=')
                        return new Token("OP", ":=");
                    else {
                        retract();
                        return new Token("OP", ":");
                    }
                case 27:
                    forward++;
                    retract();
                    return new Token("OP", String.valueOf(c));
            }
    }

    private Token whiteSpace() {
        int state = 22;
        char c;
        while (true)
            switch (state) {
                case 22:
                    c = nextChar();
                    if (c == ' ' || c == '\n' ) state = 23;
                    else return fail();
                    break;
                case 23:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (c == ' ' || c == '\n' ) state = 23;
                        else state = 24;
                    }
                    else {
                        forward++;
                        state = 24;
                    }
                    break;
                case 24:
                    retract();
                    return new Token("Whitespace", " ");
            }
    }

    private Token getKeywordAndID() {
        int state = 9;
        char c;
        String result = "";
        while (true) {
            switch (state) {
                case 9:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkLetter(c))
                            result += c;
                        if (!checkLetter(c))
                            return fail();
                        state = 10;
                    }
                    else {
                        forward++;
                        state = 11;
                    }
                    break;
                case 10:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkLetter(c))
                            result += c;
                        if (!checkLetterNumber(c)) {
                            state = 11;
                            break;
                        }
                        state = 10;
                    }
                    else {
                        forward++;
                        state = 11;
                    }
                    break;
                case 11:
                    retract();
                    int index = installID(result);
                    return new Token("id", String.valueOf(index));
            }
        }
    }

    private int installID(String ID) {
        if (!symbolTable.contains(ID)) {
            symbolTable.add(ID);
            return symbolTable.size() - 1;
        }
        for (int i = 0; i < symbolTable.size(); i++)
            if (Objects.equals(symbolTable.get(i), ID))
                return i;
        return 0;
    }

    private boolean checkLetterNumber(char temp) {
        for (int i = 48; i <= 57; i++)
            if ((int) temp == i)
                return true;
        for (int i = 65; i <= 90; i++)
            if ((int) temp == i)
                return true;
        for (int i = 97; i <= 122; i++)
            if ((int) temp == i)
                return true;
        return false;
    }

    private boolean checkNumber(char temp) {
        for (int i = 48; i <= 57; i++)
            if ((int) temp == i)
                return true;
        return false;
    }

    private boolean checkLetter(char temp) {
        for (int i = 65; i <= 90; i++)
            if ((int) temp == i)
                return true;
        for (int i = 97; i <= 122; i++)
            if ((int) temp == i)
                return true;
        return false;
    }

    private Token getNumber() {
        int state = 12;
        char c;
        String result = "";
        while (true) {
            switch (state) {
                case 12:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkNumber(c))
                            result += c;
                        if (checkNumber(c)) state = 13;
                        else return fail();
                    }
                    else {
                        forward++;
                        state = 21;
                    }
                    break;
                case 13:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkNumber(c))
                            result += c;
                        if (checkNumber(c)) state = 13;
                        else if (c == '.' ) state = 14;
                        else if (c == 'E' ) state = 16;
                        else state = 20;
                    }
                    else {
                        forward++;
                        state = 21;
                    }
                    break;
                case 14:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkNumber(c))
                            result += c;
                        if (checkNumber(c)) state = 15;
                    }
                    else {
                        forward++;
                        state = 21;
                    }
                    break;
                case 15:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkNumber(c))
                            result += c;
                        if (checkNumber(c)) state = 15;
                        else if (c == 'E' ) state = 16;
                        else state = 21;
                    }
                    else {
                        forward++;
                        state = 21;
                    }
                    break;
                case 16:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkNumber(c))
                            result += c;
                        if (c == '+' || c == '-' ) state = 17;
                        else if (checkNumber(c)) state = 18;
                    }
                    else {
                        forward++;
                        state = 21;
                    }
                    break;
                case 17:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkNumber(c))
                            result += c;
                        if (checkNumber(c)) state = 18;
                    }
                    else {
                        forward++;
                        state = 21;
                    }
                    break;
                case 18:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (checkNumber(c))
                                result += c;
                        if (checkNumber(c)) state = 18;
                        else state = 19;
                    }
                    else {
                        forward++;
                        state = 21;
                    }
                    break;
                case 19:
                    retract();
                    return new Token("digit", result);
                case 20:
                    retract();
                    return new Token("digit", result);
                case 21:
                    retract();
                    return new Token("digit", result);
            }
        }
    }

    private Token getRelop() {
        Token token = new Token("relop");
        int state = 0;
        char c;
        while (true)
            switch (state) {
                case 0:
                    c = nextChar();
                    if (c == '<' ) state = 1;
                    else if (c == '=' ) state = 5;
                    else if (c == '>' ) state = 6;
                    else return fail();
                    break;
                case 1:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (c == '=' ) state = 2;
                        else if (c == '>' ) state = 3;
                        else state = 4;
                    }
                    else {
                        forward++;
                        state = 4;
                    }
                    break;
                case 2:
                    lexemeBegin = forward;
                    token.setAttribute("LE");
                    return token;
                case 3:
                    lexemeBegin = forward;
                    token.setAttribute("NE");
                    return token;
                case 4:
                    retract();
                    token.setAttribute("LT");
                    return token;
                case 5:
                    lexemeBegin = forward;
                    token.setAttribute("EQ");
                    return token;
                case 6:
                    if (forward < inputString.length()) {
                        c = nextChar();
                        if (c == '=' ) state = 7;
                        else state = 8;
                    }
                    else {
                        forward++;
                        state = 8;
                    }
                    break;
                case 7:
                    lexemeBegin = forward;
                    token.setAttribute("GE");
                    return token;
                case 8:
                    retract();
                    token.setAttribute("GT");
                    return token;
            }
    }

    private void retract() {
        forward--;
        lexemeBegin = forward;
    }

    private Token fail() {
        forward = lexemeBegin;
        return null;
    }
}
