import intermediate_representation.IRList;
import pairs.Pair;

import java.io.*;

public class Scanner {

    private final String fileName;
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

    // buffered reader
    private BufferedReader reader;
    private int lineNumber = 1;
    String stringLine;
    private int index_in_line = -1;
    private char c;
    private int token = -2;
    private String lexeme;


    public Scanner(String file_name) throws IOException {
        this.fileName = file_name;
        // stuff about construction
    }

    private void getNextChar() {
        this.index_in_line++;
        try {
            this.c = stringLine.charAt(index_in_line);
        } catch (IndexOutOfBoundsException e) {
            // some sort of EOL command or transition onto the next line
        } catch (NullPointerException e) {
            this.endOfFile();
        }
    }

    private void moveBackChar() {
        this.index_in_line--;
        try {
            this.c = stringLine.charAt(index_in_line);
        } catch (IndexOutOfBoundsException e) {
            // some sort of EOL command or transition onto the next line
        }
    }

    private void endOfFile() {
        this.token = 9;
        this.lexeme = "EOF";
        return;
    }

    private void moveToNextLine() {
        this.index_in_line = -1;
        try {
            this.stringLine = this.reader.readLine();
        } catch (IOException e) {
            // something ab sending EOF character
//            System.out.println("Hit an end of line error case");
            this.endOfFile();
//            System.err.println("There was an error at the end of the line");
        }
//        System.out.println("changing from line" + lineNumber + " to " + (lineNumber+1));
        this.lineNumber++;
    }

    public Pair getNextWord() {
        Pair pairToReturn;
        if (this.token == 10 || this.token == -1) {
            moveToNextLine();
        }
//        System.out.println(stringLine);
        try {
            if (index_in_line == this.stringLine.length() || this.stringLine.length() == 0) {
                return this.handleReturn();
            }
        } catch (NullPointerException e) {
//            System.out.println("went inside handle return on line " + this.lineNumber);
            this.endOfFile();
            return new Pair(this.token, this.lexeme, this.lineNumber);
        }

        this.getNextChar();
//        System.out.println(c + "    " + lineNumber + "    " + this.index_in_line + "   " + this.stringLine.length());

//        System.out.println(c);
        if (this.stringLine.length() <= this.index_in_line) {
            return this.handleReturn();
        }
        //System.out.print("inital call - " + c);

        while (c == 9 || c == 32) {
//            System.out.println(index_in_line + "     " + this.stringLine.length());
            this.getNextChar();
            if (index_in_line >= this.stringLine.length()) {
                this.handleReturn();
                return new Pair(this.token, this.lexeme, this.lineNumber);
            }
        }

        //System.out.print("after whitespace loop - " + c);


        if (c == 's') {
            pairToReturn = this.handleS();
        }

        else if (c == 'l') {
            pairToReturn = this.handleL();
        }

        else if (c == 'r') {
            pairToReturn = this.handleR();
        }

        else if (c == 'm') {
            pairToReturn = this.handleM();
        }

        else if (c == 'a') {
            pairToReturn = this.handleA();
        }

        else if (c == 'n') {
            pairToReturn = this.handleN();
        }
        else if (c == 'o') {
            pairToReturn = this.handleO();
        }

        else if (c == '=') {
            pairToReturn = this.handleEquals();
        }

        else if (c == '\n') {
            pairToReturn = this.handleReturn();
        }

        else if (c == '/') {
            pairToReturn = this.handleComment();
        }

        else if (c == ',') {
            pairToReturn = this.handleComma();
        }

        else if (c >= '0' && c <= '9') {
            pairToReturn = this.handleNumber();
        }
        else {
            // some error state
            this.lexeme = "There was a word outside of the valid set of ILOC words";
            pairToReturn = this.printErrorMsg();
        }


        return pairToReturn;
    }

    private Pair handleComment() {
        this.getNextChar();
        if (c != '/' || this.index_in_line >= this.stringLine.length()) {
            return this.printErrorMsg();
        } else {
            return this.handleReturn();
        }
    }

    private Pair printErrorMsg() {
        this.token = -1;
        Pair pair = new Pair(-1, this.lexeme, lineNumber);
//        this.moveToNextLine();
        return pair;
    }

    private Pair handleL() {
        this.getNextChar();
        if (c == 'o') {
            this.getNextChar();
            if (c == 'a') {
                this.getNextChar();
                if (c == 'd') {
                    if (stringLine.charAt(index_in_line + 1) != 'I') {
                        this.token = 0;
                        this.lexeme = "load";
                        return checkForBlank();
//                        return new Pair(this.token, this.lexeme, this.lineNumber);
                    }
                    this.getNextChar();
                    if (c == 'I') {
                        this.token = 1;
                        this.lexeme = "loadI";
                        return checkForBlank();
//                        return new Pair(this.token, this.lexeme, this.lineNumber);
                    } else {
                        this.lexeme = "There was an error in the LOAD operation at the end of the OP since there was no I after";
                        return this.printErrorMsg();
                    }
                } else {
                    this.lexeme = "There was an error in the LOAD operation at the 'd'";
                    return this.printErrorMsg();
                }
            } else {
                this.lexeme = "There was an error in the LOAD operation at the 'a'";
                return this.printErrorMsg();
            }
        }
        else if (c == 's') {
            this.getNextChar();
            if (c == 'h') {
                this.getNextChar();
                if (c == 'i') {
                    this.getNextChar();
                    if (c == 'f') {
                        this.getNextChar();
                        if (c == 't') {
                            this.token = 2;
                            this.lexeme = "lshift";
                            return checkForBlank();
//                            return new Pair(this.token, this.lexeme, this.lineNumber);
                        } else {
                            this.lexeme = "There was an error in the 't' in the lshift OP";
                            return this.printErrorMsg();
                        }
                    } else {
                        this.lexeme = "There was an error in the 'f' in the lshift OP";
                        return this.printErrorMsg();
                    }
                } else {
                    this.lexeme = "There was an error in the 'i' in the lshift OP";
                    return this.printErrorMsg();
                }
            } else {
                this.lexeme = "There was an error in the 'h' in the lshift OP";
                return this.printErrorMsg();
            }
        }
        this.lexeme = "There was an error in an operation starting with 'l' but had no following valid op";
        return this.printErrorMsg();
    }

    private Pair handleS() {
        this.getNextChar();
        if (c == 't') {
            this.getNextChar();
            if (c == 'o') {
                this.getNextChar();
                if (c == 'r') {
                    this.getNextChar();
                    if (c == 'e') {
                        this.token = 0;
                        this.lexeme = "store";
                        return checkForBlank();
//                        return new Pair(this.token, this.lexeme, this.lineNumber);
                    } else {
                        this.lexeme = "There was an error at the start of the 'e' in the store operation";
                        return this.printErrorMsg();
                    }
                } else {
                    this.lexeme = "There was an error at the start of the 'r' in the store operation";
                    return this.printErrorMsg();
                }
            } else {
                this.lexeme = "There was an error at the start of the store operation";
                return this.printErrorMsg();
            }
        }
        else if (c == 'u') {
            this.getNextChar();
            if (c == 'b') {
                this.token = 2;
                this.lexeme = "sub";
                return checkForBlank();
//                return new Pair(this.token, this.lexeme, this.lineNumber);
            } else {
                this.lexeme = "There was an error in the sub operation";
                return this.printErrorMsg();
            }
        }
        this.lexeme = "There was an error in an operation that starts with 's'";
        return printErrorMsg();
    }

    private Pair handleR() {
        this.getNextChar();
        if (c == 's') {
            this.getNextChar();
            if (c == 'h') {
                this.getNextChar();
                if (c == 'i') {
                    this.getNextChar();
                    if (c == 'f') {
                        this.getNextChar();
                        if (c == 't') {
                            this.token = 2;
                            this.lexeme = "rshift";
                            return checkForBlank();
//                            return new Pair(this.token, this.lexeme, this.lineNumber);
                        } else {
                            this.lexeme = "There was an error at the 't' of the RShift operation";
                            return this.printErrorMsg();
                        }
                    } else {
                        this.lexeme = "There was an error at the 'f' of the RShift operation";
                        return this.printErrorMsg();
                    }
                } else {
                    this.lexeme = "There was an error at the 'i' of the RShift operation";
                    return this.printErrorMsg();
                }
            } else {
                this.lexeme = "There was an error at the start of the RShift operation";
                return this.printErrorMsg();
            }
        } else if (c >= '0' && c <= '9') {
            Pair p = this.handleNumber();
            if (p.token == -1) {
                this.lexeme = "There was an error processing the register number";
                return this.printErrorMsg();
            }
            p.token = 11;
            return p;
//            return new Pair(this.token, this.lexeme, this.lineNumber);
        }
        this.lexeme = "There was an error where the parser saw an r token but not followed by a valid operation";
        return this.printErrorMsg();
    }

    private Pair handleM() {
        this.getNextChar();
        if (c == 'u') {
            this.getNextChar();
            if (c == 'l') {
                this.getNextChar();
                if (c == 't') {
                    this.token = 2;
                    this.lexeme = "mult";
                    return checkForBlank();
//                    return new Pair(this.token, this.lexeme, this.lineNumber);
                } else {
                    this.lexeme = "There was an error for a mult operation at the 't'";
                }
            } else {
                this.lexeme = "There was an error for a mult operation at the 'l' ";
            }
        } else {
            this.lexeme = "There was an error for a mult operation";
        }
        return this.printErrorMsg();
    }

    private Pair handleNumber() {
        int num = Character.getNumericValue(c);
        this.getNextChar();
        while(c >= '0' && c <= '9' && stringLine.length() > index_in_line) {
            num = num * 10 + Character.getNumericValue(c);
            this.getNextChar();
        }
//        System.out.println(num + " " + index_in_line + " " + stringLine.length());
        if (c == 9 || c == 32 || stringLine.length() == index_in_line) {
            this.token = 5;
            this.lexeme = String.valueOf(num);
            return new Pair(this.token, this.lexeme, this.lineNumber);
        }
        else if (c == ',' || c == '=' || c == '/') {
            this.moveBackChar();
            this.token = 5;
            this.lexeme = String.valueOf(num);
            return new Pair(this.token, this.lexeme, this.lineNumber);
        } else {
            this.lexeme = "There was an error processing the constant";
            return this.printErrorMsg();
        }
    }

    private Pair handleA() {
        this.token = 2;
        this.getNextChar();
        if (c == 'd') {
            this.getNextChar();
            if (c == 'd') {
                this.token = 2;
                this.lexeme = "add";
                return checkForBlank();
//                return new Pair(this.token, this.lexeme, this.lineNumber);
            } else {
                this.lexeme = "There was an error processing an add operation";
                return this.printErrorMsg();
            }
        } else {
            this.lexeme = "There was an error processing an add operation";
            return this.printErrorMsg();
        }
    }

    private Pair checkForBlank() {
        this.getNextChar();
        if (c == 9 || c == 32) {
            return new Pair(this.token, this.lexeme, this.lineNumber);
        } else {
            this.lexeme = "There was not a blank space after a " + IRList.tokenConversion[this.token] + " keyword statement";
            return this.printErrorMsg();
        }


    }

    private Pair handleN() {
        this.getNextChar();
        if (c == 'o') {
            this.getNextChar();
            if (c == 'p') {
                this.token = 4;
                this.lexeme = "nop";
                return new Pair(this.token, this.lexeme, this.lineNumber);
            } else {
                this.lexeme = "There was an error in the 'p' of the NOP operation";
                return this.printErrorMsg();
            }
        } else {
            this.lexeme = "There was an error in the NOP operation";
            return this.printErrorMsg();
        }
    }

    private Pair handleO() {
        this.getNextChar();
        if (c == 'u') {
            this.getNextChar();
            if (c == 't') {
                this.getNextChar();
                if (c == 'p') {
                    this.getNextChar();
                    if (c == 'u') {
                        this.getNextChar();
                        if (c == 't') {
                            this.token = 3;
                            this.lexeme = "output";
                            return checkForBlank();
//                            return new Pair(this.token, this.lexeme, this.lineNumber);
                        } else {
                            this.lexeme = "There was an error in the OUTPUT operation at the last 't'";
                            return this.printErrorMsg();
                        }
                    } else {
                        this.lexeme = "There was an error in the OUTPUT operation at the 'u";
                        return this.printErrorMsg();
                    }
                } else {
                    this.lexeme = "There was an error in the OUTPUT operation at the 'p";
                    return this.printErrorMsg();
                }
            } else {
                this.lexeme = "There was an error in the OUTPUT operation at the first 't'";
                return this.printErrorMsg();
            }
        }
        this.lexeme = "There was an error where the 'o' character was seen but not followed by a valid output operation";
        return this.printErrorMsg();
    }

    private Pair handleEquals() {
        this.getNextChar();
        if (c == '>') {
            this.token = 8;
            this.lexeme = "=>";
            return new Pair(this.token, this.lexeme, this.lineNumber);
        } else {
            this.lexeme = "There was an error in the into operation since there was no >";
            return this.printErrorMsg();
        }
    }

    private Pair handleComma() {
        // do something ab comma
        this.token = 7;
        this.lexeme = ",";
        return new Pair(this.token, this.lexeme, this.lineNumber);
    }

    private Pair handleReturn() {
        this.token = 10;
        this.lexeme = "EOL";
        Pair pair = new Pair(this.token, this.lexeme, this.lineNumber);
        return pair;
    }

    public void scanNTokens(int numTokens) {
        for (int i = 0; i < numTokens; i++) {
            Pair wordPair = this.getNextWord();
            System.out.println(wordPair);
        }
    }

    public void scanFile() {
        while(this.token != 9) {
            Pair wordPair = this.getNextWord();
            if (wordPair.token == -1) {
                System.err.println(wordPair);
            } else {
                System.out.println(wordPair);
            }
        }
    }


    public int openFile() {
        try {
            this.reader = new BufferedReader(new FileReader(this.fileName), 256);
            this.stringLine = this.reader.readLine();
            return 1;
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Could not open file" + this.fileName + " as the input file");
            System.err.println(e.getMessage());
            return 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
