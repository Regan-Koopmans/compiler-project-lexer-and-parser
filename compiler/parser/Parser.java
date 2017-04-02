/*
 * 
 CLASS       : Parser
 DESCRIPTION : Checks whether a list of tokens is syntactically correct by constructing
 a syntax tree.
 
 */
package compiler.parser;

import java.util.ArrayList;
import compiler.lexer.Token;
import compiler.lexer.TokenType;
//import java.util.Collections;

public class Parser {
  /***********************************************************************************************
    * 
    * I just commented out the tree and stack variables to avoid the warning messages as I compiled
    * 
    * You will note that I made the shift functions private. I am just a coding Nazi like that :-D
    *
    ************************************************************************************************
    * 
    * I am just using the runtime stack to help parse the given lexer tokens list
    * 
    ***********************************************************************************************/
  //private SyntaxTree tree = new SyntaxTree();
  //private ParseStack stack = new ParseStack();
  private ArrayList<Token> tokens;
  private ArrayList<Token> accepted;
  
  /*
   * This is still the function that you access in your main
   * 
   * It will take the passed in list of tokens and deep copy it into
   * the the global variable "tokens"
   * 
   * It will then call shiftProg to start parsing the list of tokens
   */
  public void parse(ArrayList<Token> t)
  {
    if(t.size()==0)
    {
      parsingError("No tokens to parse");
      return;
    }
 
    tokens=new ArrayList<Token>(t);
    if(shiftProg())
    {
      /*
       * if tokens is not empty after shiftProg() is finished executing then 
       * there must be an grouping token at the end of the lost, which most
       * likely is never used
       */
      if(tokens.size()>0)
   {
        parsingError("\n"+tokens.get(0).toString()+"\nUnexpected input");
  return;
   }
      else
        System.out.println("Parsing successful");
   accepted=new ArrayList<Token>(t);
   buildTree();
    }
  }
  private void buildTree()
  {
   
  }
  public Parser()
  {
    
  }
  private boolean shiftProg() 
  {
    /*
     * this is when start with the production rules
     * 
     * PROG-> CODE
     * PROG-> CODE;PROC_DEFS
     * 
     * if the token list still has something then we will keep traversing
     * and everytime we traverse it, we literally removing the front element
     * That is why you will mostly see tokens.get(0) and tokens.remove(0)
     * 
     */ 
    while(tokens.size()>0) 
    {
      Token t=tokens.get(0);
      /*
       * CODE->INSTR
       * 
       * We check if the token at the beginning is a user defined name
       * 
       */ 
      if(NodeType.fromTokenType(t.getType())==NodeType.Halt)
      {
        tokens.remove(0);
        if(tokens.size()>0)
        {
          if(tokens.get(0).getValue().compareTo(";")!=0)
          {
            if(tokens.size()>1)
            {
              if(tokens.get(0).getValue().compareTo("}")==0)
              {
                continue;
              }
              else
              {
                parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
                return false;
              }
            }
            else
            {
              parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
              return false;
            }
          }
          else
            tokens.remove(0);
        }
        
      }
      else if(NodeType.fromTokenType(t.getType())==NodeType.UserDefinedName)
      {
        ArrayList<Token> temp=new ArrayList<Token>();
        // remove the element
        temp.add(tokens.remove(0));
        /*
         * [tom] [;] [jane]
         * [tom] is then removed
         * shiftUserDefinedName() then accepts the temporary ArrayList with [tom]
         * 
         * if the expected input is not given then shiftUserDefinedName() returns false
         * 
         */
        if(tokens.size()==0)
          return true;
        if(tokens.get(0).getValue().compareTo("}")==0)
        {
          continue;
        }
        if(!shiftUserDefinedName(temp))
          return false;
        /*
         * if shiftUserDefinedName() returns true then we are expecting a grouping symbol 
         * at the start of the tokens list
         * 
         * temp=[tom][=][jane]
         * tokens=[;]
         * 
         */
        if(tokens.size()==0)
          return true;
        if(tokens.get(0).getValue().compareTo("}")==0)
        {
          continue;
        }
        if(tokens.size()>0 && tokens.get(0).getValue().compareTo(";")==0)
        {
          if(tokens.size()>1)
          {
            if(tokens.get(1).getValue().compareTo("}")==0)
            {
              parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
              return false;
            }
            else
              tokens.remove(0);
          }
          else
          {
            parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
            return false;
          }
          /*
           * this is when we find a grouping token (;) and we know that this item is complete 
           * 
           * temp=[tom][=][jane][;]
           * 
           * I don't reduce in the traditional sense. I am not creating  tree as we go along 
           * but it can easily be converted to reduce by either sending to a designated stack
           * 
           * At the end of it I remove the (;) token so as to move on
           * 
           */ 
        }
        else if(tokens.size()>0)
        {
          /*
           * This is when check the size of the token list so we know what type of error message to print
           * 
           * in this case say we have:
           * 
           * temp=[tom][=][jane]
           * tokens=[{]
           * 
           * the tokens list still has something but it is not what should expected, hence the specific error
           * message
           * 
           */ 
          parsingError("\n"+tokens.get(0).toString()+"\nExpected \';\' symbol");
          return false;
        }
        else
        {
          /*
           * This is when we have something like
           * 
           * temp=[tom][=][jane]
           * tokens=[]
           */ 
          parsingError("\n"+t.toString()+"\nExpected more input");
          return false;
        }
      }
      /*
       * PROG->PROC_DEFS
       * PROC_DEFS-> PROC PROC_DEFS
       *
       * Now we are checking if the first  token on the tokens list is a procedure token
       * 
       */ 
      else if(NodeType.fromTokenType(t.getType())==NodeType.Procedure)
      {
        /*
         * Remove the "proc" token at the beginning of the list and then pass the temp list 
         * into the shiftProceduce()
         */ 
        
        ArrayList<Token> temp=new ArrayList<Token>();
        temp.add(tokens.remove(0));
        /*
         * This would return false if the parameters of the procedure call are not met
         * 
         */ 
        if(!shiftProcedure(temp))
          return false;
        if(tokens.size()>0 && tokens.get(0).getValue().compareTo("}")==0)
        {
          /*
           * 
           * At the end of it we know that the last token should be a grouping token AND "}"
           * 
           * temp=[proc][hello][{][ ... PROG ...]
           * tokens=[}][... CODE ...]
           * 
           */ 
          temp.add(tokens.remove(0));
        }
        else if(tokens.size()>0)
        {
          parsingError("\n"+tokens.get(0).toString()+"\nExpected \'}\' symbol");
          return false;
        }
        else
        {
          parsingError("\n"+t.toString()+"\nExpected input");
          return false;
        }
        
      }
      else if(NodeType.fromTokenType(t.getType())==NodeType.Control 
                &&(t.getValue().compareTo("if")==0 || t.getValue().compareTo("for")==0 || t.getValue().compareTo("while")==0))
      {
        /*
         * 
         * We have encountered a control type of token.
         * 
         * For this we could first check if the first token in the tokens list is of type "for" or "if" or "while"
         * This is to make sure that we don't accidentally parse "else" and "then" and declare the parsing a success
         * 
         */
        
        ArrayList<Token> temp=new ArrayList<Token>();
        temp.add(tokens.remove(0));
        if(!shiftControl(temp))
          return false;
      }
      else if(tokens.get(0).getType()==TokenType.IO)
      {
        if(!shiftIO())
          return false;
        if(tokens.size()==0)
          return true;
        if(tokens.get(0).getValue().compareTo("}")==0)
        {
          return true;
        }
        
        if(tokens.get(0).getValue().compareTo(";")==0)
        {
          if(tokens.size()>1)
          {
            if(tokens.get(1).getValue().compareTo("}")==0)
            {
              parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
              return false;
            }
            else
            {
              //System.out.println("k");
              tokens.remove(0);
            }
          }
          else
          {
            parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
            return false;
          }
        }
        else
        {
          parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
          return false;
        }
      }
      /*
       * 
       * Here we assume that if the token infront of the tokens list is "}" then we are probably in a control structure
       * so it would be safer to just return true without removing the token. The shift of the control structure will
       * deal with the token IF it exists. If the lexer gave us something like:
       * 
       * lexer=[}]
       * 
       * we just return true and the parse() will first check if the tokens list is empty before prematurely declaring 
       * that we parsed the code prematurely
       * 
       */ 
      else if(t.getValue().compareTo("}")==0) 
        return true;
      /*
       * This is when we know that what was passed was not a FIRST or FOLLOW element of our productions
       * 
       */ 
      else
      {
        parsingError("\n"+t.toString()+"\nUnexpected token type");
        return false;
      }
    }
    return true;
  }
  
  /*
   * ASSIGN->SVAR=String
   * ASSIGN->SVAR=SVAR
   * SVAR= UserDefinedName
   */ 
  private boolean shiftUserDefinedName(ArrayList<Token> temp)
  {
    /*
     * Always check if out tokens list is not empty
     */ 
    if(tokens.size()==0)
    {
      return true;
    }
    /*
     * we know that when assigning the next variable in our tokens list should be an assignmnet token eg
     * 
     * temp=[tom]
     * tokens=[=][jane][;]
     * 
     */ 
    Token t=tokens.remove(0);
    /*
     * STATE
     * 
     * temp=[tom]
     * tokens=[jane][;]
     * t=[=]
     * 
     */ 
    if( t.getType()==TokenType.Assignment)
    {
      temp.add(t);
      /*
       * we shift using the shiftShortString to make sure that we are not assigning a short string token
       * 
       */ 
      if(shiftShortString(temp))
      {
        return true;
        /*
         * 
         * If we did assign a short string token then we know that the next token should be a grouping one
         * We don't care at the moment what type of grouping token we have at the moment. The function 
         * that called shiftUserDefinedName() will take of that accordingly
         * 
         */ 
        
      }
   else if(tokens.size()>0 && tokens.get(0).getType()==TokenType.Integer)
   {
    t=tokens.remove(0);
    return true;
    
   }
      else if(shiftNumberOp(temp))
      {
        /*
         * This is when find out that we are assigning from a number operation
         */ 
        
        return true;
      }
      /*
       * 
       * We check if it is of the 
       * 
       * ASSIGN->SVAR=SVAR
       * SVAR->UserDefinedName
       * 
       */ 
      else if(tokens.size()>0 && tokens.get(0).getType()==TokenType.UserDefinedName)
      {
        temp.add(tokens.remove(0));
        return true;
        /*
         * 
         * Remove the token and check if the next one is a grouping token
         * 
         * temp=[tom][=][jane]
         * tokens=[;] or tokens=[{] etc
         * 
         */ 
        
      }
      /*
       * 
       * When all else has failed but the tokens list is not empty then we print an error 
       * message and return false
       */ 
      else if(tokens.size()>0)
      {
        parsingError("\n"+t.toString()+"\nUnexpected symbol");
        return false;
      }
    }
    /*
     * 
     * This when we did not receive an assignment type token in our tokens list
     * 
     * temp=[tom]
     * tokens=[;]
     * 
     */ 
    tokens.add(0,t);
    return true;
  }
  
  private boolean shiftProcedure(ArrayList<Token> temp)
  {
    if(tokens.size()>0)
    {
      Token t=tokens.remove(0);
      if(t.getType()==TokenType.UserDefinedName)
      {
        temp.add(t);
        if(tokens.size()>0)
        {
          t=tokens.remove(0);
          if(t.getValue().compareTo("{")==0)
          {
            temp.add(t);
            if(!shiftProg())
              return false;
            else
              return true;
          }
          else
          {
            parsingError("\n"+t.toString()+"\nExpected a \'{\' symbol");
            return false;
          }
        }
        else
        {
          parsingError("\n"+t.toString()+"\nExpected more input");
          return false;
        }
      }
      else
      {
        parsingError("\n"+t.toString()+"\nExpected a user defined name");
        return false;
      }
    }
    parsingError("Procedure not properly defined");
    return false;
  }
  
  
  private boolean shiftIO()
  {
    Token t=tokens.remove(0);
    if(tokens.size()>0)
    {
      if(tokens.get(0).getValue().compareTo("(")==0)
      {
        t=tokens.remove(0);
        if(tokens.size()>0)
        {
          t=tokens.remove(0);
          if(t.getType()==TokenType.Integer || t.getType()==TokenType.UserDefinedName)
          {
            if(tokens.size()>0)
            {
              t=tokens.remove(0);
              if(t.getValue().compareTo(")")==0)
                return true;
              else
                parsingError("\n"+t.toString()+"\nExpected \")\" symbol");
            }
            else
              parsingError("\n"+t.toString()+"\nExpected input");
          }
          else
            parsingError("\n"+t.toString()+"\nExpected integer or user defined name token type");
        }
        else
          parsingError("\n"+t.toString()+"\nExpected input");
      }
      else
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"(\" symbol");
    }
    else
      parsingError("Expected input");
    return false;
  }
  private boolean shiftBooleanOp()
  {
    if(tokens.size()>0)
    {
      if(tokens.get(0).getValue().compareTo("(")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"(\" symbol");
        return false;
      }
      return isBool();
    }
    else
      parsingError("Expected boolean operation");
    return false;
  }
  private boolean isBool()
  {
    if(tokens.size()<=0)
    {
      parsingError("Expected input");
      return false;
    }
    Token t=tokens.get(0);
    if(t.getValue().compareTo("(")==0)
    {
      tokens.remove(0);
      if(!isBool())
        return false;
      if(tokens.size()<=0)
        return false;
      t=tokens.remove(0);
      if(t.getValue().compareTo(")")!=0)
        return false;
      return true;
    }
    else if(t.getType()==TokenType.UserDefinedName)
    {
      tokens.remove(0);
      if(tokens.size()<2)
      {
        parsingError("Expected input after:\n"+t.toString());
        return false;
      }
      if(tokens.get(0).getValue().compareTo("<")==0 || tokens.get(0).getValue().compareTo(">")==0)
      {
        tokens.remove(0);
        if(tokens.get(0).getType()==TokenType.UserDefinedName)
        {
          tokens.remove(0);
          return true;
        }
        else
          parsingError("\n"+tokens.get(0).toString()+"\nExpected a user defined name token");
      }
      else
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"<\" or \"<\" symbols");
      return false;
    }
    else if(t.getValue().compareTo("not")==0)
    {
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected boolean operation or comparison token type or \"(\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo("(")==0||tokens.get(0).getType()==TokenType.BooleanOp||tokens.get(0).getType()==TokenType.Comparison)
        return isBool();
      parsingError("\n"+tokens.get(0).toString()+"\nExpected boolean operation or comparison token type or \"(\" symbol");
      return false;
    }
    else if(t.getValue().compareTo("and")==0 || t.getValue().compareTo("or")==0)
    {
      tokens.remove(0);
      if(tokens.size()<=0)
      {
        parsingError("Expected input after:\n"+t.toString());
        return false;
      }
      t=tokens.remove(0);
      if(t.getValue().compareTo("(")!=0)
      {
        parsingError("\n"+t.toString()+"\nExpected \"(\" symbol");
        return false;
      }
      if(!isBool())
        return false;
      if(tokens.size()<=0)
      {
        parsingError("Expected \",\" symbol");
        return false;
      }
      t=tokens.remove(0);
      if(t.getValue().compareTo(",")!=0)
      {
        parsingError("\n"+t.toString()+"\nExpected \",\" symbol");
        return false;
      }
      if(!isBool())
        return false;
      if(tokens.size()<=0)
      {
        parsingError("Expected \")\" symbol");
        return false;
      }
      t=tokens.remove(0);
      if(t.getValue().compareTo(")")!=0)
      {
        parsingError("\n"+t.toString()+"\nExpected \")\" symbol");
        return false;
      }
      return true;
    }
    else if(t.getValue().compareTo("eq")==0)
    {
      tokens.remove(0);
      if(tokens.size()<=0)
      {
        parsingError("Expected \"(\" symbol");
        return false;
      }
      t=tokens.remove(0);
      if(t.getValue().compareTo("(")!=0)
      {
        parsingError("\n"+t.toString()+"\nExpected \"(\" symbol");
        return false;
      }
      if(tokens.size()<=0)
      {
        parsingError("Expected user defined name token");
        return false;
      }
      t=tokens.remove(0);
      if(t.getType()!=TokenType.UserDefinedName)
      {
        parsingError("\n"+t.toString()+"\nExpected user defined name token");
        return false;
      }
      if(tokens.size()<=0)
      {
        parsingError("Expected \",\" symbol");
        return false;
      }
      t=tokens.remove(0);
      if(t.getValue().compareTo(",")!=0)
      {
        parsingError("\n"+t.toString()+"\nExpected \",\" symbol");
        return false;
      }
      if(tokens.size()<=0)
      {
        parsingError("Expected user defined name token");
        return false;
      }
      t=tokens.remove(0);
      if(t.getType()!=TokenType.UserDefinedName)
      {
        parsingError("\n"+t.toString()+"\nExpected user defined name token");
        return false;
      }
      if(tokens.size()<=0)
      {
        parsingError("Expected \")\" symbol");
        return false;
      }
      t=tokens.remove(0);
      if(t.getValue().compareTo(")")!=0)
      {
        parsingError("\n"+t.toString()+"\nExpected \")\" symbol");
        return false;
      }
      return true;
    }
    else
    {
      tokens.remove(0);
      parsingError("\n"+t.toString()+"\nUnexpected token type");
      return false;
    }
  }
  private boolean shiftControl(ArrayList<Token>temp)
  {
    if(tokens.size()<0)
      return false;
    
    if(temp.get(temp.size()-1).getValue().compareTo("if")==0)
    {
      if(!shiftBooleanOp())
        return false;
      if(!isThen())
        return false;
      if(tokens.size()==0)
          return true;
      if(tokens.get(0).getValue().compareTo("}")==0)
      {
        return true;
      }
      if(tokens.size()>0&&tokens.get(0).getValue().compareTo(";")==0)
      {
        if(tokens.size()>1)
        {
          if(tokens.get(1).getValue().compareTo("}")==0)
          {
            parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
            return false;
          }
          else
          {
            tokens.remove(0);
            return true;
          }
        }
        else
        {
          parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
          return false;
        }
      }
      else if(isElse())
      {
        if(tokens.size()==0)
          return true;
      if(tokens.get(0).getValue().compareTo("}")==0)
      {
        return true;
      }
        if(tokens.size()>0 &&tokens.get(0).getValue().compareTo(";")==0)
        {
          if(tokens.size()>1)
          {
            if(tokens.get(1).getValue().compareTo("}")==0)
            {
              parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
              return false;
            }
            else
            {
              tokens.remove(0);
              return true;
            }
          }
          else
          {
            parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
            return false;
          }
        }
        else if(tokens.size()>0)
        {
          parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
          return false;
        }
        else
        {
          parsingError("Expected input");
          return false;
        }
      }
      else
        return false;
    }
    else if(temp.get(temp.size()-1).getValue().compareTo("while")==0)
    {
      if(!shiftBooleanOp())
        return false;
      if(tokens.size()==0)
      {
        parsingError("\n"+temp.get(temp.size()-1).toString()+"\nExpected input");
        return false;
      }
      if(tokens.get(0).getValue().compareTo("{")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"{\" symbol");
        return false;
      }
      tokens.remove(0);
      if(!shiftProg())
        return false;
      if(tokens.size()==0)
      {
        parsingError("Expected \"}\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo("}")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"}\" symbol");
        return false;
      }
      tokens.remove(0);
      if(tokens.size()==0)
          return true;
      if(tokens.get(0).getValue().compareTo("}")==0)
      {
        return true;
      }
      if(tokens.size()>0 &&tokens.get(0).getValue().compareTo(";")==0)
      {
        if(tokens.size()>1)
        {
          if(tokens.get(1).getValue().compareTo("}")==0)
          {
            parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
            return false;
          }
          else
          {
            tokens.remove(0);
            return true;
          }
        }
        else
        {
          parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
          return false;
        }
      }
      else if(tokens.size()>0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
        return false;
      }
      else
      {
        parsingError("Expected input");
        return false;
      }
    }
    else if(temp.get(temp.size()-1).getValue().compareTo("for")==0)
    {
      if(tokens.size()==0)
      {
        parsingError("Expected \"(\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo("(")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"(\" symbol");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected user defined name token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.UserDefinedName)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected user defined name token type");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected assignment token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.Assignment)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected assignment token type");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected integer token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.Integer)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected integer token type");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected \";\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo(";")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected user defined name token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.UserDefinedName)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected user defined name token type");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected \"<\" or \">\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo("<")!=0 && tokens.get(0).getValue().compareTo(">")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"<\" or \">\" symbol");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected user defined name token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.UserDefinedName)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected user defined name token type");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected \";\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo(";")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected user defined name token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.UserDefinedName)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected user defined name token type");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected assignment token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.Assignment)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected assignment token type");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected number operation token type");
        return false;
      }
      if(tokens.get(0).getType()!=TokenType.NumberOp)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected number operation token type");
        return false;
      } 
      if(!shiftNumberOp(new ArrayList<Token>()))
        return false;
      if(tokens.size()==0)
      {
        parsingError("Expected \")\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo(")")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \")\" symbol");
        return false;
      } 
      tokens.remove(0);
      if(tokens.size()==0)
      {
        parsingError("Expected \"{\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo("{")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"{\" symbol");
        return false;
      } 
      tokens.remove(0);
      if(!shiftProg())
        return false;
      if(tokens.size()==0)
      {
        parsingError("Expected \"}\" symbol");
        return false;
      }
      if(tokens.get(0).getValue().compareTo("}")!=0)
      {
        parsingError("\n"+tokens.get(0).toString()+"\nExpected \"}\" symbol");
        return false;
      } 
      tokens.remove(0);
      
      if(tokens.size()==0)
          return true;
      else if(tokens.get(0).getValue().compareTo("}")==0)
      {
        return true;
      }
      
      if(tokens.size()>0 &&tokens.get(0).getValue().compareTo(";")==0)
        {
          if(tokens.size()>1)
          {
            if(tokens.get(1).getValue().compareTo("}")==0)
            {
              parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
              return false;
            }
            else
            {
              tokens.remove(0);
              return true;
            }
          }
          else
          {
            parsingError("\n"+tokens.get(0).toString()+"\nUnexpected token type");
            return false;
          }
        }
        else if(tokens.size()>0)
        {
          parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
          return false;
        }
        else
        {
          parsingError("Expected input");
          return false;
        }
    }
    return false;
  }
  private boolean isThen()
  {
    if(tokens.size()==0)
    {
      parsingError("Expected \"then\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo("then")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \"then\" symbol");
      return false;
    }
    tokens.remove(0);
    if(tokens.size()==0)
    {
      parsingError("Expected \"{\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo("{")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \"{\" symbol");
      return false;
    }
    tokens.remove(0);
    if(!shiftProg())
      return false;
    if(tokens.size()==0)
    {
      parsingError("Expected \"}\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo("}")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \"}\" symbol");
      return false;
    }
    tokens.remove(0);
    return true;
  }
  private boolean isElse()
  {
    if(tokens.size()==0 || tokens.get(0).getValue().compareTo("else")!=0)
      return true;
    tokens.remove(0);
    if(tokens.size()==0)
    {
      parsingError("Expected \"{\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo("{")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \"{\" symbol");
      return false;
    }
    tokens.remove(0);
    if(!shiftProg())
      return false;
    if(tokens.size()==0)
    {
      parsingError("Expected \"}\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo("}")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \"}\" symbol");
      return false;
    }
    tokens.remove(0);
    return true;
  }
  private boolean shiftShortString(ArrayList<Token> temp)
  {
    if(tokens.size()==0)
      return false;
    if(tokens.get(0).getType()==TokenType.ShortString)
    {
      //temp.add(tokens.remove(0));
      temp.add(tokens.remove(0));
      return true;
    }
    return false;
  }
  
  /*
   * 
   * This is when we check if the next tokens in our tokens list will create a number operation
   * 
   */ 
  private boolean shiftNumberOp(ArrayList<Token> temp)
  {
    if(tokens.size()==0 || tokens.get(0).getType()!=TokenType.NumberOp)
      return false;
    tokens.remove(0);
    if(tokens.size()==0)
    {
      parsingError("Expected \"(\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo("(")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \"(\" symbol");
      return false;
    }
    tokens.remove(0);
    if(tokens.size()==0)
    {
      parsingError("Expected number operation or integer or user defined name token type");
      return false;
    }
    if(tokens.get(0).getType()!=TokenType.Integer)
    {
      if(tokens.get(0).getType()!=TokenType.NumberOp)
      {
        if(tokens.get(0).getType()!=TokenType.UserDefinedName)
        {
          parsingError("\n"+tokens.get(0).toString()+"\nExpected number operation or integer or user defined name token type");
          return false;
        }
      }
    }
    if(tokens.get(0).getType()==TokenType.NumberOp)
    {
      if(!shiftNumberOp(temp))
        return false;
    }
    else
      tokens.remove(0);
    if(tokens.size()==0)
    {
      parsingError("Expected \",\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo(",")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \",\" symbol");
      return false;
    }
    tokens.remove(0);
    
    if(tokens.size()==0)
    {
      parsingError("Expected number operation or integer or user defined name token type");
      return false;
    }
    if(tokens.get(0).getType()!=TokenType.Integer)
    {
      if(tokens.get(0).getType()!=TokenType.NumberOp)
      {
        if(tokens.get(0).getType()!=TokenType.UserDefinedName)
        {
          parsingError("\n"+tokens.get(0).toString()+"\nExpected number operation or integer or user defined name token type");
          return false;
        }
      }
    }
    if(tokens.get(0).getType()==TokenType.NumberOp)
    {
      if(!shiftNumberOp(temp))
        return false;
    }
    else
      tokens.remove(0);
    if(tokens.size()==0)
    {
      parsingError("Exxpected \")\" symbol");
      return false;
    }
    if(tokens.get(0).getValue().compareTo(")")!=0)
    {
      parsingError("\n"+tokens.get(0).toString()+"\nExpected \")\" symbol");
      return false;
    }
    tokens.remove(0);
    return true;
  }
  
  // This is the function I was using to print error messages
  
  private void parsingError(String temp)
  {
    System.out.println("Parsing error: "+temp);
  }
  // Function to return a syntax tree. 
  
  // Note: I have not written any code to convert the compressed stack into a tree
  // so the tree is always NULL at this point.
  
  public void generateTree() {
    return;
  }
  
  //public SyntaxTree getTree() { return tree; }
}
