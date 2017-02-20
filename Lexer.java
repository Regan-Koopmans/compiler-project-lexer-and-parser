/*

    CLASS       : Lexer
    DESCRIPTION : Creates tokens based on given input.

*/



import java.util.ArrayList;

// Regarding to regular expressions.

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer {

  // Helper function to neaten error messages.

  private ArrayList<Token> tokens;
  private String currentWord = "";

  public void lexError(String message) {
    System.out.println("Lexer Error: " + message);
  }

  // Receieves a program in the form of a string.
  // Should traverse character by character, testing for possible
  // matches in the language and building a list of tokens.

  public void scan(String input) {

    // Flag that manages whether we are currently reading a quoted string
    boolean parsingString = false;

    tokens = new ArrayList<Token>();


    for (char c:input.toCharArray()) {

      if (c != '\n') {
        currentWord += c;
      }

      if (c == '"') {
        if (parsingString) {
          parsingString = false;
          addToken(TokenType.String);
        } else {
          parsingString = true;
        }
      } else {

        if (matchComparisonOperator(currentWord)) {
          addToken(TokenType.Comparison);
        }

        if (matchBooleanOperator(currentWord)) {
          addToken(TokenType.BooleanOp);
        }

      }
    }
  }

  public boolean matchComparisonOperator(String word) {
    return word.matches("<|>|(eq)");
  }

  public boolean matchBooleanOperator(String word) {
    return word.matches("(and)|(or)|(not)");
  }

  public void addToken(TokenType type) {
    tokens.add(new Token(type, currentWord));
    currentWord = "";

  }

  public ArrayList<Token> getTokens() { return tokens; }

}
