/*

    CLASS       : Parser
    DESCRIPTION : Checks whether a list of tokens is syntactically correct by constructing
                  a syntax tree.

*/
package compiler.parser;

import java.util.ArrayList;
import compiler.lexer.Token;
import java.util.Collections;

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
            // System.out.println(stack);
            // System.out.println();
            reduce();
            // System.out.println(stack);
        }


        // continue to reduce while reductions can be made.

        while (reduce()) {}
    

        ArrayList<SyntaxNode> testSymbols = stack.peek(stack.size());
        if (reducePROG(testSymbols)) {
            stack.pop(stack.size());
            SyntaxNode reducedNode = new SyntaxNode(NodeType.PROG);
            reducedNode.addChildren();
            stack.push(reducedNode);
        }

        if (stack.size() != 1) {
            System.out.println("Syntax error!");
        } else {
            System.out.println("Program passed!");
        }

        // I am just printing the stack here to see if anything
        // happened to the stack.
        // System.out.println("-----------");
        // System.out.println(stack);
    }

    // This is the function shift from SLR parsing with a stack
    // in the textbook.

    public void shift(Token t) {
        stack.push(new SyntaxNode(t, t.getValue()));
    }

    // This is the reduce function from the literature. At the moment it only
    // pops the top 3 to check if it can reduce to INSTR. In the final function,
    // the reduce method will have to do a lot more checking (for different stack
    // portions of size x, and for different types of syntax nodes/production rules). 

    public boolean reduce() {
        boolean reduceMade = false;

        // we have to reverse the result of peek because, due to the nature of a
        // stack, it gives us the tokens in the reverse order of program order.

        int size = stack.size();
        SyntaxNode reducedNode;
        ArrayList<SyntaxNode> testSymbols;
        for (int x = 0; x < size; x++) {

            testSymbols = stack.peek(size-x);
            Collections.reverse(testSymbols);
        
            if (reduceVAR(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(NodeType.VAR);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceCODE(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(NodeType.CODE);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceINSTR(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(NodeType.INSTR);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            
        }
        
        return reduceMade;
    }

    // This is an example of a reduce function. In this case,
    // it reduces tokens of the form.

    // "variable = number;" (which are the tokens UDN, Assignment, Integer, Grouping) 

    // to: 

    // "INSTR"

    public boolean reduceINSTR(ArrayList<SyntaxNode> testSymbols) {

        // System.out.println("Tokens in order");
        // System.out.println(testSymbols);
        // System.out.println("------------------------");

        if (reduceHalt(testSymbols)) { return true; }
        if (reduceIOCall(testSymbols)) { return true;}

        return false;
    }

    public boolean reduceHalt(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 1 && testSymbols.get(0).getType() == NodeType.Halt) {
            return true;
        }
        return false;
    }

    // function to check if the parameter array testSymbols can reduce
    // to an IO call. IO calls are of the following format:

    //   output    (   VAR    )
    //   input     (   VAR    )

    // so we check that the size is 4, that the list starts with either
    // "output" or "input" and then that the rest is as expected.

    public boolean reduceIOCall(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 4) {

            // if IO statement

            if  (testSymbols.get(0).getType() == NodeType.IO) {
                

                // if enclosed in brackets

                if (testSymbols.get(1).getValue().equals("(") && testSymbols.get(3).getValue().equals(")")) {
                    
                    // if variable

                    if (testSymbols.get(2).getType() == NodeType.VAR) {
                        return true;
                    }
                } 
            }
        } 
        return false;
    }

    public boolean reduceVAR(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 1 && 
            testSymbols.get(0).getType() == NodeType.UserDefinedName) {
            return true;
        }
        return false;
    }

    public boolean reduceCODE(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        
        if (size == 1) {
        
            // CODE -> INSTR

            if (testSymbols.get(0).getType() == NodeType.INSTR) {
                return true;
            }


        } else if (size == 3) {
            
            // CODE -> INSTR; CODE

            // System.out.println("Here!");

            if (testSymbols.get(0).getType() == NodeType.INSTR && 
                testSymbols.get(1).getValue().equals(";") && 
                testSymbols.get(2).getType() == NodeType.CODE) {
                return true;
            
            // this is not exactly "correct" to the grammar, but it
            // emulates correct behaviour

            // CODE -> CODE; CODE

            } else if (testSymbols.get(0).getType() == NodeType.CODE && 
                testSymbols.get(1).getValue().equals(";") && 
                testSymbols.get(2).getType() == NodeType.CODE) {
                return true;


            } else {
                // System.out.println("DID NOT PASS CODE STANDARDS");
                // System.out.println(testSymbols);
            }
        }
        return false;
    }

    public boolean reduceBOOL(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        if (size > 1) {
            if (size > 2) {

                if (size == 5) {

                    // (NVAR > NVAR)
                    // (NVAR < NVAR)


                } else if (size == 6) {

                }

            } else {

                // Tokens should be of the form "not BOOL"

                if (testSymbols.get(0).getValue().equals("not") && testSymbols.get(1).getType() == NodeType.BOOL) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean reducePROG(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        if (size == 1) {
            if (testSymbols.get(0).getType() == NodeType.CODE) {
                return true;
            }
        }
        else if (size == 3) {

        } 
        return false;
    }

    // Function to return a syntax tree. 

    // Note: I have not written any code to convert the compressed stack into a tree
    // so the tree is always NULL at this point.

    public void generateTree() {
        return;
    }

    public SyntaxTree getTree() { return tree; }
}
