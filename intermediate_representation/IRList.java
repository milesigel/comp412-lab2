package intermediate_representation;

public class IRList {

    public static String[] tokenConversion = new String[12];

    private IRNode head;
    private IRNode tail;
    public IRList() {
        tokenConversion[0] = "MEMOP";
        tokenConversion[1] = "LOADI";
        tokenConversion[2] = "ARITHOP";
        tokenConversion[3] = "OUTPUT";
        tokenConversion[4] = "NOP";
        tokenConversion[5] = "CONSTANT";
        tokenConversion[6] = "REGISTER";
        tokenConversion[7] = "COMMA";
        tokenConversion[8] = "INTO";
        tokenConversion[9] = "EOF";
        tokenConversion[10] = "EOL";
        tokenConversion[11] = "REG";
        head = new IRNode(-1,-1, "");
        tail = new IRNode(-1, -1, "");
        head.setNext(tail);
        tail.setPrev(head);

    }

    public void append(IRNode node) {
        node.setNext(tail);
        node.setPrev(tail.getPrev());
        tail.getPrev().setNext(node);
        tail.setPrev(node);

    }

    public void prepend(IRNode node) {
        node.setNext(head.getNext());
        head.getNext().setPrev(node);
        head.setNext(node);
        node.setPrev(head);
    }

    public String toString() {
        String toReturn = "";
        IRNode currNode = head.getNext();
        while(currNode != tail && currNode != null) {
            toReturn += currNode.toString() + "\n";
            currNode = currNode.getNext();
        }
        return toReturn;
    }

    public String getILoc() {
        String toReturn = "";
        IRNode currNode = head.getNext();
        while(currNode != tail && currNode != null) {
            toReturn += currNode.getILOCRepresentation() + "\n";
            currNode = currNode.getNext();
        }
        return toReturn;
    }

    public IRNode getHead() {
        return this.head;
    }

    public IRNode getTail() {
        return this.tail;
    }


}
