/*

    CLASS       : SyntaxTree
    DESCRIPTION : Defines a data structure to maintain the syntax structure of the program
                  in question.

*/
package compiler.parser;

public class SyntaxTree {
    public SyntaxNode root = null;

    public String toString() {
        return root.toString();
    }
}