/*

    CLASS       : SyntaxNode
    DESCRIPTION : An data type representing a node in the syntax tree

*/

package compiler.parser;

import java.lang.StringBuilder;
import java.util.ArrayList;

public class SyntaxNode {
    
    private NodeType type;
    private ArrayList<SyntaxNode> children = null;


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
}