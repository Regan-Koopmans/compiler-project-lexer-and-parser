/*

    CLASS       : SyntaxNode
    DESCRIPTION : An data type representing a node in the syntax tree and parse stack.

*/

package compiler.parser;

import java.lang.StringBuilder;
import java.util.ArrayList;
import compiler.lexer.Token;

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


    // Some logic to make printing the tree more presentable :)

    public String toString(String prefix) {
        StringBuilder sb = new  StringBuilder();
        sb.append(type.toString() + "\n");
        if (children != null) {
            for (SyntaxNode child : children) {
                sb.append(prefix + "   |\n");
                sb.append(prefix + "   |---" + child.toString("   |") + "\n");
            }
            return sb.toString();
        }
        else return type.toString();
    }

    public String toString() {
        return toString("");
    }

    // A function that adds n children to a node

    public void addChildren(SyntaxNode...newChildren) {
        if (children == null) { children = new ArrayList<SyntaxNode>(); }
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
    }

    public NodeType getType() {
        return type;
    }
}