import intermediate_representation.IRList;
import intermediate_representation.IRNode;

public class Renamer {

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
        this.LU = new int[this.maxSR];

    }

    public void addVirtualRegisters() {
        IRNode workingNode = representation.getTail();
        IRNode head = representation.getHead();
        while (workingNode != head) {


        }
    }
}
