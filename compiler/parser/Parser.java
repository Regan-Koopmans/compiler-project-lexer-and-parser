/*

    CLASS       : Parser
    DESCRIPTION : Checks whether a list of tokens is syntactically correct by constructing
                  a syntax tree.

*/
package compiler.parser;

import java.util.ArrayList;
import compiler.lexer.Token;

public class Parser {
    private SyntaxTree tree;
    public void parse(ArrayList<Token> tokens) {

       
    }

    public SyntaxTree getTree() { return tree; }
    
    public void recursiveDecentParse() {
        acceptProg();
    }

    public void acceptProg() {
        
    }
}
