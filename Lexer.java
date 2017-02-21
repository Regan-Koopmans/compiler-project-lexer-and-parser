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
  private String prevWord;
  private Token longestToken;

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

    longestToken = null;
    for (char c:input.toCharArray()) {

      // add a single character to the currently mathcing word. Ignores newlines
      if (c == '\n') {
        c = ' ';
      }
      currentWord += c;
      currentWord = currentWord.replaceAll("^\\s+","");

      // This block deals with recognising strings. This means strings implicitly
      // take preference over all other tokens (which makes sense since any text
      // should be allowed in  a string.

      if (c == '"') {
        if (parsingString) {
          parsingString = false;
          addToken(new Token(TokenType.String, currentWord));
        } else {
          parsingString = true;
        }

      } else if (!parsingString) {
        boolean matchesAnyPattern = false;
        System.out.println("Matching for " + currentWord);
        // Matching different tokens

        if (matchUserDefinedName(currentWord)) {
          longestToken = new Token(TokenType.UserDefinedName, currentWord);
          System.out.println("passed user defined name");
          matchesAnyPattern = true;
        }

        if (matchComparisonOperator(currentWord)) {
          longestToken = new Token(TokenType.Comparison, currentWord);
          System.out.println("passed comparison");
          matchesAnyPattern = true;
        }

        if (matchBooleanOperator(currentWord)) {
          longestToken = new Token(TokenType.BooleanOp, currentWord);
          System.out.println("passed boolean op");
          matchesAnyPattern = true;
        }

        if (matchNumberOp(currentWord)) {
          longestToken = new Token(TokenType.NumberOp, currentWord);
          System.out.println("passed user defined name");
          matchesAnyPattern = true;
        }

        if (matchAssignment(currentWord)) {
          longestToken = new Token(TokenType.Assignment, currentWord);
          System.out.println("passed assignment");
          matchesAnyPattern = true;
        }

        if (matchControl(currentWord)) {
          longestToken = new Token(TokenType.Control, currentWord);
          System.out.println("passed control");
          matchesAnyPattern = true;
        }

        if (matchIO(currentWord)) {
          longestToken = new Token(TokenType.IO, currentWord);
          System.out.println("passed IO");
          matchesAnyPattern = true;
        }

        if (matchInteger(currentWord)) {
          longestToken = new Token(TokenType.Integer, currentWord);
          System.out.println("passed integer");
          matchesAnyPattern = true;
        }

        if (matchHalt(currentWord)) {
          longestToken = new Token(TokenType.Halt, currentWord);
          System.out.println("passed halt");
          matchesAnyPattern = true;
        }

        if (matchProcedure(currentWord)) {
          longestToken = new Token(TokenType.Procedure, currentWord);
          System.out.println("passed user defined name");
          matchesAnyPattern = true;
        }

        if (matchGrouping(currentWord)) {
          longestToken = new Token(TokenType.Grouping, currentWord);
          System.out.println("passed grouping");
          matchesAnyPattern = true;
        }

        if (!matchesAnyPattern && longestToken != null && !parsingString) {
          addToken(longestToken);
          currentWord = String.valueOf(c);
        } else if (longestToken ==  null && !parsingString) {
          System.out.println(currentWord);
        }
      }
    }
  }

  // adds a token the list of tokens and clears working word.

  public void addToken(Token token) {
    tokens.add(token);
    currentWord = "";
    longestToken = null;
  }

  public ArrayList<Token> getTokens() { return tokens; }

  // The following boolean functions use Java's String.match() function
  // to match tokens using the following regular expressions.

  public boolean matchComparisonOperator(String word) {
    return word.matches("<|>|(eq)");
  }

  public boolean matchBooleanOperator(String word) {
    return word.matches("(and)|(or)|(not)");
  }

  public boolean matchNumberOp(String word) {
    return word.matches("(add)|(sub)|(mult)");
  }

  public boolean matchAssignment(String word) {
    return word.matches("=");
  }

  public boolean matchControl(String word) {
    return word.matches("(if)|(then)|(for)|(else)|(while)");
  }

  public boolean matchIO(String word) {
    return word.matches("(input)|(output)");
  }

  public boolean matchInteger(String word) {
    return word.matches("0|1(\\d)*");
  }

  public boolean matchHalt(String word) {
    return word.matches("halt");
  }

  public boolean matchUserDefinedName(String word) {
    Pattern rgx = Pattern.compile("^[a-zA-Z0-9]+$");
    Matcher mtch = rgx.matcher(word);
    return mtch.find();
  }

  public boolean matchProcedure(String word) {
    return word.matches("proc");
  }

  public boolean matchGrouping(String word) {
    return word.matches("[{}();,]\\s*");
  }

}
