/*

    CLASS       : SyntaxNode
    DESCRIPTION : An data type representing a node in the syntax tree and parse stack.

*/

package compiler.parser;

import java.lang.StringBuilder;
import java.util.ArrayList;
import compiler.lexer.Token;

import java.io.PrintWriter;

// This class is used to encapsulate a unit of syntax. This includes both
// leaves, which are tokens that appear in the source code, or higher-level
// concepts that we obtain from the production rules.

// Therefore, using the sytax node, we can build data structures that look
// like this:                              ---------------------


//                           SYNTAX TREE
//                           ------------


//                               [PROG]
//                               /     \
//                           [CODE]   [CODE]
//                            /   \      
//                       [INSTR]    [INSTR]
//                      / |     \
//                     /  |      \
//                    /   |        \
//                  /     |         \
//                 /      |          \
//    [UserDefinedName] [Assignment] [Integer] [Grouping]


// And like this:

//           PARSE STACK
//           -----------


//          top
//      --------------------
//      |      INSTR       | -------> containts it own children (it has been "reduced" from leaf).
//      --------------------
//      |        IO        | ---+
//      ----------- -------|    |--- can still be reduced to an INSTR, perhaps on another iteration.
//      | UserDefinedName  | ---+
//      --------------------


// Using the same basic data unit, namely, the SyntaxNode.
//                ---------------              ----------

public class SyntaxNode {

    private NodeType type;
    private ArrayList<SyntaxNode> children = null;
    private String value;
    int lineNumber = -1;

    private static PrintWriter writer;

    // Some logic to make printing the tree more presentable :)

    public void print() {
        try {
            writer = new PrintWriter("output.tree", "UTF-8");
        } catch (Exception e) {
            System.out.println("Could not write tree to file!");
        }
        print("", true);
        writer.close();
    }

    private void print(String prefix, boolean isTail) {

        writer.println(prefix + (isTail ? "└── " : "├── ") + type.toString());

        if (children != null) {
            for (int i = 0; i < children.size() - 1; i++) {
                children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (children.size() > 0) {
                children.get(children.size() - 1)
                .print(prefix + (isTail ?"    " : "│   "), true);
            }
        }


    }

    public void addChildren(ArrayList<SyntaxNode> newChildren) {
        if (children == null) {
            children = new ArrayList<SyntaxNode>();
        }
        for (SyntaxNode child:newChildren) {
            children.add(child);
        }
    }

    // Constructor

    public SyntaxNode(NodeType type) {
        this.type = type;
    }

    public SyntaxNode(Token token) {
        this.type = NodeType.fromTokenType(token.getType());
        lineNumber = token.lineNumber;
    }

    public SyntaxNode(Token token, String value) {
        this.type = NodeType.fromTokenType(token.getType());
        lineNumber = token.lineNumber;
        this.value = value;
    }

    public NodeType getType() {
        return type;
    }

    public String getValueRecursive() {
        if (value != null) {
            return value;
        } else {
            return children.get(0).getValueRecursive();
        }
    }

    public int getLineRecursive() {
        if (lineNumber != -1) {
            return lineNumber;
        } else {
            return children.get(0).getLineRecursive();
        }
    }

    public String getValue() {
        if (value != null) {
            return value;
        }
        else return "";
    }
}