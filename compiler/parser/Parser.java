/*

    CLASS       : Parser
    DESCRIPTION : Checks whether a list of tokens is syntactically correct by constructing
                  a syntax tree.

*/
package compiler.parser;

import java.util.ArrayList;
import compiler.lexer.Token;
import java.util.Collections;
import static compiler.parser.NodeType.*;

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
            while (reduce()) {
            }
        }


        // continue to reduce while reductions can be made.

        while (reduce()) {}
    

        ArrayList<SyntaxNode> testSymbols = stack.peek(stack.size());
        Collections.reverse(testSymbols);

        if (reducePROG(testSymbols)) {
            stack.pop(stack.size());
            SyntaxNode reducedNode = new SyntaxNode(PROG);
            reducedNode.addChildren(testSymbols);
            stack.push(reducedNode);
        }

        if (stack.size() != 1) {
            System.out.println("\n\u001B[31mSyntax error!\u001B[0m");
            System.out.println("Unexpected input near \"" + stack.peek(1).get(0).getValueRecursive() + 
                "\" on line " + stack.peek(1).get(0).getLineRecursive() + ".\n");
        } else {
            System.out.println("\n\u001B[32mParsing succeeded.\u001B[0m\n");
            System.out.println("Syntax tree generated : \n");
            stack.peek(1).get(0).print();
            System.out.println();
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
                reducedNode = new SyntaxNode(VAR);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceCODE(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(CODE);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceINSTR(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(INSTR);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceIOCall(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(IOCALL);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceASSIGN(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(ASSIGN);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceCALC(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(CALC);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceBOOL(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(BOOL);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceNUMEXPR(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(NUMEXPR);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceCOND_BRANCH(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(COND_BRANCH);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }
            
            if (reduceCOND_LOOP(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(COND_LOOP);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reducePROC(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(PROC);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (reduceCALL(testSymbols)) {
                stack.pop(size-x);
                reducedNode = new SyntaxNode(CALL);
                reducedNode.addChildren(testSymbols);
                stack.push(reducedNode);
                reduce();
                return true;
            }

            if (x > 1){
                    if (reducePROG(testSymbols)) {
                    stack.pop(size-x);
                    reducedNode = new SyntaxNode(PROG);
                    reducedNode.addChildren(testSymbols);
                    stack.push(reducedNode);
                    reduce();
                    return true;
                }
            }
            

        }
        
        return reduceMade;
    }

    public boolean reduceINSTR(ArrayList<SyntaxNode> testSymbols) {

        if (testSymbols.size() == 1) {
            SyntaxNode test = testSymbols.get(0);
            if (   test.getType() == Halt 
                || test.getType() == IOCALL
                || test.getType() == CALL
                || test.getType() == ASSIGN
                || test.getType() == COND_BRANCH
                || test.getType() == COND_LOOP) {
                return true;
            }
        }
        
        return false;
    }

    public boolean reduceHalt(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 1 && testSymbols.get(0).getType() == Halt) {
            return true;
        }
        return false;
    }

    public boolean reduceIOCall(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 4) {

            // if IO statement

            if  (testSymbols.get(0).getType() == IO) {
                

                // if enclosed in brackets

                if (testSymbols.get(1).getValue().equals("(") && testSymbols.get(3).getValue().equals(")")) {
                    
                    // if variable

                    if (testSymbols.get(2).getType() == VAR) {
                        return true;
                    }
                } 
            }
        } 
        return false;
    }

    public boolean reduceVAR(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 1 && 
            testSymbols.get(0).getType() == UserDefinedName) {
            return true;
        }
        return false;
    }

    public boolean reduceCODE(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        
        if (size == 1) {
        
            // CODE -> INSTR

            if (testSymbols.get(0).getType() == INSTR) {
                return true;
            }
        } else if (size == 3) {
            
            // CODE -> INSTR; CODE

            // System.out.println("Here!");

            if (testSymbols.get(0).getType() == INSTR && 
                testSymbols.get(1).getValue().equals(";") && 
                testSymbols.get(2).getType() == CODE) {
                return true;
            
            // this is not exactly "correct" to the grammar, but it
            // emulates correct behaviour

            // CODE -> CODE; CODE

            } else if (testSymbols.get(0).getType() == CODE && 
                testSymbols.get(1).getValue().equals(";") && 
                testSymbols.get(2).getType() == CODE) {
                return true;


            } 
        }
        return false;
    }

    public boolean reduceBOOL(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        if (size > 1) {
            if (size > 2) {

                if (size == 5) {

                    if (testSymbols.get(0).getValue().equals("(") 
                        && testSymbols.get(1).getType() == VAR
                        && testSymbols.get(2).getType() == Comparison
                        && testSymbols.get(3).getType() == VAR
                        && testSymbols.get(4).getValue().equals(")")) {
                        return true;
                    }


                } else if (size == 6) {
                    if (testSymbols.get(0).getType() == BooleanOp 
                        && testSymbols.get(1).getValue().equals("(")
                        && testSymbols.get(2).getType() == BOOL
                        && testSymbols.get(3).getValue().equals(",")
                        && testSymbols.get(4).getType() == BOOL
                        && testSymbols.get(5).getValue().equals(")")) {
                        return true;
                    }

                    if (testSymbols.get(0).getValue().equals("eq") 
                        && testSymbols.get(1).getValue().equals("(")
                        && testSymbols.get(2).getType() == VAR
                        && testSymbols.get(3).getValue().equals(",")
                        && testSymbols.get(4).getType() == VAR
                        && testSymbols.get(5).getValue().equals(")")) {
                        return true;
                    }
                }

            } else {

                if (testSymbols.get(0).getValue().equals("not") 
                    && testSymbols.get(1).getType() == BOOL) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean reduceCALC(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 6) {

            if (testSymbols.get(0).getType() == NumberOp 
                && testSymbols.get(1).getValue().equals("(")
                && testSymbols.get(2).getType() == NUMEXPR
                && testSymbols.get(3).getValue().equals(",")
                && testSymbols.get(4).getType() == NUMEXPR
                && testSymbols.get(5).getValue().equals(")")) {
                    return true;
            }
        }
        return false;
    }

    public boolean reduceNUMEXPR(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 1) {
            if (testSymbols.get(0).getType() == Integer 
                || testSymbols.get(0).getType() == CALC) {
                    if (testSymbols.get(0).getType() == Integer) {
                    }
                return true;
            }
        }
        return false;
    }

    public boolean reducePROG(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        if (size == 1) {
            if (testSymbols.get(0).getType() == CODE) {
                return true;
            }
        } else if (size == 2) {

            if (testSymbols.get(0).getType() == CODE
                && testSymbols.get(1).getValue().equals(";")) {

                return true;
            } 

            if (testSymbols.get(0).getType() == CODE
                && testSymbols.get(1).getType() == CODE) {

                return true;
            } 

        } else if (size == 3) {
            if (testSymbols.get(0).getType() == CODE
                && testSymbols.get(1).getValue().equals(";")
                && testSymbols.get(2).getType() == PROC_DEFS) {
                return true;
            }
        } 
        return false;
    }

    public boolean reduceASSIGN(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 3) {
            if (testSymbols.get(0).getType() == VAR 
                && testSymbols.get(1).getType() == Assignment
                && (testSymbols.get(2).getType() == VAR || testSymbols.get(2).getType() == NUMEXPR || testSymbols.get(2).getType() == ShortString)) {
                return true;
            }
        }
        return false;
    }

    public boolean reduceCOND_LOOP(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        if (size == 7) {
            if (testSymbols.get(0).getValue().equals("while") 
                && testSymbols.get(1).getValue().equals("(")
                && testSymbols.get(2).getType() == BOOL
                && testSymbols.get(3).getValue().equals(")")
                && testSymbols.get(4).getValue().equals("{")
                && testSymbols.get(5).getType() == CODE
                && testSymbols.get(6).getValue().equals("}")) {
                    return true;
            }
        } else if (size == 22) {
            if (testSymbols.get(0).getValue().equals("for") 
                && testSymbols.get(1).getValue().equals("(")
                && testSymbols.get(2).getType() == BOOL
                && testSymbols.get(3).getValue().equals(")")
                && testSymbols.get(4).getValue().equals("{")
                && testSymbols.get(5).getType() == CODE
                && testSymbols.get(6).getValue().equals("}")) {
                    return true;
            }
        }
        return false;
    }

    public boolean reduceCOND_BRANCH(ArrayList<SyntaxNode> testSymbols) {
        int size = testSymbols.size();
        if (size == 9) {

            if (testSymbols.get(0).getValue().equals("if") 
                && testSymbols.get(1).getValue().equals("(")
                && testSymbols.get(2).getType() == BOOL
                && testSymbols.get(3).getValue().equals(")")
                && testSymbols.get(4).getValue().equals("then")
                && testSymbols.get(5).getValue().equals("{")
                && (testSymbols.get(6).getType() == CODE || testSymbols.get(6).getType() == PROG)
                && testSymbols.get(7).getValue().equals("}")
                && testSymbols.get(8).getValue().equals(";")) {
                    return true;
            }

        } else if (size == 13) {
            if (testSymbols.get(0).getValue().equals("if") 
                && testSymbols.get(1).getValue().equals("(")
                && testSymbols.get(2).getType() == BOOL
                && testSymbols.get(3).getValue().equals(")")
                && testSymbols.get(4).getValue().equals("then")
                && testSymbols.get(5).getValue().equals("{")
                && (testSymbols.get(6).getType() == CODE || testSymbols.get(6).getType() == PROG)
                && testSymbols.get(7).getValue().equals("}")
                && testSymbols.get(8).getValue().equals("else")
                && testSymbols.get(9).getValue().equals("{")
                && (testSymbols.get(10).getType() == CODE || testSymbols.get(10).getType() == PROG)
                && testSymbols.get(11).getValue().equals("}")
                && testSymbols.get(12).getValue().equals(";")) {
                    return true;
            }
        }
        return false;
    }



    public boolean reducePROC_DEFS(ArrayList<SyntaxNode> testSymbols) {
        return false;
    }


    public boolean reducePROC(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 4) {
            if (testSymbols.get(0).getType() == Procedure
            && testSymbols.get(1).getValue().equals("{")
            && testSymbols.get(2).getType() == PROG
            && testSymbols.get(3).getValue().equals("}")) {
                return true;
            }
        }        
        return false;
    }

    public boolean reduceCALL(ArrayList<SyntaxNode> testSymbols) {
        if (testSymbols.size() == 2) {
            if (testSymbols.get(0).getType() == VAR
                && testSymbols.get(1).getValue().equals(";")) {
                return true;
            }
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
