import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.lang.*;

public class Read {
	static Registers registers;
	static String instruction;
	static int mainOpcode;
	static String mainR1;
	static String mainR2;
	static String mainImmediate;
	static int register1Value;
	static int register2Value;
	static String mainRegister1;
	static String mainRegister2;
	static int noOfInstructions;

	public Read() {
		registers = new Registers();
	}

	public static ArrayList<String> readFile(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		ArrayList<String> a = new ArrayList<String>();
		while (line != null) {
			a.add(line);
			line = br.readLine();
		}
		br.close();
		noOfInstructions = a.size();
		return a;
	}

	// Converts Decimal to Binary.
	public String getImmediate(String register) {
		int value = Integer.parseInt(register);
		String bin = "";
		if (value >= 0) {
			bin = Integer.toBinaryString(value);
			int x = bin.length();
			if (x != 6) {
				for (int i = 0; i < 6 - x; i++) {
					bin = "0" + bin;
				}
			}
		} else {
			bin = Integer.toBinaryString(value).substring(26, 32);
		}
		return bin;
	}

	// Gets binary reps of "ex: R4"
	public String getRegister(String register) {
		String registerNumber = "";
		for (int i = 1; i < register.length(); i++) {
			registerNumber = registerNumber + register.charAt(i);
		}
		String bin = Integer.toBinaryString(Integer.parseInt(registerNumber));
		int x = bin.length();
		if (x != 6) {
			for (int i = 0; i < 6 - x; i++) {
				bin = "0" + bin;
			}
		}
		return bin;
	}

	public String getOpCode(String opCode) {
		switch (opCode) {
		case "ADD":
			opCode = "0000";
			break;
		case "SUB":
			opCode = "0001";
			break;
		case "MUL":
			opCode = "0010";
			break;
		case "MOVI":
			opCode = "0011";
			break;
		case "BEQZ":
			opCode = "0100";
			break;
		case "ANDI":
			opCode = "0101";
			break;
		case "EOR":
			opCode = "0110";
			break;
		case "BR":
			opCode = "0111";
			break;
		case "SAL":
			opCode = "1000";
			break;
		case "SAR":
			opCode = "1001";
			break;
		case "LDR":
			opCode = "1010";
			break;
		case "STR":
			opCode = "1011";
			break;
		}
		return opCode;
	}

	public void readInstructions(ArrayList<String> data) {
		String opcode = "";
		String register1 = "";
		String register2 = "";
		for (int i = 0; i < data.size(); i++) {
			String instruction = data.get(i);
			String[] content = instruction.split(" ");
			opcode = getOpCode(content[0]);
			register1 = getRegister(content[1]);
			if (isInteger(content[2]) == true) {
				register2 = getImmediate(content[2]);
			} else if (isInteger(content[2]) == false) {
				register2 = getRegister(content[2]);
			}
			String instructionBinary = opcode + register1 + register2;
			insertInstruction(instructionBinary);
		}
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public void insertInstruction(String instruction) {
		registers.instructionMemory[registers.ir] = instruction;
		registers.ir++;
	}
	public static String getValue(String value) {
		String realValue = "";
		for (int i = 2; i < value.length(); i++) {
			realValue = realValue + value.charAt(i);
		}
		return realValue;
	}

	public static String adjustAdd(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int result = r1 + r2;
		String resstr = Integer.toBinaryString(result);
		int len = resstr.length();
		if (result >= 0)
			for (int i = 0; i < 8 - len; i++)
				resstr = "0" + resstr;
		else if (result < 0)
			resstr = resstr.substring(24, 32);
		return resstr;
	}

	public static String adjustSub(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int result = r1 - r2;
		String resstr = Integer.toBinaryString(result);
		int len = resstr.length();
		if (result >= 0)
			for (int i = 0; i < 8 - len; i++)
				resstr = "0" + resstr;
		else if (result < 0)
			resstr = resstr.substring(24, 32);
		return resstr;
	}

	public static String adjustMul(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int result = r1 * r2;
		String resstr = Integer.toBinaryString(result);
		int len = resstr.length();
		if (result >= 0)
			if (len <= 8) {
				for (int i = 0; i < 8 - len; i++)
					resstr = "0" + resstr;
			} else {
				resstr = resstr.substring(len - 8);
			}
		else if (result < 0)
			resstr = resstr.substring(24, 32);
		return resstr;
	}

	public static String adjustMovI(String immediate) {
		String str = "";
		if (immediate.charAt(0) == '0') {
			str = "00" + immediate;
		} else {
			str = "11" + immediate;
		}
		return str;
	}

	public static String adjustAndI(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int result = r1 & r2;
		String resstr = Integer.toBinaryString(result);
		int len = resstr.length();
		if (result >= 0)
			for (int i = 0; i < 8 - len; i++)
				resstr = "0" + resstr;
		else if (result < 0)
			resstr = resstr.substring(24, 32);
		return resstr;
	}

	public static String adjustEor(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int result = r1 ^ r2;
		String resstr = Integer.toBinaryString(result);
		int len = resstr.length();
		if (result >= 0)
			for (int i = 0; i < 8 - len; i++)
				resstr = "0" + resstr;
		else if (result < 0)
			resstr = resstr.substring(24, 32);
		return resstr;
	}

	public static String adjustSAl(String register1, String register2) {
		int r1 = Integer.parseInt(register1, 2);
		int r2 = Integer.parseInt(register2, 2);
		int result = r1 << r2;
		String resstr = Integer.toBinaryString(result);
		int len = resstr.length();
		if (len > 6)
			resstr = resstr.substring(len - 6);
		if (len < 6)
			resstr = "00000" + resstr;
		if (resstr.charAt(0) == '0') {
			resstr = "00" + resstr;
		} else {
			resstr = "11" + resstr;
		}
		return resstr;
	}

	public static String adjustSAR(String register1, String register2, boolean negative0) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int result = r1 >> r2;
		String resstr = Integer.toBinaryString(result);
		int len = resstr.length();
		if (result < 0) {
			resstr = resstr.substring(24, 32);
		} else {
			for (int i = 0; i < 8 - len; i++)
				resstr = "0" + resstr;
		}
		return resstr;
	}

	public static void adjustBEQZ(String mainRegister1, String mainRegister2, boolean negative0) {
		if (Integer.parseInt(mainRegister1, 2) == 0) {
			if (negative0 == true) {
				mainRegister2 = "1111111111" + mainRegister2;
			}
			short r2 = (short) Integer.parseInt(mainRegister2, 2);
			if ((registers.pc + r2) >= 0) {
				registers.pc = (short) (registers.pc + r2);
			}
		}
	}

	public static void adjustBr(String mainRegister1, String mainRegister2, boolean negative0, boolean negative1) {
		String register0 = mainRegister1;
		String register1 = mainRegister2;
		if (negative0 == true) {
			register0 = "1111111111" + mainRegister1;
		}

		if (negative1 == true) {
			register1 = "1111111111" + mainRegister2;
		}
		short r1 = (short) Integer.parseInt(register0, 2);
		short r2 = (short) Integer.parseInt(register1, 2);

		short res = (short) (r1 | r2);
		if (res >= 0) {
			registers.pc = res;
		}

	}

	public static char checkCarry(String resstr) {
		int len = resstr.length();
		char carry;
		if (len <= 6)
			carry = '0';
		else
			carry = '1';
		return carry;
	}

	public static char checkAddOverFlow(char carry, String resstr, String register1, String register2) {
		char overFlow = '0';
		if (resstr.length() > 6) {
			overFlow = Character.forDigit(Character.getNumericValue(carry) ^ Character.getNumericValue(resstr.charAt(1))
					^ Character.getNumericValue(register1.charAt(0)) ^ Character.getNumericValue(register2.charAt(0)),
					10);
		} else {
			overFlow = Character.forDigit(Character.getNumericValue(carry) ^ Character.getNumericValue(resstr.charAt(1))
					^ Character.getNumericValue(register1.charAt(0)) ^ Character.getNumericValue(register2.charAt(0)),
					10);
		}
		return overFlow;
	}

	public static char checkSubOverFlow(String resstr, String register1, String register2) {
		char overFlow = '0';
		overFlow = (char) (0 ^ (int) resstr.charAt(0) ^ (int) register1.charAt(0) ^ (int) register2.charAt(0));
		return overFlow;
	}

	public static char checkNegative(int result) {
		char negative = '0';
		if (result < 0)
			negative = '1';
		return negative;
	}

	public static char checkSign(char negative, char overFlow) {
		char sign = Character.forDigit(Character.getNumericValue(negative) ^ Character.getNumericValue(overFlow), 10);
		return sign;
	}

	public static char checkZero(int result) {
		char zero;
		if (result == 0)
			zero = '1';
		else
			zero = '0';
		return zero;
	}

	public static void adjustAddFlags(String register1, String register2, boolean negative0, boolean negative1) {
		int result = Integer.parseInt(register1, 2) + Integer.parseInt(register2, 2);
		String resstr = Integer.toBinaryString(result);
		char carry = checkCarry(resstr);
		char overFlow = checkAddOverFlow(carry, resstr, register1, register2);
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int res = r1 + r2;
		char negative = checkNegative(res);
		char sign = checkSign(negative, overFlow);
		char zero = checkZero(res);
		registers.registers.put("SREG", "000" + carry + overFlow + negative + sign + zero);
	}

	public static void adjustSubFlags(String register1, String register2, boolean negative0, boolean negative1) {
		int result = Integer.parseInt(register1, 2) - Integer.parseInt(register2, 2);
		String resstr = Integer.toBinaryString(result);
		char overFlow = checkSubOverFlow(resstr, register1, register2);
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int res = r1 - r2;
		char negative = checkNegative(res);
		char sign = checkSign(negative, overFlow);
		char zero = checkZero(res);
		registers.registers.put("SREG",
				"000" + registers.registers.get("SREG").charAt(3) + overFlow + negative + sign + zero);
	}

	public static void adjustMulFlags(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int res = r1 * r2;
		char negative = checkNegative(res);
		char zero = checkZero(res);
		registers.registers.put("SREG",
				"000" + registers.registers.get("SREG").charAt(3) + registers.registers.get("SREG").charAt(4) + negative
						+ registers.registers.get("SREG").charAt(6) + zero);
	}

	public static void adjustMovIFlags(String immediate, boolean negative0) {
		if (negative0 == true) {
			immediate = "1111111111" + immediate;
		}
		int r1 = (short) Integer.parseInt(immediate, 2);
		char negative = checkNegative(r1);
		char zero = checkZero(r1);
		registers.registers.put("SREG",
				"000" + registers.registers.get("SREG").charAt(3) + registers.registers.get("SREG").charAt(4) + negative
						+ registers.registers.get("SREG").charAt(6) + zero);
	}

	public static void adjustAndIFlags(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int res = r1 & r2;
		char negative = checkNegative(res);
		char zero = checkZero(res);
		registers.registers.put("SREG",
				"000" + registers.registers.get("SREG").charAt(3) + registers.registers.get("SREG").charAt(4) + negative
						+ registers.registers.get("SREG").charAt(6) + zero);
	}

	public static void adjustEorFlags(String register1, String register2, boolean negative0, boolean negative1) {
		if (negative0 == true) {
			register1 = "1111111111" + register1;
		}
		if (negative1 == true) {
			register2 = "1111111111" + register2;
		}
		int r1 = (short) Integer.parseInt(register1, 2);
		int r2 = (short) Integer.parseInt(register2, 2);
		int res = r1 ^ r2;
		char negative = checkNegative(res);
		char zero = checkZero(res);
		registers.registers.put("SREG",
				"000" + registers.registers.get("SREG").charAt(3) + registers.registers.get("SREG").charAt(4) + negative
						+ registers.registers.get("SREG").charAt(6) + zero);
	}

	public static void adjustSalFlags(String register1, String register2) {
		String resstri = adjustSAl(register1, register2);
		char negative = '0';
		if (resstri.charAt(0) == '1') {
			negative = '1';
		}
		int res = Integer.parseInt(resstri);
		char zero = checkZero(res);
		registers.registers.put("SREG",
				"000" + registers.registers.get("SREG").charAt(3) + registers.registers.get("SREG").charAt(4) + negative
						+ registers.registers.get("SREG").charAt(6) + zero);

	}

	public static void adjustSarFlags(String register1, String register2) {
		boolean negative0 = checkNegative(registers.registers.get("R" + register1Value));
		String resstri = adjustSAR(register1, register2, negative0);
		char negative = '0';
		if (resstri.charAt(0) == '1') {
			negative = '1';
		}
		int res = Integer.parseInt(resstri);
		char zero = checkZero(res);
		registers.registers.put("SREG",
				"000" + registers.registers.get("SREG").charAt(3) + registers.registers.get("SREG").charAt(4) + negative
						+ registers.registers.get("SREG").charAt(6) + zero);

	}

	public static boolean checkNegative(String s) {
		if (s.charAt(0) == '1')
			return true;
		else
			return false;
	}

	public static String fetch() {
		instruction = registers.instructionMemory[registers.pc];
		registers.pc++;
		return instruction;
	}

	public static void decode() {
		mainOpcode = Integer.parseInt(instruction.substring(0, 4), 2);
		mainR1 = instruction.substring(4, 10);
		mainR2 = instruction.substring(10, 16);
		mainImmediate = instruction.substring(10, 16);
		register1Value = Integer.parseInt(mainR1, 2);
		register2Value = Integer.parseInt(mainR2, 2);
		mainRegister1 = "";
		mainRegister2 = "";
	}

	public static void execute() {
		String resstr = "";
		boolean negative0 = false;
		boolean negative1 = false;
		switch (mainOpcode) {
		case 0:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			negative0 = checkNegative(registers.registers.get("R" + register1Value));
			mainRegister2 = getValue(registers.registers.get("R" + register2Value));
			negative1 = checkNegative(registers.registers.get("R" + register2Value));
			resstr = adjustAdd(mainRegister1, mainRegister2, negative0, negative1);
			adjustAddFlags(mainRegister1, mainRegister2, negative0, negative1);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 1:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			negative0 = checkNegative(registers.registers.get("R" + register1Value));
			mainRegister2 = getValue(registers.registers.get("R" + register2Value));
			negative1 = checkNegative(registers.registers.get("R" + register2Value));
			resstr = adjustSub(mainRegister1, mainRegister2, negative0, negative1);
			adjustSubFlags(mainRegister1, mainRegister2, negative0, negative1);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 2:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			negative0 = checkNegative(registers.registers.get("R" + register1Value));
			mainRegister2 = getValue(registers.registers.get("R" + register2Value));
			negative1 = checkNegative(registers.registers.get("R" + register2Value));
			resstr = adjustMul(mainRegister1, mainRegister2, negative0, negative1);
			adjustMulFlags(mainRegister1, mainRegister2, negative0, negative1);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 3:
			resstr = adjustMovI(mainImmediate);
			negative0 = checkNegative(mainImmediate);
			adjustMovIFlags(mainImmediate, negative0);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 4:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			mainRegister2 = mainImmediate;
			negative0 = checkNegative(mainImmediate);
			adjustBEQZ(mainRegister1, mainRegister2, negative0);
			break;

		case 5:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			negative0 = checkNegative(registers.registers.get("R" + register1Value));
			mainRegister2 = mainImmediate;
			negative1 = checkNegative(mainImmediate);
			resstr = adjustAndI(mainRegister1, mainRegister2, negative0, negative1);
			adjustAndIFlags(mainRegister1, mainRegister2, negative0, negative1);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 6:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			negative0 = checkNegative(registers.registers.get("R" + register1Value));
			mainRegister2 = getValue(registers.registers.get("R" + register2Value));
			negative1 = checkNegative(registers.registers.get("R" + register2Value));
			resstr = adjustEor(mainRegister1, mainRegister2, negative0, negative1);
			adjustEorFlags(mainRegister1, mainRegister2, negative0, negative1);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 7:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			negative0 = checkNegative(registers.registers.get("R" + register1Value));
			mainRegister2 = getValue(registers.registers.get("R" + register2Value));
			negative1 = checkNegative(registers.registers.get("R" + register2Value));
			adjustBr(mainRegister1, mainRegister2, negative0, negative1);
			break;

		case 8:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			mainRegister2 = mainImmediate;
			resstr = adjustSAl(mainRegister1, mainRegister2);
			adjustSalFlags(mainRegister1, mainRegister2);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 9:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			negative0 = checkNegative(registers.registers.get("R" + register1Value));
			mainRegister2 = mainImmediate;
			resstr = adjustSAR(mainRegister1, mainRegister2, negative0);
			adjustSarFlags(mainRegister1, mainRegister2);
			registers.registers.put("R" + register1Value, resstr);

			break;
		case 10:
			mainRegister1 = getValue(registers.registers.get("R" + register1Value));
			mainRegister2 = mainImmediate;
			registers.registers.put("R" + register1Value, registers.dataMemory[Integer.parseInt(mainRegister2, 2)]);
			break;
		case 11:
			mainRegister1 = registers.registers.get("R" + register1Value);
			mainRegister2 = mainImmediate;
			registers.dataMemory[Integer.parseInt(mainRegister2, 2)] = mainRegister1;
			break;
		}
	}

	public static void run() {
		int clockCycles = 1;
		int maxCylces = 3 + ((noOfInstructions - 1) * 1);
		LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
		for (int i = 0; i <= 63; i++) {
			temp.put("R" + i, registers.registers.get("R" + i));
		}
		while (clockCycles <= maxCylces) {
			if (clockCycles == 1) {
				fetch();
			} else if (clockCycles == 2) {
				decode();
				fetch();
			} else if (clockCycles == maxCylces - 1) {
				execute();
				decode();
			} else if (clockCycles == maxCylces) {
				execute();
			} else {
				execute();
				decode();
				fetch();
			}
			System.out.println("*******************************");
			System.out.println("Clock Cycle = " + clockCycles);
			System.out.println("Status : " + registers.registers.get("SREG"));
			System.out.println("Fetched Instruction : " + instruction);
			System.out.println("Main Opcode : " + mainOpcode);
			System.out.println("Main R1 : " + mainR1);
			System.out.println("Main R2 : " + mainR2);
			System.out.println("******* Changes *******");
			for (int i = 0; i <= 63; i++) {
				if (registers.registers.get("R" + i) != temp.get("R" + i)) {
					System.out.println(temp.get("R" + i));
					System.out.println("R" + i + "  ->  " + registers.registers.get("R" + i));
				}
			}
			System.out.println("*******************************");

			clockCycles++;
		}
		System.out.println("****** All Registers Content ******");
		for (int i = 0; i <= 63; i++) {
			System.out.println("R" + i + "  ->  " + registers.registers.get("R" + i));
		}
		System.out.println("****** All Memory Content ******");
		for (int i = 0; i < registers.instructionMemory.length; i++) {
			System.out.println("Memory Index " + i + " -> " + registers.instructionMemory[i]);
		}
		System.out.println("****** All Data Content ******");
		for (int i = 0; i < registers.dataMemory.length; i++) {
			System.out.println("Data Index " + i + " -> " + registers.dataMemory[i]);
		}
	}

	public static void main(String[] args) throws IOException {
		Read a = new Read();
		a.readInstructions(a.readFile("Program_1.txt"));
		run();
	}
}
