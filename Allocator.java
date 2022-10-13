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
    private static final int OUTPUT = 3;


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
        this.numRegisters = numRegisters;
        this.renamedCode = renamedCode;
        this.maxLive = maxLive;
        // initialize all data structures
        this.PRtoVR = new int[numRegisters];
        this.marks = INVALID;
        this.VRtoPR = new int[vrName-1];
        this.PRtoNU = new int[numRegisters];
        this.workingNode = renamedCode.getHead().getNext();
        for (int i = 0; i < vrName-1; i++) {
            VRtoPR[i] = INVALID;
        }
        Arrays.setAll(PRtoNU, i -> INVALID);
        Arrays.setAll(PRtoVR, i -> INVALID);
        for (int i = 0; i < numRegisters-1; i++) { // leaving the last register value empty for spills
            registerStack.push(i);
        }
        if (maxLive <= numRegisters) {
            registerStack.push(reservedSpillRegister); // add the last one to the stack if maxlive is less than or equal
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
        // implement
        IRNode loadI = new IRNode(Integer.MAX_VALUE, LOADI, "loadI");
        loadI.setSourceRegister(0, VRtoSpillLoc[virtualRegister]);
        loadI.setPhysicalRegister(1, reservedSpillRegister);

        insertAboveWorkingNode(loadI);

        IRNode load = new IRNode(Integer.MAX_VALUE, LOADI, "load");
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
            reg = spill(virtualRegister);
        }
        VRtoPR[virtualRegister] = reg;
        PRtoVR[reg] = virtualRegister;
        PRtoNU[reg] = nextUse;
        return reg;
    }

    private int spill(int virtualRegister) {
        int currentSpillAddress;
        int spillReg = 0;
        int highestNU = 0;
        for (int i = 0; i < numRegisters; i++) {
            if (i == marks) { // dont spill
                continue;
            } else {
                if (PRtoNU[i] > highestNU) {
                    spillReg = i;
                    highestNU = PRtoNU[i];
                }
            }
        }

        if (VRtoSpillLoc[virtualRegister] == INVALID) {
            currentSpillAddress = spillLocation;
            spillLocation += 4;
        } else {
            currentSpillAddress = VRtoSpillLoc[virtualRegister];
        }

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

    private void allocate() {
        IRNode tail = renamedCode.getTail();
        int opCode;
        while (workingNode != tail) {
            clearMarks();
            switch (workingNode.getOpCode()) {
                case MEMOP: { // MEMOP (store, load)
                    handleMEMOP();
                }
                case LOADI: { // LOADI (loadI)
                    handleLOADI();
                }
                case ARITHOP: { // (add, sub, mult, lshift, rshift)
                    handleARITHOP();
                }
            }
            workingNode = workingNode.getNext();
        }
    }


    private void handleARITHOP() {
        handleUses(List.of(0, 1));
        handleDef(List.of(2));
    }

    private void handleLOADI() {
        handleDef(List.of(1));
    }

    private void clearMarks() {
        marks = INVALID;
    }

    private void handleMEMOP() {
        if (workingNode.getLexeme().equals("store")) {
            handleUses(List.of(0,1));
        } else { // load
            handleUses(List.of(1));
        }
    }

    private void handleUses(List<Integer> uses) {
        int pr;
        for (int i: uses) {
            pr = VRtoPR[workingNode.getVirtualRegister(i)];
            if (pr == INVALID) {
                workingNode.setPhysicalRegister(i, getAPR(workingNode.getVirtualRegister(i), workingNode.getnextUse(i)));
                restore(workingNode.getVirtualRegister(i), workingNode.getPhysicalRegister(i));
            } else {
                workingNode.setPhysicalRegister(i, pr);
            }
            marks = workingNode.getPhysicalRegister(i);
        }
        for (int i: uses) {
            if (workingNode.getnextUse(i) == INVALID && PRtoVR[workingNode.getPhysicalRegister(i)] != INVALID) {
                freeAPR(workingNode.getPhysicalRegister(i));
            }
        }
    }


    private void handleDef(List<Integer> defs) {
        for (int i: defs) {
            workingNode.setPhysicalRegister(workingNode.getVirtualRegister(i), workingNode.getnextUse(i));
        }
    }

    private void insertAboveWorkingNode(IRNode node) {
        node.setNext(workingNode);
        node.setPrev(workingNode.getPrev());
        workingNode.getPrev().setNext(node);
        workingNode.setPrev(node);
    }


}
