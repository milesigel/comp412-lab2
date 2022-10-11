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

    }

    public void addVirtualRegisters() {
        IRNode head = representation.getHead();
        int opCode;
        while (workingNode != head) {
            handleNode2();
            workingNode = workingNode.getPrev();
        }
    }

    public void handleStore() {

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
                if (SRtoVR[regVal] == -1){
                    SRtoVR[regVal] = vrName;
                    vrName++;
                }
                workingNode.setVirtualRegister(i, SRtoVR[regVal]);
                workingNode.setNextUseRegister(i, LU[regVal]);
                handleDef();
            }
            else if (workingNode.getOpCode() == 2 && i == 2) {
                if (SRtoVR[regVal] == -1){
                    SRtoVR[regVal] = vrName;
                    vrName++;
                }
                workingNode.setVirtualRegister(i, SRtoVR[regVal]);
                workingNode.setNextUseRegister(i, LU[regVal]);
                handleDef();
            }
            else if (workingNode.getOpCode() == 1 && i == 1) {
                if (SRtoVR[regVal] == -1){
                    SRtoVR[regVal] = vrName;
                    vrName++;
                }
                workingNode.setVirtualRegister(i, SRtoVR[regVal]);
                workingNode.setNextUseRegister(i, LU[regVal]);
                handleDef();
            }
        }

        for (int i = 0; i < numOps; i++) {
            regVal = workingNode.getSourceRegister(i);
            if (regVal > maxSR || regVal < 0) {
                continue;
            }
            if (workingNode.getOpCode() == 0) {
                if (workingNode instanceof IRStoreNode) {
                    if (SRtoVR[regVal] == -1){
                        SRtoVR[regVal] = vrName;
                        vrName++;
                    }
                    workingNode.setVirtualRegister(i, SRtoVR[regVal]);
                    workingNode.setNextUseRegister(i, LU[regVal]);
                    handleUse();
                } else if (i == 0) {
                    if (SRtoVR[regVal] == -1) {
                        SRtoVR[regVal] = vrName;
                        vrName++;
                    }
                    workingNode.setVirtualRegister(i, SRtoVR[regVal]);
                    workingNode.setNextUseRegister(i, LU[regVal]);
                    handleUse();
                }

            }
            else if (workingNode.getOpCode() == 2 && i < 2) {
                if (SRtoVR[regVal] == -1){
                    SRtoVR[regVal] = vrName;
                    vrName++;
                }
                workingNode.setVirtualRegister(i, SRtoVR[regVal]);
                workingNode.setNextUseRegister(i, LU[regVal]);
                handleUse();
            }
        }

    }

    private void handleNode() {
        int numOps = 2;
        for (int i = numOps; i >= 0; i--) {
            regVal = workingNode.getSourceRegister(i);
            if (regVal > maxSR || regVal < 0 || (i == 0 && (workingNode.getOpCode() == 3 || workingNode.getOpCode() == 1))) {
                continue;
            }
            if (SRtoVR[regVal] == -1){
                SRtoVR[regVal] = vrName;
                vrName++;
            }
            workingNode.setVirtualRegister(i, SRtoVR[regVal]);
            workingNode.setNextUseRegister(i, LU[regVal]);

            if (workingNode.getOpCode() == 2) {
                // arithop
                if (i == 0) {
                    handleUse();
                } else {
                    handleDef();
                }
            }
            if (workingNode.getOpCode() == 0) {
                if (workingNode instanceof IRStoreNode) {
                    handleUse();
                } else {
                    if (i > 0) {
                        handleDef();
                    } else {
                        handleUse();
                    }
                }
            }
            if (workingNode.getOpCode() == 1) {
                if (i > 0) {
                    handleDef();
                }
            }
        }
    }

    public int getVrName() {
        return this.vrName;
    }


    private void handleUse() {
        LU[this.regVal] = workingNode.getLineNum();
    }

    private void handleDef() {

        SRtoVR[this.regVal] = -1;
        LU[this.regVal] = -1;
    }
}
