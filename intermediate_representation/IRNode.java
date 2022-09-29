package intermediate_representation;


public class IRNode {

    private IRNode next;
    private IRNode prev;
    private int lineNum;
    private int opCode;
    private int operandArray[];
    public IRNode (int lineNum, int opCode) {
        this.lineNum = lineNum;
        this.opCode = opCode;
        this.operandArray = new int[12];
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
        this.operandArray[(operandNum - 1) * 4] = value;
    }

    public void setOperandArray(int position, int value) {
        this.operandArray[position] = value;
    }

    @Override
    public String toString() {
        return "[" + lineNum + " ," + IRList.tokenConversion[opCode] + " " + genterateOperandStringArray() + "]";
    }

    private String genterateOperandStringArray() {
        String sb = "[";
        for (int i = 0; i < operandArray.length; i++) {
            sb += operandArray[i] + ", ";
        }
        return sb + "]";
    }
}
