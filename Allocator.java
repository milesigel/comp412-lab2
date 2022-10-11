import intermediate_representation.IRList;

public class Allocator {

    private IRList renamedCode;
    private int numRegisters;
    private int numVirtualRegisters;
    private int[] VRtoPR;
    private int[] PRtoVR;
    private int[] VRtoSpillLoc;
    private int[] PRtoNU;


    public Allocator(int numRegisters, IRList renamedCode, int vrName) {
        this.numRegisters = numRegisters;
        this.renamedCode = renamedCode;
        this.PRtoVR = new int[numRegisters];

    }





}
