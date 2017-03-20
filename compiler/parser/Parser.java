/*

    CLASS       : Parser
    DESCRIPTION : Checks whether a list of tokens is syntactically correct by constructing
                  a syntax tree.

*/
package compiler.parser;

import java.util.ArrayList;
import compiler.lexer.Token;

public class Parser {
    
    private SyntaxTree tree = new SyntaxTree();
    private ParseStack stack = new ParseStack();

    public void parse(ArrayList<Token> tokens) {

        // For every token in our list, shift this token 
        // (that is:add it to the stack), and then try to reduce.

        // in reality, we might need to add more reduce calls,
        // but this was just to try prove the concept.


        // 
        //                                place in        ------------
        //    TOKEN -> TOKEN -> TOKEN --------------->    |   stack  |
        //                                                 -----------
        //
        //    for every token that we add to the stack, we try 
        //    to reduce the stack to a smaller stack with more
        //    high-level syntax nodes.

        //     -------------------------   |
        //     | low-level syntax-node |   | reduce -> 
        //     |-----------------------|   |            -------------------------
        //     | low-level syntax-node |   V           | high-level syntax-node |
        //      ------------------------               --------------------------


        for (Token t : tokens) {
            shift(t);
            reduce();
        }

        // I am just printing the stack here to see if anything
        // happened to the stack.

        System.out.println(stack);
    }

    // This is the function shift from SLR parsing with a stack
    // in the textbook.

    public void shift(Token t) {
        stack.push(new SyntaxNode(t));
    }

    // This is the reduce function from the literature. At the moment it only
    // pops the top 3 to check if it can reduce to INSTR. In the final function,
    // the reduce method will have to do a lot more checking (for different stack
    // portions of size x, and for different types of syntax nodes/production rules). 

    public boolean reduce() {
        boolean reduceMade = false;
        if (stack.size() >= 3) {
            ArrayList<SyntaxNode> test = stack.peek(3);
            if (reduceINSTR(test)) {
                stack.pop(3);
                stack.push(new SyntaxNode(NodeType.INSTR));
            }
        }
        return reduceMade;
    }

    // This is an example of a reduce function. In this case,
    // it reduces tokens of the form.

    // "variable = number;" (which are the tokens UDN, Assignment, Integer, Grouping) 

    // to: 

    // "INSTR"

    public boolean reduceINSTR(ArrayList<SyntaxNode> test) {
        return test.get(0).getType() == NodeType.Integer && 
                test.get(1).getType() == NodeType.Assignment && 
                    test.get(2).getType() == NodeType.UserDefinedName;
    }

    // Function to return a syntax tree. 

    // Note: I have not written any code to convert the compressed stack into a tree
    // so the tree is always NULL at this point.

    public SyntaxTree getTree() { return tree; }
    
}
