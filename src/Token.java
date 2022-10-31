public class Token {
    public Token(String name, String attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    public Token(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "<" + name + ", " + attribute + ">";
    }

    private String name, attribute;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getAttribute() {return attribute;}

    public void setAttribute(String attribute) {this.attribute = attribute;}
}
