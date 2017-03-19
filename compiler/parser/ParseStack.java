package compiler.parser;

import java.util.ArrayList;
import java.lang.StringBuilder;

public class ParseStack {
	private ArrayList<SyntaxNode> data = new ArrayList<SyntaxNode>();

	public void push(SyntaxNode input) {
		data.add(0, input);
	}

	public SyntaxNode at(int x) {
		return data.get(x);
	}

	public ArrayList<SyntaxNode> pop(int amount) {
		ArrayList<SyntaxNode> result = new ArrayList<SyntaxNode>();
		for (int x = 0; x < amount; x++) {
			result.add(data.get(0));
			data.remove(0);
		}
		return result;
	}

	public ArrayList<SyntaxNode> peek(int amount) {
		ArrayList<SyntaxNode> result = new ArrayList<SyntaxNode>();
		for (int x = 0; x < amount; x++) {
			result.add(data.get(x));
		}
		return result;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (SyntaxNode s : data) {
			sb.append(s.getType() + "\n");
		}
		return sb.toString();
	}

	public int size() {
		return data.size();
	}
}