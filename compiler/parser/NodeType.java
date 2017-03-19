/*

    Enum        : NodeType
    DESCRIPTION : All the permitted types of nodes (incomplete)

*/

package compiler.parser;

import compiler.lexer.TokenType;

public enum NodeType {

  // Terminals

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
  ShortString        ("Short String"),

  // Non Terminals

  INSTR ("INSTR");

  // NEED TO ADD MORE OF THESE (eg. PROG and CODE etc)


  public final String name;

  private NodeType(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  // Translates between Token types and node types. Used for the "leaves"
  // of the tree.

  public static NodeType fromTokenType(TokenType token) {
    NodeType coercedType = IO;
    switch(token) {
      case Comparison       : coercedType = NodeType.Comparison;      break;
      case BooleanOp        : coercedType = NodeType.BooleanOp;       break;         
      case NumberOp         : coercedType = NodeType.NumberOp;        break;    
      case String           : coercedType = NodeType.String;          break;        
      case Assignment       : coercedType = NodeType.Assignment;      break;       
      case Control          : coercedType = NodeType.Control;         break;        
      case IO               : coercedType = NodeType.IO;              break;        
      case Integer          : coercedType = NodeType.Integer ;        break;        
      case Halt             : coercedType = NodeType.Halt ;           break;              
      case UserDefinedName  : coercedType = NodeType.UserDefinedName; break;    
      case Procedure        : coercedType = NodeType.Procedure;       break;          
      case Grouping         : coercedType = NodeType.Grouping;        break;           
      case ShortString      : coercedType = NodeType.ShortString ;    break;
    }
    return coercedType;
  }
}