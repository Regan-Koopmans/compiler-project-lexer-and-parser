/*

    Enum        : NodeType
    DESCRIPTION : All the permitted types of nodes (incomplete)

*/

package compiler.parser;

import compiler.lexer.TokenType;

// NodeType allows us to discern between SyntaxNodes. This allows us to
// perform reductions more easily in Parser.java 

public enum NodeType {

  // Terminals - These are the types of Nodes that represent leaf symbols in the language.
  //             These types correspond directly to the types defined in TokenType.

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

  IOCALL        ("IO-CALL"),
  BOOL          ("BOOL"),
  CODE          ("CODE"),
  VAR           ("VAR"),
  SVAR          ("SVAR"),
  ASSIGN        ("ASSIGN"),
  NVAR          ("NVAR"),
  NUMEXPR       ("NUMEXPR"),
  COND_BRANCH   ("COND_BRANCH"),
  COND_LOOP     ("COND_LOOP"),
  PROC_DEFS     ("PROC_DEFS"),
  PROC     	("PROC"),
  CALL 		("CALL"),
  PROG          ("PROG"),
  CALC          ("CALC"),
  INSTR         ("INSTR");

  // NEED TO ADD MORE OF THESE (eg. PROG and CODE etc)

  public final String name;

  private NodeType(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  // Translates between Token types and node types. Used for the "leaves"
  // of the tree. This code is not integral to the parsing logic, and it only
  // serves to translate between a Token to a Syntax Node in a a tree.

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
