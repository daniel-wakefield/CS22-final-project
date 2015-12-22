/* 
 * Huff.java
 *
 * A program that compresses a file using Huffman encoding.
 *
 * <Daniel Wakefield>, <dan_wakefield@hmc.edu>
 * <12/9/2015>
 */ 

import java.io.*;
import java.util.*;

public class Huff {

    /* Put any methods that you add here. */


    /** 
     * main method for compression.  Takes command line arguments. 
     * To use, type: java Huff input-file-name output-file-name 
     * at the command-line prompt. 
     */ 
    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        FileReader in = null;               // reads in the original file
        ObjectOutputStream out = null;      // writes out the compressed file
        
        // Get the file names from the command line (if any) or from the console.
        String infilename, outfilename;
        if (args.length >= 2) {
            infilename = args[0];
            outfilename = args[1];
        } else {
            System.out.print("Enter the name of the original file: ");
            infilename = console.nextLine();
            System.out.print("Enter the name to be used for the compressed file: ");
            outfilename = console.nextLine();
        }
            
        // Open the input file.
        try {
            in = new FileReader(infilename);
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + infilename);
            System.exit(1);
        }

        // Open the output file.
        try {
            out = new ObjectOutputStream(new FileOutputStream(outfilename));
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + outfilename);
            System.exit(1);
        }
        
        
                                           
    
        // Create a BitWriter that is able to write to the compressed file.
        BitWriter writer = new BitWriter(out);

        /****** Add your code below. ******/
        /* 
         * Note: After you read through the input file once, you will need
         * to reopen it in order to read through the file
         * a second time.
         */
        
        // create an array to store the different character frequencies
        int[] charFreq = new int[256];
        
        // variable to store next character of infile
        int r;
        
        // run through the entire file, incrmenting the appropriate entry in
        // the character frequency array
        while ((r = (int) in.read()) != -1) {
            charFreq[(int)r]++;
        }
        
        // create a new huffinton tree object
        HuffTree tree = new HuffTree(charFreq);
        
        // get the array of the approprate codes for each character
        Code[] cArray = tree.CodeArray();
        
        // close then reopen the infile 
        in.close();
        
        try {
            in = new FileReader(infilename);
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + infilename);
            System.exit(1);
        }
        
        // copy the frequency array into the header of the outfile
        for (int i = 0; i < 256; i++) {
            out.writeInt(charFreq[i]);
        }
        
        // create a new BitWriter
        BitWriter bw = new BitWriter(out);
        
        // run through the infile
        while((r= (int) in.read()) != -1) {
            // and for each character write the appropriate code to the outfile
            bw.writeCode(cArray[r]);
        }
        
        /* Leave these lines at the end of the method. */
        in.close();
        out.close();
    }
}
