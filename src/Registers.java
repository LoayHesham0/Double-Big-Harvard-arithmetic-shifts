import java.util.Iterator;
import java.util.LinkedHashMap;

public class Registers {
	 String[] instructionMemory;
	 String[] dataMemory;
	 int ir;
	 int dr;
	 LinkedHashMap<String, String> registers;
	 short pc;

	public Registers() {
		instructionMemory = new String[1024];
		dataMemory = new String[2048];
		registers = new LinkedHashMap<String,String>();
		ir = 0;
		dr = 0;
		String emptyRegisterData = "00000000";
		for (int i = 0; i <= 63; i++) {
			registers.put("R" + i, emptyRegisterData);
		}
		pc = 0;
		registers.put("SREG",emptyRegisterData);
	}
	
	public static void main(String [] args) {
		short pc=0;
		boolean negative0=true;
		boolean negative1=true;
		String register0="100100";
		String register1="111111";
		if(negative0==true) {
			register0 = "1111111111"+register0;
		}

		if(negative1==true) {
			register1 = "1111111111"+register1;
		}
		short r1 = (short)Integer.parseInt(register0,2);
		short r2 = (short)Integer.parseInt(register1,2);
		
		short res = (short)(r1|r2);
		if (res>=0) {
			pc=res;
		}
		System.out.println(pc);
	}
	
	
}
