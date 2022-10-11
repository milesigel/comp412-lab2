/**
 * 1. 412fe –h When a –h flag is detected, 412fe will produce a list of valid command-line arguments that includes a
 * description of all command-line arguments required for Lab 1 as well as any additional command-line arguments
 * supported by your 412fe implementation.
 *  - 412fe is not required to process command-line arguments that appear after the –h flag.
 *
 * 2. 412fe -s <name> When a -s flag is present, 412fe should read the file specified by <name> and print,
 * to the standard output stream, a list of the tokens that the scanner found.
 *  - For each token, it should print the line number, the token’s type (or syntactic category), and its spelling (or lexeme).
 *
 * 3. 412fe -p <name> When a -p flag is present, 412fe should read the file specified by <name>, scan it and parse it,
 * build the intermediate representation, and report either success or report all the errors that it finds in the input file.
 *
 * 4. 412fe -r <name> When a -r flag is present, 412fe should read the file specified by <name>, scan it, parse it,
 * build the intermediate representation, and print out the information in the intermediate representation
 * (in an appropriately human readable format).
 */
import intermediate_representation.IRList;

import java.io.*;
import java.util.ArrayList;


public class AllocMain {


    public static void printCommandHelp() {
        System.out.println("Here are some helpful flags which are helpful in running this program! \n");
        System.out.println(" * -h ---> Help flag will display all of the flag options for this program");
        System.out.println(" * -x <name> ---> scan and parse the input block. It should then perform renaming on the " +
                "code in the input block and print the results to the standard output stream (stdout)");

    }

    private static void printRetVal(int retVal, String file_name) {
        if (retVal == 0) {
            System.out.println("The parser successfully parsed the file " + file_name);
        } else {
            System.out.println("The parser encountered " + retVal + " errors while parsing the file " + file_name);
        }
    }

    private static void printFileNotFoundError(String fileName) {
        System.err.println("There was an error opening the file : " + fileName);
    }


    /**
     * Main entry into the program.
     * @param args number of different flags that specify operation
     *
     * - h ---> Help option
     * - x ---> The -x flag will only be used for Code Check 1. Again, <name> is a Linux pathname. With this flag,
     *          412alloc should scan and parse the input block. It should then perform renaming on the code in the
     *          input block and print the results to the standard output stream (stdout). lab2_ref does not implement
     *          the -x flag.
     * - k ---> k is the number of registers available to the allocator (3 ≤ k ≤ 64) and <name> is a Linux pathname to
     *          the file containing the input block
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            printCommandHelp();
            return;
        }
        String command = args[0];
        String filePath = args[1];
        if (command.equals("-x")) {
            // ~comp412/students/lab2/code_check_1/ are where thr test files are found
            Scanner scan = new Scanner(filePath);
            if (scan.openFile() == 0) {
                printFileNotFoundError(filePath);
                printCommandHelp();
                return;
            }
            Parser parser = new Parser(scan);
            int retVal = parser.parse();
            if (retVal == 0) {
                // the parse was successful and we can get the IR from the parser
                IRList representation = parser.getIR();
                Renamer renamer = new Renamer(representation, parser.getMaxSR());
                renamer.addVirtualRegisters();
                System.out.println(representation.getILoc());
            } else {
                // there was an error in the parse
            }


        } else if (command.equals("-k")) {

        } else {
            printCommandHelp();
        }
    }
}
