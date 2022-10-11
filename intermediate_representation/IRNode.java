package intermediate_representation;


import pairs.Pair;

import java.util.Arrays;

public class IRNode {

    private IRNode next;
    private IRNode prev;
    private int lineNum;
    private int opCode;
    private String lexeme;
    private int operandArray[];
    public IRNode (int lineNum, int opCode, String lexeme) {
        this.lineNum = lineNum;
        this.opCode = opCode;
        this.operandArray = new int[12];
        this.lexeme = lexeme;
        Arrays.setAll(operandArray, i -> -1);

    }

    public IRNode getPrev() {
        return prev;
    }
    public IRNode getNext() {
        return next;
    }

    public void setNext(IRNode n) {
        next = n;
    }

    public void setPrev(IRNode p) {
        prev = p;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public int[] getOperandArray() {
        return operandArray;
    }

    public int getOperand(int position) {
        return operandArray[position];
    }

    public void setSourceRegister(int operandNum, int value) {
        this.operandArray[operandNum * 4] = value;
    }

    // 0 indexed
    public int getSourceRegister(int operandNum) {
        return this.operandArray[operandNum * 4];
    }

    public void setVirtualRegister(int operandNum, int value) {
        this.operandArray[(operandNum * 4) + 1] = value;
    }

    public void setNextUseRegister(int operandNum, int value) {
        this.operandArray[(operandNum * 4) + 3] = value;
    }
    public void setOperandArray(int position, int value) {
        this.operandArray[position] = value;
    }

    private int getVirtualRegister(int operandNum) {
        return this.operandArray[(operandNum * 4) + 1];
    }

    @Override
    public String toString() {
        return "[" + lineNum + " ," + IRList.tokenConversion[opCode] + " " + genterateOperandStringArray() + "]";
    }

    public String getILOCRepresentation() {
        if (this.opCode == 2) {
            return this.lexeme + "  r" + this.getVirtualRegister(0) + ", r"
                    + this.getVirtualRegister(1)
                    + " => r" + this.getVirtualRegister(2);
        } else if (this.opCode == 1) {
            return this.lexeme + " " + this.getSourceRegister(0) + " => r" + this.getVirtualRegister(1);
        } else if (this.opCode == 0) {
            return this.lexeme + " r" + this.getVirtualRegister(0) + " => r" + this.getVirtualRegister(1);
        } else if (this.opCode == 3){
            return this.lexeme + " " + this.getSourceRegister(0);
        } else {
            return this.lexeme;
        }
    }

    private String genterateOperandStringArray() {
        String sb = "[";
        for (int i = 0; i < operandArray.length; i++) {
            if (operandArray[i] == -1) {
                sb += "null, ";
            } else {
                sb += operandArray[i] + ", ";
            }
        }
        return sb + "]";
    }

    public String getLexeme() {
        return this.lexeme;
    }
}
