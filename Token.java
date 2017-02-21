/*

    CLASS       : Token
    DESCRIPTION : Encapsulates a token in the language, used in Lexer.

*/


public class Token {

    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String toString() {

      String returnString = "TYPE: ";
      returnString += type.toString() + "\nVALUE: ";
      returnString += value;
      return returnString;
    }
}
