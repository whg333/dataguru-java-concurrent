package cn.dataguru.week8;

public class CalculatorUtil {

	//自己实现的功能有限的解析算术表达式
	private static final Calculator calculator = new Calculator();
	
	//是否使用第三方的功能较强大的eval解析算术表达式
	private static final boolean useEval = true;
	
	public static String calculate(String express){
		if(useEval){
			return calculateByEval(express);
		}else{
			return calculateByCalculator(express);
		}
	}
	
	private static String calculateByEval(String express){
		try {
			return new Expression(express).eval().toEngineeringString();
		} catch (Exception e) {
			System.err.println("Error express: "+express+ "\r\n");
			return "Error express: "+express;
		}
	}
	
	private static String calculateByCalculator(String express){
		try {
			return calculator.calculate(express) + "";
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage() + "\r\n");
			return "Error express: "+express;
		}
	}
	
}
