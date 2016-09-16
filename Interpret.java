import java.math.BigInteger;

public class Interpret {
	enum symbol {nosym, eoln, number, plus, minus, mult, gcd, exp, lcm, inv, equals, lparen, rparen, comma, error};
	private symbol sy = symbol.nosym;
	private char ch;
	private BigInteger value;
	private String input;
	private int idx=0;
	private BigInteger modulus= new BigInteger("1");
	private final BigInteger zero = new BigInteger("0");
	private final BigInteger one = new BigInteger("1");

	public Interpret(BigInteger n){
		modulus = n;
	}

	public String eval(String equation) throws Exception{
		BigInteger result;
		if (equation.length()==0) return "0"; // This is actually handled in ModularCalculator now.
		sy = symbol.nosym;
		idx = 0;
		value = zero;
		input = equation;
		ch = nextch();
		sy = token();
		result = expression();
		if (sy!=symbol.eoln)error("Unexpected input at end of equation");
		while (result.compareTo(zero)<0) result = result.add(modulus);
		return result.toString();
	}

	public void setModulus(BigInteger n){
		modulus = n;
	}

	private BigInteger expression() throws Exception{
		BigInteger result;

		if (sy==symbol.plus)
		{
			sy = token();
			result = term();
		}
		else
			if (sy==symbol.minus)
			{
				sy = token();
				result = term().negate();
			}
			else
				result = term();
		while (sy==symbol.plus || sy==symbol.minus){
			symbol operator = sy;
			sy = token();
			switch (operator){
			case plus:
				result = result.add(term());
				break;
			case minus:
				result = result.subtract(term());
				break;
			}
			result = result.mod(modulus);
		}
		result = result.mod(modulus);
		return result;
	}

	private BigInteger term() throws Exception{
		BigInteger result = factor();
		while (sy==symbol.mult || sy==symbol.exp){
			symbol operator = sy;
			sy = token();
			BigInteger val = factor();

			switch (operator){
			case mult:
				result = result.multiply(val);
				break;
				//case divd:
				//result = result.divide(val);
				//break;
			case exp:
				if (result.compareTo(zero)==0 && val.compareTo(zero)==0)
					error("0^0 is undefined");
				result = result.modPow(val,modulus);
				break;
			}
			result = result.mod(modulus);
		}
		result = result.mod(modulus);
		return result;
	}

	private BigInteger factor() throws Exception{
		BigInteger val;
		BigInteger arg1, arg2;
		switch (sy){
		case number:
			val = value;
			sy = token();
			return val;
		case lparen:
			sy = token();
			val = expression();
			if (sy!=symbol.rparen) error("Parentheses mismatch");
			sy = token();
			return val;
		case gcd:
			sy = token();
			if (sy!=symbol.lparen) error("Left parenthesis expected");
			sy = token();
			arg1 = expression();
			if (sy!=symbol.comma) error("Comma expected");
			sy = token();
			arg2 = expression();
			if (sy!=symbol.rparen) error("Right parenthesis expected");
			sy = token();
			val = arg1.gcd(arg2);  
			return val;
		case lcm:
			sy = token();
			if (sy!=symbol.lparen) error("Left parenthesis expected");
			sy = token();
			arg1 = expression();
			if (sy!=symbol.comma) error("Comma expected");
			sy = token();
			arg2 = expression();
			if (sy!=symbol.rparen) error("Right parenthesis expected");
			sy = token();
			val = arg1.multiply(arg2).divide(arg1.gcd(arg2));  // LCM(a,b) = ab/GCD(a,b)
			return val;
		case inv:
			sy = token();
			if (sy!=symbol.lparen) error("Left parenthesis expected");
			sy = token();
			arg1 = expression();
			//if (sy!=symbol.comma) error("Comma expected");
			//sy = token();
			//arg2 = expression();
			if (sy!=symbol.rparen) error("Right parenthesis expected");
			sy = token();
			if (arg1.gcd(modulus).equals(one))
				val = arg1.modInverse(modulus);
			else{
				error(arg1+" is not invertible mod "+modulus);
				return zero;
			}
			return val;
		default:
			error("Syntax error - unexpected input");
		}
		return zero;
	}

	private symbol token() throws Exception{
		symbol sy = symbol.nosym;
		while (ch==' ') ch = nextch();
		switch (ch) {
		case (char)0:
			return symbol.eoln;
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			String input="0";
			sy = symbol.number;
			while (ch >= '0' && ch <= '9'){
				input+=ch;
				ch = nextch();
			}
			value = new BigInteger(input);
			break;
		case '+':
			sy = symbol.plus;
			ch = nextch();
			break;
		case '-':
			sy = symbol.minus;
			ch = nextch();
			break;
		case '*':
			sy = symbol.mult;
			ch = nextch();
			break;
			/*
		case '/':
			sy = symbol.divd;
			ch = nextch();
			break;
			 */
		case '(':
			sy = symbol.lparen;
			ch = nextch();
			break;
		case ')':
			sy = symbol.rparen;
			ch = nextch();
			break;
		case '^':
			sy = symbol.exp;
			ch = nextch();
			break;
		case ',':
			sy = symbol.comma;
			ch = nextch();
			break;
		case 'g':
		case 'G':
			ch = nextch();
			if (ch!='c' && ch!='C') error("Unknown name");
			ch = nextch();
			if (ch!='d' && ch!='D') error("Unknown name");
			ch = nextch();
			sy = symbol.gcd;
			break;
		case 'l':
		case 'L':
			ch = nextch();
			if (ch!='c' && ch!='C') error("Unknown name");
			ch = nextch();
			if (ch!='m' && ch!='M') error("Unknown name");
			ch = nextch();
			sy = symbol.lcm;
			break;
		case 'i':
		case 'I':
			ch = nextch();
			if (ch!='n' && ch!='N') error("Unknown name");
			ch = nextch();
			if (ch!='v' && ch!='V') error("Unknown name");
			ch = nextch();
			sy = symbol.inv;
			break;
		default:
			error("Illegal character");
		}
		return sy;
	}

	private char nextch(){
		if (idx>=input.length())
			return (char)0;
		else
			return input.charAt(idx++);
	}

	private void error(String errorMessage) throws Exception{
		String errorReport=input+"\n";
		for (int i=1; i<idx; i++) errorReport+=' ';
		errorReport+="^\n";
		errorReport+=errorMessage+"\n";
		throw new Exception(errorReport);
	}

}
