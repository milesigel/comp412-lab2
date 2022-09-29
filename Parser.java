import intermediate_representation.IRList;
import intermediate_representation.IRNode;
import pairs.Pair;

public class Parser {
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

    private Scanner scanner;
    private IRList representationList;
    private Pair word;
    private int lineNumber = 1;
    private int maxSR = 0;
    private int error = 0;

    public Parser(Scanner scan) {
        // construction
        this.scanner = scan;
        this.representationList = new IRList();
    }

    public IRList getIR() {
        return this.representationList;
    }

    public int parseNTokens(int num) {
        word = scanner.getNextWord();
        for (int i = 0; i < num; i++) {
            switch (word.token) {
                case 0 :
                    finishMEMOP();
                    break;
                case 1 :
                    finishLOADI();
                    break;
                case 2 :
                    finishARITHOP();
                    break;
                case 3 :
                    finishOUTPUT();
                    break;
                case 4 :
                    finishNOP();
                    break;
            }
            word = scanner.getNextWord();
        }
        return 1;
    }

    public int parse() {
        word = scanner.getNextWord();
        while (word.token != 9) {
//            System.out.println(word.token);
            switch (word.token) {
                case 0 :
                    finishMEMOP();
                    break;
                case 1 :
                    finishLOADI();
                    break;
                case 2 :
                    finishARITHOP();
                    break;
                case 3 :
                    finishOUTPUT();
                    break;
                case 4 :
                    finishNOP();
                    break;
                case 9 :
                    endOfFile();
                    break;
                case -1 :
                    handleError();
                    break;
            }
            word = scanner.getNextWord();
            lineNumber++;
        }
        return error;
    }

    private void endOfFile() {
        return;
    }

    private void finishNOP() {
        IRNode newNode = new IRNode(this.lineNumber, 4);
        word = scanner.getNextWord();
        if (word.token == 10) {
            // eol
            representationList.append(newNode);
            return;
        } else {
            printCustomErrorMsg("There was no EOL following the NOP operation");
        }
    }

    private void finishOUTPUT() {
        IRNode newNode = new IRNode(this.lineNumber, 3);
        word = scanner.getNextWord();
        if (word.token == 5) {
            // constant
            newNode.setSourceRegister(1, Integer.parseInt(word.lexeme));
            word = scanner.getNextWord();
            if (word.token == 10) {
                // eol
                representationList.append(newNode);
                return;
            } else {
                printCustomErrorMsg("There was no EOL at the end of the OUTPUT operation");
            }
        } else {
            printCustomErrorMsg("There was no constant following the OUTPUT operation");
        }

    }

    private void finishARITHOP() {
        IRNode newNode = new IRNode(this.lineNumber, 2);
        String lexeme = word.lexeme;
        word = scanner.getNextWord();
        if (word.token == 11) {
            // register
            newNode.setSourceRegister(1, Integer.parseInt(word.lexeme));
            checkAndSetMaxSR(Integer.parseInt(word.lexeme));
            word = scanner.getNextWord();
            if (word.token == 7) {
                // comma
                word = scanner.getNextWord();
                if (word.token == 11) {
                    // register
                    newNode.setSourceRegister(2, Integer.parseInt(word.lexeme));
                    checkAndSetMaxSR(Integer.parseInt(word.lexeme));
                    word = scanner.getNextWord();
                    if (word.token == 8) {
                        // into
                        word = scanner.getNextWord();
                        if (word.token == 11) {
                            // register
                            newNode.setSourceRegister(3, Integer.parseInt(word.lexeme));
                            checkAndSetMaxSR(Integer.parseInt(word.lexeme));
                            word = scanner.getNextWord();
                            if (word.token == 10) {
                                // eol
                                representationList.append(newNode);
                                return;
                            } else {
                                printCustomErrorMsg("There was no EOL operation at the end of the " + lexeme + " operation");
                            }
                        } else {
                            printCustomErrorMsg("There was no Register value at the end in the " + lexeme + " operation");
                        }
                    } else {
                        printCustomErrorMsg("There was no => in the " + lexeme + " operation");
                    }
                } else {
                    printCustomErrorMsg("There was no Register value at the start in the " + lexeme + " operation");
                }
            } else {
                printCustomErrorMsg("There was no comma in the " + lexeme + " operation");
            }
        } else {
            printCustomErrorMsg("There was no register following the start of the " + lexeme + " operation");
        }

    }


    private void finishMEMOP() {
        IRNode newNode = new IRNode(this.lineNumber, 0);
        String lexeme = word.lexeme;
        word = scanner.getNextWord();
        if (word.token == 11) {
            newNode.setSourceRegister(1, Integer.parseInt(word.lexeme));
            checkAndSetMaxSR(Integer.parseInt(word.lexeme));
            word = scanner.getNextWord();
            if (word.token == 8) {
                word = scanner.getNextWord();
                if (word.token == 11) {
                    newNode.setSourceRegister(2, Integer.parseInt(word.lexeme));
                    checkAndSetMaxSR(Integer.parseInt(word.lexeme));
                    word = scanner.getNextWord();
                    if (word.token == 10) {
                        representationList.append(newNode);
                        return;
                    } else {
                        printCustomErrorMsg("There was no EOL operation at the end of the " + lexeme + " operation");
                    }
                } else {
                    printCustomErrorMsg("There was no Register value at the end in the " + lexeme + " operation");
                }
            } else {
                printCustomErrorMsg("There was no => in the " + lexeme + " operation");
            }
        } else {
            printCustomErrorMsg("There was no Register value at the start in the " + lexeme + " operation");
        }
    }
    
    private void finishLOADI() {
        // start with loadI
        IRNode newNode = new IRNode(this.lineNumber, 1);
        word = scanner.getNextWord();
        if (word.token == 5) {
            // constant
            newNode.setSourceRegister(1, Integer.parseInt(word.lexeme));
            word = scanner.getNextWord();
            if (word.token == 8) {
                // into
                word = scanner.getNextWord();
                if (word.token == 11) {
                    // register
                    newNode.setSourceRegister(2, Integer.parseInt(word.lexeme));
                    checkAndSetMaxSR(Integer.parseInt(word.lexeme));
                    word = scanner.getNextWord();
                    if (word.token == 10) {
                        // eol
                        representationList.append(newNode);
                        return;
                    } else {
                        printCustomErrorMsg("There was no EOL operation at the end of the LOADI");
                    }
                } else {
                    printCustomErrorMsg("There was no register following the into in LOADI");
                }
            } else {
                printCustomErrorMsg("There was no INTO operation following the constant in LOADI");
            }
        } else {
            printCustomErrorMsg("There was no constant following the LOADI operation");
        }

    }

    /**
     * Handle error state when there is not a valid sentence in the ILOC grammar.
     */
    private void handleError() {
        error++;
        System.err.println("ERROR " + this.lineNumber + ": " + word.lexeme);
    }

    private void printCustomErrorMsg(String msg) {
        error++;
        if (this.word.token == -1) {
            System.err.println("ERROR " + this.lineNumber + ": " + word.lexeme);
        } else {
            System.err.println("ERROR " + this.lineNumber + ": " + msg);
            if (word.token != -1) {
                while (word.token != 10 && word.token != -1) {
                    word = scanner.getNextWord();
                }
            }
        }
    }

    private void checkAndSetMaxSR(int regNum) {
        if (regNum > this.maxSR) {
            this.maxSR = regNum;
        }
    }


    public int getMaxSR() {
        return this.maxSR;
    }
}
