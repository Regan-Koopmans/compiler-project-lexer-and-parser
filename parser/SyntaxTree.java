/*

    CLASS       : SyntaxTree
    DESCRIPTION : Defines a data structure to maintain the syntax structure of the program
                  in question.

*/
package compiler.parser;

// This data structure will inevitably be the final.
// It is not currently used, but we will use this to contain the
// tree that we generate from the ParseStack.

public class SyntaxTree {
    public SyntaxNode root = null;

    public String toString() {
        return root.toString();
    }
}