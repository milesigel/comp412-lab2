package intermediate_representation;

public class IRStoreNode extends IRNode {
    public IRStoreNode(int lineNum, int opCode) {
        super(lineNum, opCode, "store");
    }
}
