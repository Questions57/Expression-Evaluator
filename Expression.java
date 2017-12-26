
package apps;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    


    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Matches parentheses and square brackets. Populates the openingBracketIndex and
     * closingBracketIndex array lists in such a way that closingBracketIndex[i] is
     * the position of the bracket in the expression that closes an opening bracket
     * at position openingBracketIndex[i]. For example, if the expression is:
     * <pre>
     *    (a+(b-c))*(d+A[4])
     * </pre>
     * then the method would return true, and the array lists would be set to:
     * <pre>
     *    openingBracketIndex: [0 3 10 14]
     *    closingBracketIndex: [8 7 17 16]
     * </pe>
     * 
     * See the FAQ in project description for more details.
     * 
     * @return True if brackets are matched correctly, false if not
     */
   

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, the constructors for ScalarSymbol and ArraySymbol
     * will initialize values to zero and null, respectively.
     * The actual values will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    	StringTokenizer tokenizer = new StringTokenizer(expr, delims, true); 
    	
    	
    	
    	String[] tokens = new String[expr.length()];
    	
    	
    	arrays = new ArrayList<ArraySymbol>();
    	scalars = new ArrayList<ScalarSymbol>();
    	
    	int i = 0;
    	
    	
    	while (tokenizer.hasMoreTokens()){
    		tokens[i] = tokenizer.nextToken();
    		i++;
    	}
    	for (int j = 0; j < tokens.length; j++){
    		if (isArrayVar(j, tokens)){	
    			
    			if (!arrayVariable(tokens[j])) arrays.add(new ArraySymbol(tokens[j])); 							

    		}
    		else if (isScalarVar(j, tokens)) {
    			
    			if (!scalarVariable(tokens[j])) scalars.add(new ScalarSymbol(tokens[j])); 

    		}
    	}
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions. (Note: you can use one or more private helper methods
     * to implement the recursion.)
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    	Stack<Float> operD = new Stack<Float>();
    	
    	
    		Stack<String> operT = new Stack<String>();
    		
    		Expression copy = new Expression(copy(expr));
    		
    		copy.scalars = scalars;
    		copy.arrays = arrays;
    		
    		return eval(copy.expr, operT, operD);
    }
    	

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    	for (ArraySymbol as: arrays) {
    		System.out.println(as);
    	}
    }
    
    private boolean onlyInt(String str){
    	if (str == null) return false;

    	if (str.matches("[0-9]+") || str.contains(".")) return true;

    	else return false;
    	
    }
    
    private boolean scalarVariable(String str){
    	
    	for (ScalarSymbol symbol: scalars){
    		if (symbol.name.equals(str)) return true;
    	}
    	
    	return false;
    }
    
    private boolean arrayVariable(String str){
    	for (ArraySymbol symbol: arrays){
    		if (symbol.name.equals(str)) return true;
    	}
    	
    	
    	return false;
    }
    
    
    private boolean hasOnlyLetters(String name) {
    	if (name == null) return false;
 
        return name.matches("[a-zA-Z]+");
    }
    
    private boolean hasPrecedence(String current, String other){
    	if (other.equals("*") || other.equals("/")) return false;
    	return true;
	
    }

    private boolean isScalarVar(int index, String[] tokens){
    	if (!hasOnlyLetters(tokens[index])){
    		return false;
    	}
    	if (index + 1 < tokens.length - 1){
    		if (tokens[index + 1] != null){
    			if (tokens[index + 1].equals("[")){
    			return false;
    			}	
    		}
    			
    	}
    	return true;
    }
    
   
    
    private float getScalarVal(String token1){
    	
    	for (int i = 0; i < scalars.size(); i++){
    		if (scalars.get(i).name.equals(token1)) return (float)scalars.get(i).value;
    		
    	}
    	return 0;
    	
    }
    
    private boolean isArrayVar(int i, String[] tokens){
    	if (!hasOnlyLetters(tokens[i])){
    		return false;
    	}
    	if (i + 1 < tokens.length - 1 ){
    		if (tokens[i + 1] != null){
    			if (tokens[i + 1].equals("[")) return true;
    		}
    	}
    	return false;
    }
    
    
    private float getArrayVal(String token2, int ind){
    	for (int i = 0; i < arrays.size(); i++){
    		if (arrays.get(i).name.equals(token2)) return (float)arrays.get(i).values[ind];
    		
    	}
    	return 0;
    }
    
    private String copy(String s){
    	String c = "";
		for (int i = 0; i < s.length(); i++){
			c = c + s.charAt(i); }
		
		return c;
    }
    
 private float eval(String e, Stack<String> operT, Stack<Float> operD){
    	
    	StringTokenizer token = new StringTokenizer(e, delims, true);
    	String[] tokens = new String[e.length()];
    	int j = 0;
    	
    	while (token.hasMoreTokens()){ 
    		tokens[j] =token.nextToken();
    		j++;
    	}
    	
    	
    	for (int i = 0; i < tokens.length; i++){
    		if (tokens[i] == null || tokens[i].equals("")) break;
    		if(onlyInt(tokens[i])) operD.push(Float.parseFloat(tokens[i]));
   
    		
    		else if (isScalarVar(i, tokens))
    			operD.push(getScalarVal(tokens[i]));
    		
    		else if (isArrayVar(i, tokens)){
    			String name = tokens[i];
    			
    			Stack<String> openB = new Stack<String>();
    			
    			int closingIndex = 0;
    			for(int k = i + 1; k <= tokens.length; k++){ 
    				if (tokens[k].equals("[")) openB.push("[");

    				
    				if (tokens[k].equals("]")){
    					openB.pop();
    					if (openB.size() == 0){
    						closingIndex = k;
    						break;
    					}
    				}
    				
    				
    			}
    			while (i < closingIndex){
    				i++;
    				
    				
    			}
    			
    			operD.push(getArrayVal(name, (int)eval(e.substring(1 + firstB(e), endB(e)), new Stack<String>(), new Stack<Float>())));
    			
    			
    			e = e.substring(endB(e) + 1, e.length());
    		}
    		else if (tokens[i].equals("(")){
    			
    			Stack<String> openP = new Stack<String>();
    			int closingIndex = 0;
    			for(int k = i; k <= tokens.length; k++){ 
    				
    				if (tokens[k].equals("(")) openP.push("(");
    				
    				if (tokens[k].equals(")")){
    					openP.pop();
    					if (openP.size() == 0){
    						
    						
    						
    						closingIndex = k;
    						
    						break;
    					}
    				}
    			}
    			while (i < closingIndex){
    				i++;
    			}
        		
        		operD.push(eval(e.substring(1 + firstP(e), endP(e)), new Stack<String>(), new Stack<Float>()));
        		
        		
        		e = e.substring(endP(e) + 1, e.length());
    		}
    		
    		else if (tokens[i].equals(" ") || tokens[i].equals("/t")){}
    		
    		
    		else{
    			if (operT.size() == 0) operT.push(tokens[i]);

    			else if (hasPrecedence(tokens[i], operT.peek())) operT.push(tokens[i]);

    			else {
    				
    				if (operT.peek().equals("*")){
    					
    					operD.push((operD.pop()) * (operD.pop()));
    					
    					operT.pop();
    					
    					operT.push(tokens[i]);
    					
    				}
    				
    				else if (operT.peek().equals("/")){
    					operD.push((1 / operD.pop()) * (operD.pop()));  
    					
    					operT.pop();
    					
    					operT.push(tokens[i]);
    				
    				}
    			}
    		}	
    	}
    	while (operT.size() > 0){
    		
			if (operT.peek().equals("*")){
				String tempOperator = operT.pop();
				if (operT.size() >= 1 && operT.peek().equals("/")){
					float tempOperand = operD.pop();
					
					operD.push((1 / operD.pop()) * operD.pop());
					
					operT.pop();
					operT.push(tempOperator);
					
					operD.push(tempOperand);
				}
				else{
					operT.push(tempOperator);
					operD.push(operD.pop() * (operD.pop())); 
					
					operT.pop();
				}
			}
			else if (operT.peek().equals("/")){
				String tempOperator = operT.pop();
				if (operT.size() >= 1 && operT.peek().equals("/")){
					float tempOperand = operD.pop();
					operD.push((1 / operD.pop()) * operD.pop());
					operT.pop();
					operT.push(tempOperator);
					operD.push(tempOperand);
				}
				else{
					operT.push(tempOperator);
					
					operD.push((1 / operD.pop()) * (operD.pop()));  
					operT.pop();
				}
			}
			else if (operT.peek().equals("+")){
				String tempOperator = operT.pop();
				if (operT.size() >= 1 && operT.peek().equals("-")){
					float tempOperand = operD.pop();
					operD.push((0 - operD.pop()) + operD.pop());
					
					operT.pop();
					operT.push(tempOperator);
					
					operD.push(tempOperand);
				}
				else{
					operT.push(tempOperator);
					
					operD.push(operD.pop() + (operD.pop()));  
					operT.pop();
				}
			}
			else {
				operD.push((0 - operD.pop()) + (operD.pop()));  
				operT.pop();
			}
		}
    	
    	return (operD.pop());
    }
    
    private int firstP(String str){
    	int num = 0;
    	for (int x = 0; x < str.length(); x++){
    		
    		if (str.charAt(x) == '('){
    			
    			num = x;
    			
    			break;
    		}
    		
    	}
    	return num;
    }
    
    private int endP(String str){
    	Stack stack = new Stack<>();
    	
    	int n = 0;
    	
    	for (int ind = 0; ind <= str.length(); ind++){
    		
    		if (str.charAt(ind) == '(') stack.push(ind);
    		
    		if (str.charAt(ind) == ')'){
    		      stack.pop();
    			  n = ind;
    			  if (stack.size() == 0)	break;
        		
    		}
    	}
    	return n;
    }
    
    private int firstB(String s){
    	int num = 0;
    	
    	
    	for (int ind = 0; ind < s.length(); ind++){
    		   if (s.charAt(ind) == '['){
    			  num = ind;
    			   break;
    		}
    	}
    	
    	return num;
    }
    
    
    private int endB(String str) {
    	Stack stack = new Stack<>();
    	
    	int num = 0;
    	
    	for (int ind = 0; ind <= str.length(); ind++){

    		if (str.charAt(ind) == '[') stack.push(ind);
    		
    		if (str.charAt(ind) == ']'){
    			stack.pop();
    			num = ind;
    			if (stack.size() == 0) break;
        		
    		}    	}
    	return num;
    }
    
    

}