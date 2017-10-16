package cn.dataguru.week8;

public class HomeWork {

	public static void main(String[] args) {
		Calculator cal = new Calculator();
	    String express = "(3.5*5+(20-12)-3)/7";
	    cal.calculate(express);
	    printExpressEval(express);

	    express = "( ( ( 15 / 3 ) + ( 1.3 * 2 ) + ( 20 - 12 ) ) - 3 + 5 )";
	    cal.calculate(express);
	    printExpressEval(express);
	    
	    express = "-2.2+((3+4)*2-22)/2*3";
	    cal.calculate(express);
	    printExpressEval(express);
	}
	
	private static void printExpressEval(String express){
		System.out.println(express+" = "+new Expression(express).eval().toPlainString()+"\n");
	}
	
}
