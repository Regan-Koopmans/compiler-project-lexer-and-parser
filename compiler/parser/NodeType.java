/*

    Enum        : NodeType
    DESCRIPTION : All the permitted types of nodes (incomplete)

*/

package compiler.parser;

public enum NodeType {
  Comparison         ("Comparison"),
  BooleanOp          ("Boolean Operator"),
  NumberOp           ("Number Operator"),
  String             ("String"),
  Assignment         ("Assignment"),
  Control            ("Control"),
  IO                 ("IO"),
  Integer            ("Integer"),
  Halt               ("Halt"),
  UserDefinedName    ("User Defined Name"),
  Procedure          ("Procedure"),
  Grouping           ("Grouping"),
  ShortString        ("Short String");
  // NEED TO ADD MORE OF THESE (eg. PROG and CODE etc)


  public final String name;

  private NodeType(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}