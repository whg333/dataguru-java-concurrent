package cn.dataguru.week8;

import java.util.EmptyStackException;
import java.util.Stack;

public class Calculator {
	
	private static final String SPACE = " ";
	
	private final Stack<String> stack = new Stack<String>();
	
	/** 四则运算 */
	private enum Operator{
		add("+"){
			@Override
			public double calculate(double d1, double d2){
				return d1 + d2;
			}
		},
		sub("-"){
			@Override
			public double calculate(double d1, double d2){
				return d1 - d2;
			}
		},
		mul("*"){
			@Override
			public double calculate(double d1, double d2){
				return d1 * d2;
			}
		},
		div("/"){
			@Override
			public double calculate(double d1, double d2){
				return d1 / d2;
			}
		};
		
		private final String op;
		private Operator(String op){
			this.op = op;
		}
		
		public abstract double calculate(double d1, double d2);
		
		public static Operator getOperator(String op){
			for(Operator operator:values()){
	    		if(operator.op.equals(op)){
	    			return operator;
	    		}
	    	}
			throw new IllegalArgumentException("不可能到达的代码段");
		}
		
		private static boolean isOperator(String op){
	    	for(Operator operator:values()){
	    		if(operator.op.equals(op)){
	    			return true;
	    		}
	    	}
	    	return false;
	    }
	}
	
	/** "执行计算，先将【中缀表达式】转换为【逆波兰表达式】后再计算" */
    public double calculate(String infix_express){
        String suffix_express = transformSuffixExpress(infix_express);
        System.out.println(infix_express + "转换后的逆波兰表达式为：【" + suffix_express + "】");
        for (String s:suffix_express.split(SPACE)){
        	if (isDigit(s) || isFloat(s)){
                stack.push(s);
            }else if (Operator.isOperator(s)){
            	calculateWithOperator(s);
            }else{
            	throw new IllegalArgumentException("【逆波兰表达式】中发现不可识别的字符：【"+s+"】");
            }
        }
        assertTrue(stack.size() == 1, "【逆波兰表达式】不匹配，计算出现错误");
        String result = stack.pop();
        assertTrue(stack.empty(), "【逆波兰表达式】计算出错，计算完毕栈应该为空");
        System.out.println(infix_express + "的计算结果为：" + result + "\n");
        return Double.parseDouble(result);
    }
    
    private void calculateWithOperator(String s){
    	String num2;
        String num1;
		try {
			num2 = stack.pop();
			num1 = stack.pop();
            stack.push(calculateWithOperator(s, num1, num2));
		} catch (EmptyStackException e) {
			throw new IllegalArgumentException("【逆波兰表达式】不匹配，计算出现错误");
		}
    }
        
    private static String calculateWithOperator(String op, String x, String y){
        return Operator.getOperator(op).calculate(Double.parseDouble(x), Double.parseDouble(y))+"";
    }
    
    private static boolean isFloat(String s){
    	try {
			Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
    }
    
    /** 将【中缀表达式】转换为计算机可识别的【逆波兰表达式】 */
    private String transformSuffixExpress(String infixExpress){
        StringBuilder suffixExpress = new StringBuilder();
        int length = infixExpress.length();
        int index = 0;
        
        while (index < length){
            StringBuilder s = new StringBuilder();
            s.append(infixExpress.charAt(index));
            if (isBlank(s.toString())){ // 空白符则直接忽略
                index = index + 1;
                continue;
            }else if (isDigit(s.toString())){ // 数字则尝试继续读取下一个，因为可能是大于9的数字或浮点数
                while (index+1 < length 
                      && (isDigit(infixExpress.charAt(index+1)) 
                           || isDot(infixExpress.charAt(index+1)))){
                    s = s.append(infixExpress.charAt(index+1));
                    index = index + 1;
                }
                suffixExpress.append(s).append(SPACE);
            }else if (isLeftParentheses(s.toString())){
                stack.push(s.toString());           
            }else if (Operator.isOperator(s.toString())){
            	//如果第一个是负号则继续读取看下是否是一个负数
            	if(index == 0 && s.toString().equals(Operator.sub.op)){
            		while(index+1 < length 
                			&& (isDigit(infixExpress.charAt(index+1))
                					|| isDot(infixExpress.charAt(index+1)))){
                		s = s.append(infixExpress.charAt(index+1));
                		index = index + 1;
                	}
            		suffixExpress.append(s).append(SPACE);
            	}else{
            		// 遇到运算符后，栈顶出栈并打印运算符，除非isBreak方法的一种情况发生，才入栈s运算符
                    while (!isBreak(s.toString(), stack)){
                        suffixExpress.append(stack.pop()).append(SPACE);
                    }
                    stack.push(s.toString());
            	}
            	
            }else if (isRightParentheses(s.toString())){             
                while (!isLeftParentheses(stack.peek())){
                    String next_operator = stack.pop();
                    assertTrue(!stack.empty(), "输入的【中缀算术表达式】有误，左右括号不平衡");
                    suffixExpress.append(next_operator).append(SPACE);
                }
                stack.pop(); // 遇到了左括号跳出whlie后，令左括号出栈
            }else{
                throw new IllegalArgumentException("【中缀表达式】中发现不可识别的字符：【"+s+"】"); 
            }
            index = index + 1;
        }
            
        while (!stack.empty()){
            suffixExpress.append(stack.pop()).append(SPACE);
        }
            
        assertTrue(stack.empty(), "【中缀表达式转换后缀表达式】出错，转换完毕栈应该为空");
        suffixExpress.deleteCharAt(suffixExpress.length() - 1);
        return suffixExpress.toString();
    }
    
    private static void assertTrue(boolean result, String exceptionMsg){
    	if(!result){
    		throw new IllegalArgumentException(exceptionMsg);
    	}
    }
    
    public static boolean isBlank(String x) {
		int len;
		if (x == null || (len = x.length()) == 0)
			return true;

		while (len-- > 0) {
			if (!Character.isWhitespace(x.charAt(len)))
				return false;
		}

		return true;
	}
    
    private static boolean isDigit(String s){
    	int len;
		if (s == null || (len = s.length()) == 0)
			return false;
		while (len-- > 0) {
			char temp = s.charAt(len);
			if (!isDigit(temp))
				return false;
		}
		return true;
    }
    
    private static boolean isLeftParentheses(String s){
    	return s.equals("(");
    }
    
    private static boolean isRightParentheses(String s){
    	return s.equals(")");
    }
    
    /**
     * 遇到运算符后，出栈并打印运算符，直到下列3种情况的一中发生：<br/>
     * (1)栈为空；（2）栈顶的符号是左括号；（3）栈顶符号的优先级比s运算符更低
     */
    private static boolean isBreak(String s, Stack<String> stack){
       return stack.empty()
              || isLeftParentheses(stack.peek()) 
              || comparePriority(stack.peek(), s) < 0;
    }
    
    /** 加减乘除四则运算的对比优先级，0代表相等，1代表op1优先级大于op2，-1代表op1优先级小于op2 */
    private static int comparePriority(String op1, String op2){
        if (isPlusOrMinus(op1) && isPlusOrMinus(op2))
            return 0;
        if (isMultiplyOrDivide(op1) && isMultiplyOrDivide(op2))
            return 0;
        if (isPlusOrMinus(op1) && isMultiplyOrDivide(op2))
            return -1;
        if (isMultiplyOrDivide(op1)&& isPlusOrMinus(op2))
            return 1;
        throw new IllegalArgumentException("不可能到达的代码段");
    }
    
    private static boolean isPlusOrMinus(String op){
        return op.equals(Operator.add.op) || op.equals(Operator.sub.op);
    }

    private static boolean isMultiplyOrDivide(String op){
        return op.equals(Operator.mul.op) || op.equals(Operator.div.op);
    }
    
    private static boolean isDigit(char c){
    	return c <= 57 && c >= 48;
    }
    
    private static boolean isDot(char c){
    	return c == '.';
    }
	
}
