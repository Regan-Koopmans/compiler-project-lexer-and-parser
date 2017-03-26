/*

    CLASS       : ParseStack
    DESCRIPTION : A data structure used in Parser.java

*/
package compiler.parser;

import java.util.ArrayList;
import java.lang.StringBuilder;

public class ParseStack {
	
	// The stack is implemented as an ArrayList, but this
	// is just a minor detail.

	private ArrayList<SyntaxNode> data = new ArrayList<SyntaxNode>();

	// Adds an element to the top of the stack.

	public void push(SyntaxNode input) {
		data.add(0, input);
	}

	// Returns the SyntaxNode at position `x` in
	// the stack. Where index 0 is the top.

	public SyntaxNode at(int x) {
		return data.get(x);
	}

	// The function `pop` removes and returns the top n items from
	// the stack. The parameter `amount` tells the function how many
	// to remove.

	// therefore if we have the stack:

	//      -----------
	//      |   5     |
	//      -----------
	//      |   6     |
	//      -----------
	//      |   7     |
	//      -----------

	// the function call pop(2) = [5,6]
	// but the stack now looks like this:

	// 
	// 
	// 
	// 
	//      -----------
	//      |   7     |
	//      -----------

	// This pop method is very inportant to the 
	// reduce method in parser. In usage, we would
	// then create a new element using 5 and 6, and push this
	// to the stack.

	public ArrayList<SyntaxNode> pop(int amount) {
		ArrayList<SyntaxNode> result = new ArrayList<SyntaxNode>();
		for (int x = 0; x < amount; x++) {
			result.add(data.get(0));
			data.remove(0);
		}
		return result;
	}

	// The function `peek` returns the top n items from the stack but
	// does not delete them from the stack. The parameter `amount`
	// tells the function how many to return.

	// therefore if we have a stack:

	//      -----------
	//      |   5     |
	//      -----------
	//      |   6     |
	//		-----------
	//      |   7     |
	//      -----------

	// the function call peek(2) = [5,6]
	// and the stack itself remains the same.

	//      -----------
	//      |   5     |
	//      -----------
	//      |   6     |
	//		-----------
	//      |   7     |
	//      -----------

	public ArrayList<SyntaxNode> peek(int amount) {
		ArrayList<SyntaxNode> result = new ArrayList<SyntaxNode>();
		for (int x = 0; x < amount; x++) {
			result.add(data.get(x));
		}
		return result;
	}

	// This is a helper function used to print the stack
	// out to the terminal.

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (SyntaxNode s : data) {
			sb.append(s.getType() + "\n");
		}
		return sb.toString();
	}

	// Helper function to return the size of the
	// stack currently.

	public int size() {
		return data.size();
	}
}
