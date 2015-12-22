/* 
 * Puff.java
 *
 * A program that decompresses a file that was compressed using 
 * Huffman encoding.
 *
 * <Daniel Wakefield>, <your e-mail address: dan_wakefiel@hmc.edu>
 * <current date: 12/9/2015>
 */ 

import java.io.*;
import java.util.*;

public class Puff {

    /* Put any methods that you add here. */


    /** 
     * main method for decompression.  Takes command line arguments. 
     * To use, type: java Puff input-file-name output-file-name 
     * at the command-line prompt. 
     */ 
    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        ObjectInputStream in = null;      // reads in the compressed file
        FileWriter out = null;            // writes out the decompressed file

        // Get the file names from the command line (if any) or from the console.
        String infilename, outfilename;
        if (args.length >= 2) {
            infilename = args[0];
            outfilename = args[1];
        } else {
            System.out.print("Enter the name of the compressed file: ");
            infilename = console.nextLine();
            System.out.print("Enter the name to be used for the decompressed file: ");
            outfilename = console.nextLine();
        }

        // Open the input file.
        try {
            in = new ObjectInputStream(new FileInputStream(infilename));
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + infilename);
            System.exit(1);
        }

        // Open the output file.
        try {
            out = new FileWriter(outfilename);
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + outfilename);
            System.exit(1);
        }
    
        // Create a BitReader that is able to read the compressed file.
        BitReader reader = new BitReader(in);


        /****** Add your code here. ******/
        
        // create an empty character frequency array
        int[] charArray = new int[256];
        
        // populate it with the frequencies given in the header
        for (int i = 0; i < 256; i++) {
            if ((charArray[i] = in.readInt()) == -1) {
                System.out.println("error reading header"); 
                System.exit(1);
            }
        }
        
        // create the huffington tree from that array
        HuffTree tree = new HuffTree(charArray);
        
        // create a new bitreader to read from infile
        BitReader br = new BitReader(in);
        
        // a variable to store the bit
        int bit;
        
        // create a new decoder object 
        HuffTree.Decoder dec = tree.getDecoder();
        
        // while there are still bits to read
        while ((bit = br.getBit()) != -1) {
            // increment the decoder object - if we reach a leaf node
            if (dec.nextStep(bit)) {
                // write it to the outfile
                out.write(dec.getChar());
            }
        }
          
        /* Leave these lines at the end of the method. */
        in.close();
        out.close();
    }
}
