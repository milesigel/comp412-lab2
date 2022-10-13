import intermediate_representation.IRList;
import intermediate_representation.IRNode;
import intermediate_representation.IRStoreNode;

public class Renamer {

    /**
     *         tokenConversion[0] = "MEMOP";
     *         tokenConversion[1] = "LOADI";
     *         tokenConversion[2] = "ARITHOP";
     *         tokenConversion[3] = "OUTPUT";
     *         tokenConversion[4] = "NOP";
     *         tokenConversion[5] = "CONSTANT";
     *         tokenConversion[6] = "REGISTER";
     *         tokenConversion[7] = "COMMA";
     *         tokenConversion[8] = "INTO";
     *         tokenConversion[9] = "EOF";
     *         tokenConversion[10] = "EOL";
     *         tokenConversion[11] = "REG";
     */

    private IRList representation;
    private int vrName = 0;
    private int maxSR;
    private int SRtoVR[];
    private int LU[];
    private int regVal;
    private int maxLive;
    private int currentlyLive;


    private IRNode workingNode;


    /**
     * Default constructor which takes in an IR that needs to be renamed with virtual registers.
     *
     * @param representation the intermediate representation passed from the parser
     * @param maxSR the maximum source register in the input program
     */
    public Renamer(IRList representation, int maxSR) {
        this.representation = representation;
        this.maxSR = maxSR;
        this.workingNode = representation.getTail().getPrev();
        this.SRtoVR = new int[this.maxSR+ 1];
        this.LU = new int[this.maxSR + 1];
        for (int i = 0; i < maxSR + 1; i++) {
            this.SRtoVR[i] = -1;
            this.LU[i] = -1;
        }
        maxLive = 0;
        currentlyLive = 0;

    }

    public void addVirtualRegisters() {
        IRNode head = representation.getHead();
        int opCode;
        while (workingNode != head) {
            handleNode2();
            workingNode = workingNode.getPrev();
        }
    }


    private void handleNode2() {
        int numOps = 3;
//        System.out.println(workingNode);
        for (int i = 0; i < numOps; i++) {
            regVal = workingNode.getSourceRegister(i);
            if (regVal > maxSR || regVal < 0) {
                continue;
            }
            if (workingNode.getOpCode() == 0 && i == 1 && !(workingNode instanceof IRStoreNode)) {
                handleNewDef(i);
            }
            else if (workingNode.getOpCode() == 2 && i == 2) {
                handleNewDef(i);
            }
            else if (workingNode.getOpCode() == 1 && i == 1) {
                handleNewDef(i);
            }
        }

        for (int i = 0; i < numOps; i++) {
            regVal = workingNode.getSourceRegister(i);
            if (regVal > maxSR || regVal < 0) {
                continue;
            }
            if (workingNode.getOpCode() == 0) {
                if (workingNode instanceof IRStoreNode) {
                    handleNewUse(i);
                } else if (i == 0) {
                    handleNewUse(i);
                }
            }
            else if (workingNode.getOpCode() == 2 && i < 2) {
                handleNewUse(i);
            }
        }

    }

    private void handleNewDef(int i) {
        if (SRtoVR[regVal] == -1){
            SRtoVR[regVal] = vrName;
            vrName++;
        } else {
            currentlyLive--;
        }
        workingNode.setVirtualRegister(i, SRtoVR[regVal]);
        workingNode.setNextUseRegister(i, LU[regVal]);
        handleDef();
    }

    private void handleNewUse(int i) {
        if (SRtoVR[regVal] == -1){
            SRtoVR[regVal] = vrName;
            vrName++;
            currentlyLive++;
            if (currentlyLive > maxLive) {
                maxLive = currentlyLive;
            }
        }
        workingNode.setVirtualRegister(i, SRtoVR[regVal]);
        workingNode.setNextUseRegister(i, LU[regVal]);
        handleUse();
    }


    public int getVrName() {
        return this.vrName;
    }

    public int getMaxLive() {
        return this.maxLive;
    }


    private void handleUse() {
        LU[this.regVal] = workingNode.getLineNum();
    }

    private void handleDef() {
        SRtoVR[this.regVal] = -1;
        LU[this.regVal] = -1;
    }
}
