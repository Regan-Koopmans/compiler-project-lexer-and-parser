/*

    CLASS       : Token
    DESCRIPTION : Encapsulates a token in the language, used in Lexer.

*/

public class Token {
    
    private String type;
    private String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    } 
}