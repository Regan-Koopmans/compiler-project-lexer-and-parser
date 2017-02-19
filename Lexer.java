/*

    CLASS       : Lexer
    DESCRIPTION : Creates tokens based on given input.

*/

import java.util.ArrayList;

class Lexer {
  
  // Helper function to neaten error messages. 
  
  private ArrayList<Token> tokens;

  public void lexError(String message) {
    System.out.println("Lexer Error: " + message);
  }

  // Receieves a program in the form of a string.
  // Should traverse character by character, testing for possible
  // matches in the language and building a list of tokens.

  public void scan(String input) {

    tokens = new ArrayList<Token>();

    // TODO: program lexing logic

  }

  public ArrayList<Token> getTokens() { return tokens; }

}
