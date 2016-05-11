package pt.upa.ui;

import java.math.BigInteger;
import java.util.Scanner;

/**
 * This is used for IO operations
 * No native JAVA IO operations shall be called outside this class 
 * 
 * @author André Dias
 *
 */
public class Dialog {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private static boolean _debug;
	private static boolean _soap;
	private static boolean _trace;
	private static Scanner _input;
	private static Dialog _instance;
	private Dialog(){}
	
	public static Dialog IO(){
		if(Dialog._instance == null){
			String debug = System.getenv("UPA_DEBUG");
			String trace = System.getenv("UPA_TRACE");
			String soap = System.getenv("UPA_SOAP");
			Dialog._debug = false;
			Dialog._trace = false;
			Dialog._soap = false;
			if(debug != null && debug.equals("true")) Dialog._debug = true;
			if(trace != null && trace.equals("true")) Dialog._trace = true;
			if(soap != null && soap.equals("true")) Dialog._soap = true;
			Dialog._instance = new Dialog();
			Dialog._input = new Scanner(System.in);
		}
		return Dialog._instance;
	}
	
	public void black(){ System.out.println(ANSI_BLACK); }
	public void red(){ System.out.print(ANSI_RED); }
	public void reset(){ System.out.print(ANSI_RESET); }
	public void green(){ System.out.print(ANSI_GREEN); }
	public void yellow(){ System.out.print(ANSI_YELLOW); }
	public void blue(){ System.out.print(ANSI_BLUE); }
	public void magent(){ System.out.print(ANSI_PURPLE); }
	public void cyan(){ System.out.print(ANSI_CYAN); }
	public void white(){ System.out.print(ANSI_WHITE); }
	private void printTag(String s){
		int length = 20;
		print("[ ");
		for(int i = 0; i < length - s.length(); i+=2) print(" ");
		green();
		print(s);
		reset();
		int i = 0;
		for(i = 0; i < length - s.length() - 1; i+=2) print(" ");
		if(i*2 != length - s.length()) print(" ");
		print(" ] ");
	}
	public void SOAP(String s){
		if(!Dialog._soap) return;
		println(s);
	}
	public void debug(String method, String message){
		if(!Dialog._debug) return; 
		printTag(method);
		print(message);
		println("");
	}
	public void debug(String s){ if(Dialog._debug) System.out.println(s); }
	public void trace(String r, String s){ if(!Dialog._debug && !Dialog._trace) return; if(Dialog._debug && Dialog._trace) printTag(r); if(Dialog._trace) println(s); }
	public void trace(String s){ if(Dialog._trace) println(s); }

	public int readInteger(){ return Dialog._input.nextInt(); }
	public double readDouble(){ return Dialog._input.nextDouble(); }
	public String readString(){ return Dialog._input.next(); }
	public Float readFloat(){ return Dialog._input.nextFloat(); }
	public Long readLong(){ return Dialog._input.nextLong(); }
	public Short readShort(){ return Dialog._input.nextShort(); }
	public Byte readByte(){ return Dialog._input.nextByte(); }
	public boolean readBoolean(){ return Dialog._input.nextBoolean(); }
	public String readLine(){ return Dialog._input.nextLine(); }
	public BigInteger readBigInteger(){ return Dialog._input.nextBigInteger(); }
	

	public int readInteger(String s){ print(s); return readInteger(); }
	public double readDouble(String s){ print(s); return readDouble(); }
	public String readString(String s){ print(s); return readString(); }
	public Float readFloat(String s){ print(s); return readFloat(); }
	public Long readLong(String s){ print(s); return readLong(); }
	public Short readShort(String s){ print(s); return readShort(); }
	public Byte readByte(String s){print(s); return readByte(); }
	public boolean readBoolean(String s){ print(s); return readBoolean(); }
	public String readLine(String s){ print(s); return readLine(); }
	public BigInteger readBigInteger(String s){ print(s); return readBigInteger(); }
	
	public void clearLine(){
		print(String.format("\033[%dA",1));
		print("\033[2K");
	}
	public void error(String s){ red(); println(s); reset(); }
	public void print(String s){ System.out.print(s); }
	public void println(String s){ print(s); System.out.println(); }
}
