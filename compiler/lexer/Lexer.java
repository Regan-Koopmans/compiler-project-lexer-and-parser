/*

    CLASS       : Lexer
    DESCRIPTION : Creates tokens based on given input.

*/

package compiler.lexer;
import java.util.ArrayList;

// Regarding to regular expressions.

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

  // Helper function to neaten error messages.

  private ArrayList<Token> tokens = new ArrayList<Token>();
  private String currentWord = "";
  private Token longestToken;

  public void lexError(String message) {
    System.out.println("\nLexer Error: " + message + "\n");
    System.out.println();
    System.exit(0);
  }

  // Receieves a program in the form of a string.
  // Should traverse character by character, testing for possible
  // matches in the language and building a list of tokens.

  public void scan(String input, Integer lineNumber) {

    // Flag that manages whether we are currently reading a quoted string
    boolean parsingString = false;
    longestToken = null;
    char c;
    int count = 0;
    char [] charArray = input.toCharArray();
    for (;count < charArray.length;count++) {

      c = charArray[count];
      // add a single character to the currently mathcing word. Ignores newlines
      currentWord += c;
      currentWord = currentWord.replaceAll("^\\s","");
      //currentWord = currentWord.trim();

      // This block deals with recognising strings. This means strings implicitly
      // take preference over all other tokens (which makes sense since any text
      // should be allowed in  a string.

      if (c == '"') {
        if (parsingString) {
          parsingString = false;
          if (currentWord.matches("\"(\\w|\\s){0,8}\""))
          {
            addToken(new Token(TokenType.ShortString, currentWord, count));
          } else {
            lexError("On line " + lineNumber +  ": " + currentWord +
              " does not fit the restrictions for short strings.");
          }
          } else {
          parsingString = true;
        }
      } else if (!parsingString) {
        boolean matchesAnyPattern = false;
        // Matching different tokens

        if (matchUserDefinedName(currentWord)) {
          longestToken = new Token(TokenType.UserDefinedName, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchComparisonOperator(currentWord)) {
          longestToken = new Token(TokenType.Comparison, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchBooleanOperator(currentWord)) {
          longestToken = new Token(TokenType.BooleanOp, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchNumberOp(currentWord)) {
          longestToken = new Token(TokenType.NumberOp, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchAssignment(currentWord)) {
          longestToken = new Token(TokenType.Assignment, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchControl(currentWord)) {
          longestToken = new Token(TokenType.Control, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchIO(currentWord)) {
          longestToken = new Token(TokenType.IO, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchInteger(currentWord)) {
          longestToken = new Token(TokenType.Integer, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchHalt(currentWord)) {
          longestToken = new Token(TokenType.Halt, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchProcedure(currentWord)) {
          longestToken = new Token(TokenType.Procedure, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (matchGrouping(currentWord)) {
          longestToken = new Token(TokenType.Grouping, currentWord, lineNumber);
          matchesAnyPattern = true;
        }

        if (!matchesAnyPattern && longestToken != null && !parsingString) {
          addToken(longestToken);
          count--;
        } else if (longestToken ==  null && !parsingString) {
        }
      }
    }

    if (longestToken != null) { addToken(longestToken); }
    if (!currentWord.equals("")) {
      lexError("On line " + lineNumber + ": \"" + currentWord +
        "\" does not match any token specification.");
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
    return word.matches("0|(\\d)+");
  }

  public boolean matchHalt(String word) {
    return word.matches("halt");
  }

  public boolean matchUserDefinedName(String word) {
    Pattern rgx = Pattern.compile("^[a-z][a-z]*[1-9]*$");
    Matcher mtch = rgx.matcher(word);
    return mtch.find();
  }

  public boolean matchProcedure(String word) {
    return word.matches("proc");
  }

  public boolean matchGrouping(String word) {
    Pattern rgx = Pattern.compile("^[\\(\\)\\{\\};,]$");
    Matcher mtch = rgx.matcher(word);
    return mtch.find();
  }

}
