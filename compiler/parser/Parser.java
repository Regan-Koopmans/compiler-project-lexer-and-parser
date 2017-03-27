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
      tokens=new ArrayList<Token>(t);
      if(shiftProg())
      {
        /*
         * if the tokens list is not empty after shiftProg() is finished executing then 
         * there must be a grouping token at the end of the list, which most
         * likely is never used
         */
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
      /*
       * this is when start with the production rules
       * 
       * PROG-> CODE
       * PROG-> CODE;PROC_DEFS
       * 
       * if the token list still has something then we will keep traversing
       * and everytime we traverse it, we literally remove the front element
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
                        parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
                        return false;
                    }
                    else
                        tokens.remove(0);
                }
                else
                {
                    parsingError("\n"+t.toString()+"\nExpected input");
                    return false;
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
              if(tokens.size()>0 && tokens.get(0).getValue().compareTo(";")==0)
              {
                temp.add(tokens.remove(0));
                /*
                 * this is when we find a grouping token (;) and we know that this item is complete 
                 * 
                 * temp=[tom][=][jane][;]
                 * 
                 * I don't reduce in the traditional sense. I am not creating a tree as we go along 
                 * but it can easily be converted to reduce by either sending to a designated stack
                 * or creating the tree as we traverse the list
                 * 
                 * At the end of it I remove the (;) token so as to move on
                 * 
                 */ 
              }
              else if(tokens.size()>0)
              {
                /*
                 * This is when check we the size of the token list so we know what type of error message to print
                 * 
                 * in this case, say we have:
                 * 
                 * temp=[tom][=][jane]
                 * tokens=[{]
                 * 
                 * the tokens list still has something but it is not what should expected, hence the specific error
                 * message
                 * 
                 */ 
                parsingError(tokens.get(0).toString()+"\nExpected \';\' symbol");
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
                parsingError(t.toString()+"\nExpected more input");
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
              tokens.remove(0);
              ArrayList<Token> temp=new ArrayList<Token>();
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
                parsingError(tokens.get(0).toString()+"\nExpected \'}\' symbol");
                return false;
              }
              else
              {
                parsingError(t.toString()+"\nExpected input");
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
              tokens.remove(t);
              ArrayList<Token> temp=new ArrayList<Token>();
              temp.add(t);
              if(!shiftControl(temp))
                return false;
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
             * that we parsed the code successfully
             * 
             */ 
            else if(tokens.get(0).getType()==TokenType.IO)
            { 
                if(!shiftIO())
                    return false;
                if(tokens.size()>0)
                {
                    if(tokens.get(0).getValue().compareTo(";")==0)
                        tokens.remove(0);
                    else
                    {
                        parsingError("\n"+tokens.get(0).toString()+"\nExpected \";\" symbol");
                        return false;
                    }
                }
                else
                {
                    parsingError("Expected input at:\n"+t.toString());
                    return false;
                }
          }
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
        parsingError("Expected input");
        return false;
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
          /*
           * 
           * If we did assign a short string token then we know that the next token should be a grouping one
           * We don't care at the moment what type of grouping token we have at the moment. The function 
           * that called shiftUserDefinedName() will take of that accordingly
           * 
           */ 
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
          /*
           * This is when find out that we are assigning from a number operation
           */ 
          if(tokens.size()>0)
          {
            /*
             * Again, we just check if the token at the front of the tokens list is a 
             * group token but we do not care what value it has at the moment
             */ 
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
        if(shiftBooleanOp(temp)) // this is when we check if it a boolean operation. We must implement it in a similar fashion as shiftNumberOp
          return true;
        /*
         * 
         * We check if it is of the 
         * 
         * ASSIGN->SVAR=SVAR
         * SVAR->UserDefinedName
         * 
         */ 
        if(tokens.size()>0 && tokens.get(0).getType()==TokenType.UserDefinedName)
        {
          temp.add(tokens.remove(0));
          
          /*
           * 
           * Remove the token and check if the next one is a grouping token
           * 
           * temp=[tom][=][jane]
           * tokens=[;] or tokens=[{] etc
           * 
           */ 
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
        /*
         * 
         * When all else has failed but the tokens list is not empty then we print an error 
         * message and return false
         */ 
        else if(tokens.size()>0)
        {
          parsingError(t.toString()+"\nUnexpected symbol");
          return false;
        }
      }
      /*
       * 
       * This when we did not receive an assignment type token in our tokens list
       * 
       * temp=[tom]
       * tokens=[pot][;]
       * 
       */ 
      parsingError("Expected assignment symbol");
      return false;
    }
    
    /*
     * 
     * still working on this one
     * 
     */ 
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
      if(tokens.size()>=2)
        return false;
      if(tokens.get(0).getType()==TokenType.Assignment && tokens.get(1).getType()==TokenType.ShortString)
      {
        temp.add(tokens.remove(0));
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
      /*
       * 
       * we do not start by removing tokens. This is to make sure that we don't interrupt the ordering 
       * of our tokens list unnecessarily.
       * 
       * Checking if the tokens list is empty, is always ideal before operations
       * 
       */ 
      if(tokens.size()>=6)
      {
        /*
         * 
         * we check if the size of the tokens list is 6 or greater because we know inorder to make a number operation
         * we must have something of the form:
         * 
         * temp=[tom][=]
         * tokens=[ add ][ ( ][ integer ][ , ][ integer ][ ) ]
         * 
         */ 
        if(tokens.get(0).getType()==TokenType.NumberOp && tokens.get(1).getValue().compareTo("(")==0)
        {
          /*
           * We first checked for a number operation token and a "(" symbol. That is how we will know that we are
           * heading in the right direction
           *
           */ 
          temp.add(tokens.remove(0));
          temp.add(tokens.remove(0));
          
          /*
           * STATE
           * 
           * temp=[age][=][ add ][ ( ]
           * tokens=[ integer ][ , ][ integer ][ ) ]
           * 
           */ 
          if(tokens.get(0).getType()==TokenType.Integer || tokens.get(0).getType()==TokenType.UserDefined)
          {
            temp.add(tokens.remove(0));
            /*
             * STATE
             * 
             * temp=[age][=][ add ][ ( ][ integer ]
             * tokens=[ , ][ integer ][ ) ]
             * 
             */ 
            if(tokens.get(0).getValue().compareTo(",")==0)
            {
              
              temp.add(tokens.remove(0));
              
              /*
               * STATE
               * 
               * temp=[age][=][ add ][ ( ][ integer ][ , ]
               * tokens=[ integer ][ ) ]
               * 
               */
              if(tokens.get(0).getType()==TokenType.Integer || tokens.get(0).getType()==TokenType.UserDefined)
              {
                temp.add(tokens.remove(0));
                /*
                 * STATE
                 * 
                 * temp=[age][=][ add ][ ( ][ integer ][ , ][ integer ]
                 * tokens=[ ) ]
                 * 
                 */
                
                if(tokens.get(0).getValue().compareTo(")")==0)
                {
                  temp.add(tokens.remove(0));
                  temp.add(tokens.remove(0));
                  /*
                   * STATE
                   * 
                   * temp=[age][=][ add ][ ( ][ integer ][ , ][ integer ][ ) ]
                   * tokens=[... CODE ...]
                   * 
                   */
                  return true;
                }
                /*
                 * STATE
                 * 
                 * temp=[age][=][ add ][ ( ][ integer ][ , ][ integer ]
                 * tokens=[ { ]
                 * 
                 * Point is that we did not find a ")" token so we print an error message
                 * because all signs were pointing toward a number operation but there was an 
                 * syntax error
                 * 
                 */
                else
                {
                  parsingError(tokens.get(0).toString()+"\nExpected \')\'");
                  return false;
                }
              }
              /*
               * STATE
               * 
               * temp=[age][=][ add ][ ( ][ integer ][ , ]
               * tokens=[ eq ][ ) ]
               * 
               * We did not find an integer type token in front of the tokens list as we should have
               * We print an error message because there was a syntax error
               * 
               */ 
              else
              {
                parsingError(tokens.get(0).toString()+"\nExpected integer or user defined name type of token");
                return false;
              }
            }
            /*
             * STATE
             * 
             * temp=[age][=][ add ][ ( ][ integer ]
             * tokens=[ eq ][ integer ][ ) ]
             * 
             * We did not find the "," symbol as we should have in front of the tokens list
             * We print an error message because of the syntax error in the number operation
             * 
             */ 
            else
            {
              parsingError(temp.get(temp.size()-1).toString()+"\nExpected \',\' symbol");
              return false;
            }
          }
          /*
           * STATE
           * 
           * temp=[age][=][ add ][ ( ]
           * tokens=[ tom ][ , ][ integer ][ ) ]
           * 
           * We did not find an integer token in front of the tokens list
           * We print an error message to show  that there was a syntax error with in the number operation
           * 
           */ 
          else
          {
            parsingError(temp.get(temp.size()-1).toString()+"\nExpected integer or user defined type of token");
            return false;
          }
        }
        /*
         * We print nothing here because we never started working with the number operation
         * Most probably this is something else so we leave the tokens list and do not assume we have reached a
         * syntax error
         */ 
      }
      return false;
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
