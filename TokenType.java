/*

    CLASS       : Enum
    DESCRIPTION : Defines a valid token type in the language.

*/

public enum TokenType {

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
  Procedure          ("Procedure");

  public final String name;

  private TokenType(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
