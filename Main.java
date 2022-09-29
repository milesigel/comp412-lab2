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


public class Main {


    public static void printCommandHelp() {
        System.out.println("Here are some helpful flags which are helpful in running this program! \n");
        System.out.println(" * -h ---> Help flag will display all of the flag options for this program");
        System.out.println(" * -s <name> ---> read file specified by name and print tokens that the scanner finds");
        System.out.println(" * -p <name> ---> read file specified by name and report if parser successfully build IR");
        System.out.println(" * -r <name> ---> read file specified by name and print out the IR");
    }

    private static void printRetVal(int retVal, String file_name) {
        if (retVal == 0) {
            System.out.println("The parser successfully parsed the file " + file_name);
        } else {
            System.out.println("The parser encountered " + retVal + " errors while parsing the file " + file_name);
        }
    }


    /**
     * Main entry into the program.
     * @param args number of different flags that specify operation
     *
     * - h ---> Help option
     * - s <name> ----> read file specified by name and print tokens that the scanner finds
     * - p <name> ----> read file specified by name and report if parser successfully build IR
     * - r <name> ----> read file specified by name and print out the IR
     */
    public static void main(String[] args) throws IOException {
        Scanner scan;
        Parser parser;
        int retVal;
        if (args.length == 0) {
            System.out.println("There were no arguments passed in the command line! Please enter at least one flag");
            printCommandHelp();
        }
        else if (args.length == 1) {
            //there are two cases, either -h or -p <name> where the -p is not specified
            if (args[0].equals("-h")) {
                printCommandHelp();
            } else {
                String file_name = args[0];
                scan = new Scanner(file_name);
                if (scan.openFile() == 0) {
                    printCommandHelp();
                    return;
                }
                parser = new Parser(scan);
                retVal = parser.parse();
                printRetVal(retVal, file_name);
            }
        } else if (args.length == 2) {
            String file_name = args[1];
            scan = new Scanner(file_name);
            if (scan.openFile() == 0) {
                printCommandHelp();
                return;
            }
            parser = new Parser(scan);
            String flag = args[0];
            if (flag.equals("-s")) {
                scan.scanFile();
            } else if (flag.equals("-p")) {
                retVal = parser.parse();
                printRetVal(retVal, file_name);

            } else if (flag.equals("-r")) {
                retVal = parser.parse();
                printRetVal(retVal, file_name);
                IRList repList = parser.getIR();
                System.out.println(repList);
            }
        } else if (args.length > 2) {
            ArrayList<String> flags = new ArrayList<>();
            String fileName = args[args.length-1];
            scan = new Scanner(fileName);
            parser = new Parser(scan);
            System.out.println("Please only provide one command line argument \n");
            for (int i = 0; i < args.length - 2; i++) {
                flags.add(args[i]);
            }
            if (flags.contains("-h")) {
                printCommandHelp();
            } else if (flags.contains("-r")) {
                retVal = parser.parse();
                printRetVal(retVal, fileName);
                IRList repList = parser.getIR();
                System.out.println(repList);
            } else if (flags.contains("-p")) {
                retVal = parser.parse();
                printRetVal(retVal, fileName);
            } else if (flags.contains("-s")) {
                scan.scanFile();
            }
        }




    }
}
