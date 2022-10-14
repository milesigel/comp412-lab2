import intermediate_representation.IRList;
import intermediate_representation.IRNode;

import java.util.*;

public class Allocator {


    private static final int INVALID = -1;
    private static final int VALID = Integer.MAX_VALUE;

    private int spillLocation = 32768;
    private static final int MEMOP = 0;
    private static final int LOADI = 1;
    private static final int ARITHOP = 2;


    private IRList renamedCode;
    private int marks;
    private int numRegisters;
    private int numVirtualRegisters;
    private int maxLive;
    private int[] VRtoPR;
    private int[] PRtoVR;
    private int[] VRtoSpillLoc;
    private int[] PRtoNU;
    private Stack<Integer> registerStack;
    private IRNode workingNode;
    private int reservedSpillRegister;

    public Allocator(int numRegisters, IRList renamedCode, int vrName, int maxLive) {
        this.reservedSpillRegister = numRegisters-1;
        this.registerStack = new Stack<>();
        this.numRegisters = numRegisters;
        this.renamedCode = renamedCode;
        this.maxLive = maxLive;
        this.VRtoSpillLoc = new int[vrName];
        // initialize all data structures
        this.PRtoVR = new int[numRegisters];
        this.marks = INVALID;
        this.VRtoPR = new int[vrName];
        this.PRtoNU = new int[numRegisters];
        this.workingNode = renamedCode.getHead().getNext();
        for (int i = 0; i < vrName; i++) {
            VRtoPR[i] = INVALID;
        }
        Arrays.setAll(PRtoNU, i -> INVALID);
        Arrays.setAll(PRtoVR, i -> INVALID);
        Arrays.setAll(VRtoSpillLoc, i -> INVALID);
        if (maxLive <= numRegisters) {
            registerStack.push(reservedSpillRegister); // add the last one to the stack if maxlive is less than or equal
        }
        for (int i = numRegisters-2; i >= 0; i--) { // leaving the last register value empty for spills
            registerStack.push(i);
        }
    }

    /**
     * Frees a PR by removing it from the mappings and pushing it onto the stack
     * @param pr the physical register being freed
     */
    private void freeAPR(int pr) {
        VRtoPR[PRtoVR[pr]] = INVALID;
        PRtoVR[pr] = INVALID;
        PRtoNU[pr] = INVALID;
        registerStack.push(pr);
    }


    /**
     * Restores a virtual register into a physical register
     * @param virtualRegister
     */
    private void restore(int virtualRegister, int physicalRegister) {
        String printS = "";
        for (int i: VRtoSpillLoc) {
            printS += Integer.toString(i) + ", ";
        }
//        System.out.println(virtualRegister);
//        System.out.println(printS);
        // implement
        IRNode loadI = new IRNode(Integer.MAX_VALUE, LOADI, "loadI");
        loadI.setSourceRegister(0, VRtoSpillLoc[virtualRegister]);
        loadI.setPhysicalRegister(1, reservedSpillRegister);

        insertAboveWorkingNode(loadI);

        IRNode load = new IRNode(Integer.MAX_VALUE, MEMOP, "load");
        load.setPhysicalRegister(0, reservedSpillRegister);
        load.setPhysicalRegister(1, physicalRegister);

        insertAboveWorkingNode(load);
    }
    /**
     * Gets a new physical register for an operation
     * @return the PR that will be inside of an operation
     */
    private int getAPR(int virtualRegister, int nextUse) {
        int reg;
        if (registerStack.size() != 0) {
            reg = registerStack.pop();
        } else {
            // find register to spill (preferably the one with the highest next use value
            reg = spill();
        }
        VRtoPR[virtualRegister] = reg;
        PRtoVR[reg] = virtualRegister;
        PRtoNU[reg] = nextUse;
        return reg;
    }

    private int spill() {
        int currentSpillAddress;
        int spillReg = 0;
        int highestNU = 0;
        int spillVR = 0;
        for (int i = 0; i < numRegisters-1; i++) {
            if (i == marks) { // dont spill
                continue;
            }
            if (PRtoNU[i] > highestNU) {
                spillReg = i;
                highestNU = PRtoNU[i];
                spillVR = PRtoVR[i];
            }
        }
        if (VRtoSpillLoc[spillVR] == INVALID) {
            currentSpillAddress = spillLocation;
            VRtoSpillLoc[spillVR] = spillLocation;
            spillLocation += 4;
        } else {
            currentSpillAddress = VRtoSpillLoc[spillVR];
        }

        VRtoPR[spillVR] = INVALID; // setting the spilled value to now be invalid]


        IRNode loadI = new IRNode(Integer.MAX_VALUE, LOADI, "loadI");
        loadI.setSourceRegister(0, currentSpillAddress);
        loadI.setPhysicalRegister(1, reservedSpillRegister);

        insertAboveWorkingNode(loadI);

        IRNode store = new IRNode(Integer.MAX_VALUE, MEMOP, "store");
        store.setPhysicalRegister(0, spillReg);
        store.setPhysicalRegister(1, reservedSpillRegister);

        insertAboveWorkingNode(store);

        return spillReg;

        // finish this
    }

    public void allocate() {
        IRNode tail = renamedCode.getTail();
        int opCode;
        while (workingNode != tail) {
            clearMarks();
            switch (workingNode.getOpCode()) {
                case MEMOP: { // MEMOP (store, load)
                    handleMEMOP();
                    break;
                }
                case LOADI: { // LOADI (loadI)
                    handleLOADI();
                    break;
                }
                case ARITHOP: { // (add, sub, mult, lshift, rshift)
                    handleARITHOP();
                    break;
                }
            }
            workingNode = workingNode.getNext();
        }
    }


    private void handleARITHOP() {
        handleUses(Arrays.asList(0, 1));
        handleDef(Arrays.asList(2));
    }

    private void handleLOADI() {
        handleDef(Arrays.asList(1));
    }

    private void clearMarks() {
        marks = INVALID;
    }

    private void handleMEMOP() {
        if (workingNode.getLexeme().equals("store")) {
            handleUses(Arrays.asList(0,1));
        } else { // load
            handleUses(Arrays.asList(0));
            handleDef(Arrays.asList(1));
        }
    }

    private void handleUses(List<Integer> uses) {
        int pr;
        for (int i: uses) {
            pr = VRtoPR[workingNode.getVirtualRegister(i)];
            if (pr == INVALID) {
                pr = getAPR(workingNode.getVirtualRegister(i), workingNode.getnextUse(i));
                workingNode.setPhysicalRegister(i, pr);
                restore(workingNode.getVirtualRegister(i), pr);
            } else {
                workingNode.setPhysicalRegister(i, pr);
                PRtoNU[pr] = workingNode.getnextUse(i);
            }
            marks = workingNode.getPhysicalRegister(i);
        }
        for (int i: uses) {
            if (workingNode.getnextUse(i) == INVALID && PRtoVR[workingNode.getPhysicalRegister(i)] != INVALID) {
                freeAPR(workingNode.getPhysicalRegister(i));
            }
        }
        clearMarks();
    }


    private void handleDef(List<Integer> defs) {
        for (int i: defs) {
            workingNode.setPhysicalRegister(i, getAPR(workingNode.getVirtualRegister(i), workingNode.getnextUse(i)));
        }
    }

    private void insertAboveWorkingNode(IRNode node) {
        node.setNext(workingNode);
        node.setPrev(workingNode.getPrev());
        workingNode.getPrev().setNext(node);
        workingNode.setPrev(node);
    }

}
