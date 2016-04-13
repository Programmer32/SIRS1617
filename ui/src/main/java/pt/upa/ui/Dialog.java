package pt.upa.ui;

import java.math.BigInteger;
import java.util.Scanner;

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
	private static Scanner _input;
	private static Dialog _instance;
	private Dialog(){}
	
	public static Dialog IO(){
		if(Dialog._instance == null){
			Dialog._debug = true;
			Dialog._instance = new Dialog();
			Dialog._input = new Scanner(System.in);
		}
		return Dialog._instance;
	}
	
	public void black(){ if(Dialog._debug) System.out.println(ANSI_BLACK); }
	public void red(){ if(Dialog._debug) System.out.print(ANSI_RED); }
	public void reset(){ if(Dialog._debug) System.out.print(ANSI_RESET); }
	public void green(){ if(Dialog._debug) System.out.print(ANSI_GREEN); }
	public void yellow(){ if(Dialog._debug) System.out.print(ANSI_YELLOW); }
	public void blue(){ if(Dialog._debug) System.out.print(ANSI_BLUE); }
	public void magent(){ if(Dialog._debug) System.out.print(ANSI_PURPLE); }
	public void cyan(){ if(Dialog._debug) System.out.print(ANSI_CYAN); }
	public void white(){ if(Dialog._debug) System.out.print(ANSI_WHITE); }
	
	public void debug(String method, String message){
		if(!Dialog._debug) return;
		int length = 20;
		print("[ ");
		for(int i = 0; i < length - method.length(); i+=2) print(" ");
		green();
		print(method);
		reset();
		int i = 0;
		for(i = 0; i < length - method.length() - 1; i+=2) print(" ");
		if(i*2 != length - method.length()) print(" ");
		print(" ] " + message);
		println("");
		
	}
	public void debug(String s){
		if(Dialog._debug) System.out.println(s);
	}


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
	
	
	public void print(String s){ System.out.print(s); }
	public void println(String s){ print(s); System.out.println(); }
}
