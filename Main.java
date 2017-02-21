/*

    CLASS       : Main
    DESCRIPTION : Manages the reading of files and provides an entry point to
                  the Lexer class.

*/

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;

class Main {

  // Helper method to neaten error messages

  public static void fatalError(String message) {
    System.out.println(message);
    System.exit(1);
  }

  public static void main(String[] args) {

    if (args.length == 0) {
      fatalError("You did not pass an input file.");
    }

    // Create an instance of the Lexer class.

    Lexer lex = new Lexer();

    // Create String from file.

    StringBuilder sb = new StringBuilder();
    File inputFile = new File(args[0]);

    try {
      FileReader fileReader = new FileReader(inputFile);
      BufferedReader reader = new BufferedReader(fileReader);
      String line;

      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
      fileReader.close();

    } catch (Exception e) { fatalError("There was a problem reading the file"); }

    // Invoke the Lexers' scan method to scan through the string

    lex.scan(sb.toString());
    System.out.println();
    for (Token token:lex.getTokens()) {
      System.out.println(token);
      System.out.println("");
    }
  }
}
