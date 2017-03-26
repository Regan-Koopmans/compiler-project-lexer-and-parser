/*

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
    
    //private SyntaxTree tree = new SyntaxTree();
    //private ParseStack stack = new ParseStack();
    private ArrayList<Token> tokens;
    public void parse(ArrayList<Token> t)
    {
      tokens=new ArrayList<Token>(t);
      if(shiftProg())
      {
        if(tokens.size()>0)
          parsingError(tokens.get(0).toString()+"\nUnexpected input");
        else
          System.out.println("Parsing successful");
      }
    }
    public Parser()
    {
      
    }
    private boolean shiftProg() 
    {
        while(tokens.size()>0) 
        {
          Token t=tokens.get(0);
            if(NodeType.fromTokenType(t.getType())==NodeType.UserDefinedName)
            {
              ArrayList<Token> temp=new ArrayList<Token>();
              temp.add(tokens.remove(0));
              if(!shiftUserDefinedName(temp))
                return false;
              if(tokens.size()>0 && tokens.get(0).getValue().compareTo(";")==0)
              {
                temp.add(tokens.remove(0));
              }
              else if(tokens.size()>0)
              {
                parsingError(tokens.get(0).toString()+"\nExpected \';\' symbol");
                return false;
              }
              else
              {
                parsingError(t.toString()+"\nExpected more input");
                return false;
              }
            }
            else if(NodeType.fromTokenType(t.getType())==NodeType.Procedure)
            {
              tokens.remove(0);
              ArrayList<Token> temp=new ArrayList<Token>();
              if(!shiftProcedure(temp))
                return false;
              if(tokens.size()>0 && tokens.get(0).getValue().compareTo("}")==0)
              {
                temp.add(tokens.remove(0));
              }
              else if(tokens.size()>0)
              {
                parsingError(tokens.get(0).toString()+"\nExpected \';\' symbol");
                return false;
              }
              else
              {
                parsingError(t.toString()+"\nExpected input");
                return false;
              }
            }
            else if(NodeType.fromTokenType(t.getType())==NodeType.Control)
            {
              tokens.remove(t);
              ArrayList<Token> temp=new ArrayList<Token>();
              if(!shiftControl(temp))
                return false;
            }
            else if(t.getValue().compareTo("}")==0)
              return true;
            else
            {
              parsingError("\n"+t.toString()+"\nUnexpected token type");
              return false;
            }
        }
        return true;
    }

    // This is the function shift from SLR parsing with a stack
    // in the textbook.
    private boolean shiftUserDefinedName(ArrayList<Token> temp)
    {
      if(tokens.size()==0)
      {
        parsingError("Expected input");
        return false;
      }
      Token t=tokens.remove(0);
      if( t.getType()==TokenType.Assignment)
      {
        temp.add(t);
        
        if(shiftShortString(temp))
        {
          if(tokens.size()>0 && tokens.get(0).getType()==TokenType.Grouping)
            return true;
          else if(tokens.size()>0)
          {
            parsingError(tokens.get(0).toString()+"\nUnexpected type");
            return false;
          }
          else
          {
            parsingError(t.toString()+"Expected input");
            return false;
          }
        }
        if(shiftNumberOp(temp))
        {
          if(tokens.size()>0)
          {
            if(tokens.get(0).getType()==TokenType.Grouping)
              return true;
            else
            {
              parsingError("Expected grouping symbol");
              return false;
            }
          }
          parsingError("\n"+temp.get(temp.size()-1).toString()+"\nExpected input");
          return false;
        }
        if(shiftBooleanOp(temp))
          return true;
        if(tokens.size()>0 && tokens.get(0).getType()==TokenType.UserDefinedName)
        {
          temp.add(tokens.remove(0));
          if(tokens.size()>0 && tokens.get(0).getType()==TokenType.Grouping)
            return true;
          else if(tokens.size()>0)
          {
            parsingError(tokens.get(0).toString()+"\nUnexpected token type");
            return false;
          }
          else
          {
            parsingError(t.toString()+"\nExpected input");
            return false;
          }
        }
        else if(tokens.size()>0)
        {
          parsingError(t.toString()+"\nUnexpected symbol");
          return false;
        }
      }
      parsingError("Expected assignment symbol");
      return false;
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
              parsingError(t.toString()+"\nExpected a \'{\' symbol");
              return false;
            }
          }
          else
          {
            parsingError(t.toString()+"\nExpected more input");
            return false;
          }
        }
        else
        {
          parsingError(t.toString()+"\nExpected a user defined name");
          return false;
        }
      }
      parsingError("Procedure not properly defined");
      return true;
    }
    
    private boolean shiftIO(ArrayList<Token> temp)
    {
      return false;
    }
    private boolean shiftBooleanOp(ArrayList<Token>temp)
    {
      
      return false;
    }
    private boolean shiftControl(ArrayList<Token>temp)
    {
      return false;
    }
    private boolean shiftShortString(ArrayList<Token> temp)
    {
      return false;
    }
    private boolean shiftNumberOp(ArrayList<Token> temp)
    {
      if(tokens.size()>=6)
      {
        if(tokens.get(0).getType()==TokenType.NumberOp && tokens.get(1).getValue().compareTo("(")==0)
        {
          temp.add(tokens.remove(0));
          temp.add(tokens.remove(0));
          if(tokens.get(0).getType()==TokenType.Integer)
          {
            temp.add(tokens.remove(0));
            if(tokens.get(0).getValue().compareTo(",")==0)
            {
              temp.add(tokens.remove(0));
              if(tokens.get(0).getType()==TokenType.Integer)
              {
                temp.add(tokens.remove(0));
                if(tokens.get(0).getValue().compareTo(")")==0)
                {
                  temp.add(tokens.remove(0));
                  return true;
                }
                else
                {
                  parsingError(tokens.get(0).toString()+"\nExpected \')\'");
                  return false;
                }
              }
              else
              {
                parsingError(tokens.get(0).toString()+"\nExpected integer type");
                return false;
              }
            }
            else
            {
              parsingError(temp.get(temp.size()-1).toString()+"\nExpected \',\' symbol");
              return false;
            }
          }
          else
          {
            parsingError(temp.get(temp.size()-1).toString()+"\nExpected integer type");
            return false;
          }
        }
      }
      return false;
    }
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
