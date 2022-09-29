import intermediate_representation.IRList;
import intermediate_representation.IRNode;

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


    /**
     * Default constructor which takes in an IR that needs to be renamed with virtual registers.
     *
     * @param representation the intermediate representation passed from the parser
     * @param maxSR the maximum source register in the input program
     */
    public Renamer(IRList representation, int maxSR) {
        this.representation = representation;
        this.maxSR = maxSR;
        this.SRtoVR = new int[this.maxSR];
        this.LU = new int[this.maxSR + 1];
        for (int i = 0; i < maxSR + 1; i++) {
            this.SRtoVR[i] = -1;
            this.LU[i] = -1;
        }
    }

    public void addVirtualRegisters() {
        IRNode workingNode = representation.getTail().getPrev();
        IRNode head = representation.getHead();
        int source;
        int opCode;
        while (workingNode != head) {
            opCode = workingNode.getOpCode();
            switch (opCode) {
                case 0 : {

                    break;
                }
                default : {

                }
            }
        }
    }
}
