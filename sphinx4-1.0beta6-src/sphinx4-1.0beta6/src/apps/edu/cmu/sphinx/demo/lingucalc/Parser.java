package edu.cmu.sphinx.demo.lingucalc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Parser {

	HashMap<String, Double> vars;
	static boolean isInitialized = false;
	static HashMap<String, Integer> digits;
	static HashMap<String, Integer> teens;
	static HashMap<String, Integer> tens;
	
	public Parser() {
		vars = new HashMap<>();
		vars.put("temp", 0.0);
		vars.put("lastResult", 0.0);
		if (!isInitialized) {
			digits = new HashMap<String, Integer>();
			teens = new HashMap<String, Integer>();
			tens = new HashMap<String, Integer>();
			digits.put("one", 1);
			digits.put("two", 2);
			digits.put("three", 3);
			digits.put("four", 4);
			digits.put("five", 5);
			digits.put("six", 6);
			digits.put("seven", 7);
			digits.put("eight", 8);
			digits.put("nine", 9);
			teens.put("ten", 10);
			teens.put("eleven", 11);
			teens.put("twelve", 12);
			teens.put("thirteen", 13);
			teens.put("fourteen", 14);
			teens.put("fifteen", 15);
			teens.put("sixteen", 16);
			teens.put("seventeen", 17);
			teens.put("eighteen", 18);
			teens.put("nineteen", 19);
			tens.put("twenty", 20);
			tens.put("thirty", 30);
			tens.put("forty", 40);
			tens.put("fifty", 50);
			tens.put("sixty", 60);
			tens.put("seventy", 70);
			tens.put("eighty", 80);
			tens.put("ninety", 90);
		}
	}
	
	public Double parse(String str) {
		if (str.equals("store last result")) {
			vars.put("temp", vars.get("lastResult"));
			return null;
		} else if (str.equals("retrieve last result")) {
			return vars.get("temp");
		} else {
			str = str.replaceAll(" and", "");
			StringTokenizer st = new StringTokenizer(str);
			LinkedList<String> tokenizedExpr = new LinkedList<>();
			while (st.hasMoreTokens()) {
				tokenizedExpr.add(st.nextToken());
			}
			double result = parseExpression(tokenizedExpr);
			vars.put("lastResult", result);
			return result;
		}
	}
	
	public Double parseExpression(LinkedList<String> tokenizedExpr) {
		String s = tokenizedExpr.getFirst();
		if (s.equals("log")) {
			tokenizedExpr.removeFirst(); // remove 'log'
			tokenizedExpr.removeFirst(); // remove 'base'
			double operand1 = parseNumber(tokenizedExpr);
			tokenizedExpr.removeFirst(); // remove 'of'
			return Math.log10(parseExpression(tokenizedExpr))/Math.log10(operand1);
		} else {
			double operand1 = parseNumber(tokenizedExpr);
			if (tokenizedExpr.isEmpty()) {
				return operand1;
			}
			String operator = tokenizedExpr.removeFirst();
			switch (operator) {
			case "plus":
				return operand1 + parseExpression(tokenizedExpr);
			case "minus":
				return operand1 - parseExpression(tokenizedExpr);
			case "multiplied":
				tokenizedExpr.removeFirst(); // remove 'by'
				return operand1 * parseExpression(tokenizedExpr);
			case "times":
				return operand1 * parseExpression(tokenizedExpr);
			case "divided":
				tokenizedExpr.removeFirst(); // remove 'by'
				return operand1 / parseExpression(tokenizedExpr);
			case "power":
				return Math.pow(operand1, parseExpression(tokenizedExpr));
			case "squared":
				return operand1 * operand1;
			case "cubed":
				return operand1 * operand1 * operand1;
			default:
				return 0.0;
			}
		}
	}
	
	public double parseNumber(LinkedList<String> tokenizedExpr) {
		if (tokenizedExpr.isEmpty()) {
			return 0;
		}
		String s = tokenizedExpr.getFirst();
		//System.out.println("S: " + s);
		if (s.equals("zero")) {
			tokenizedExpr.removeFirst();
			return 0;
		} else if (s.equals("pi")) {
			tokenizedExpr.removeFirst();
			return Math.PI;
		} else if (teens.containsKey(s)) {
			tokenizedExpr.removeFirst();
			return teens.get(s);
		} else if (digits.containsKey(s)) {
			tokenizedExpr.removeFirst();
			if (tokenizedExpr.isEmpty()) {
				return digits.get(s);
			} else {
				String s1 = tokenizedExpr.getFirst();
				if (s1.equals("hundred")) {
					tokenizedExpr.removeFirst();
					return digits.get(s) * 100 + parseNumber(tokenizedExpr);
				} else if (s1.equals("thousand")) {
					tokenizedExpr.removeFirst();
					return digits.get(s) * 1000 + parseNumber(tokenizedExpr);
				} else if (digits.containsKey(s1)) {
					double result = digits.get(s);
					do {
						tokenizedExpr.removeFirst();
						result = result * 10 + digits.get(s1);
						if (tokenizedExpr.isEmpty()) {
							break;
						}
						s1 = tokenizedExpr.getFirst();
					} while (digits.containsKey(s1));
					return result;
				} else {
					return digits.get(s);
				}
			}
		} else if (tens.containsKey(s)) {
			tokenizedExpr.removeFirst();
			if (tokenizedExpr.isEmpty()) {
				return tens.get(s);
			} else {
				String s1 = tokenizedExpr.getFirst();
				if (digits.containsKey(s1)) {
					tokenizedExpr.removeFirst();
					return tens.get(s) + digits.get(s1);
				} else {
					return tens.get(s);
				}
			}
		} else {
			System.out.println("Number " + s + "not found");
			return 0;
		}
	}
	
}
