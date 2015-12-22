/*
 * HuffTree - a class that represents a huffington tree
 * the constructor takes an array of integers where each
 * index is the char's ascii value, and uses that to create
 * the huffington tree.
 * 
 * Daniel wakeifeld
 * dan_wakefield@hmc.edu
 * 
 */
public class HuffTree {
  
  // a node for the huffington tree
  private class Node {
      private char key;   // the character
      private int freq;   // that characters frequency
      private boolean leaf;  // is this a leaf node - if false there is no char or freq attribute
      private Node left;     // left node in tree
      private Node right;   // right node in tree
      private Node next;    // next node in linked list before tree is produced
    
      // constructor for leaf node
      private Node (char k, int f, boolean l) {
          this.key = k;
          this.freq = f;
          this.leaf = l;
          this.left = null;
          this.right = null;
          this.next = null; 
      }
    
      // constructor for interior node
      private Node(int f, boolean l) {
          this.freq = f;
          this.leaf = l;
          this.left = null;
          this.right = null;
          this.next = null;
      }
  }
  
  // the start of the linked list/ root of the tree
  private Node Start;
  
  /*
   * constructor for the HuffTree class - it takes an array of character
   * frequencies, indexed by ascii values, and creates the tree
   */
  public HuffTree(int[] arr) {
    
      // initialize the root/ start node
      Start = null;
    
      // create a linked list from the array of character frequencies
      for (int i = 0; i < arr.length; i++) {
          // if values if nonzero and positive
          if (arr[i] > 0) {
              // create a new node, with this char and freq, and it's a leaf node (true)
              Node n = new Node((char)i, arr[i], true);
          
              // insert into the linked list in ascending order
              InsertNode(n);
          }  
      }
    
      // create a new node to merge lowest two nodes in linked list
      Node newNode;
    
      // while there are at least two nodes in the linked list
      while(Start != null && Start.next != null) {
          // merge the first two nodes
          newNode = Merge(Start, Start.next);
      
          // increment linked list to third node
          Start = Start.next.next;
      
          // insert newly merged node back into list
          InsertNode(newNode);
      
          // when loop is finished huffman tree is created
      }
  }
  
  /*
   * Merge creates a new interior node with the combined requency of 
   * node n and m, and the new node has children n and m, with the lowest 
   * frequency child on the left and the higher on the right - it returns
   * a reference to the new node
   */
  private Node Merge(Node n, Node m) {
      // create the new interior node
      Node parent = new Node (n.freq + m.freq, false);
      
      
      // if the frequency of n is less than m
      if (n.freq < m.freq) {
          // the left node is n 
          parent.left = n;
          
          // and the right node is m
          parent.right = m;
          
          // next node is null
          parent.next = null;
      }
      
      // otherwise swap n and m
      else {
          parent.left = m;
          parent.right = n;
          parent.next = null;
      }
      
      // return the newly created node
      return parent;
  }
  
  /*
   * InsertNode inserts a node into the linked list of nodes
   * creates a list sorted in ascending order
   */
  private void InsertNode(Node n) {
    // if there are no other nodes
    if (Start == null) {
       // insert n at the head of the list and return
       Start = n;
       return;
    }
    
    // otherwise create a trav node and a trailing refernce
    Node trav = Start;
    Node prev = null;
    
    // while we haven't reached the end of the list
    while(trav != null) {
        // we need to continue in the list to find the right spot to insert
        if (trav.freq < n.freq) {
            // increment prev
            prev = trav;
            // increment trav
            trav = trav.next;
        }
      
        // otherwise we have found the place we need to insert
        else {
            // if it's before the first nod
            if (prev == null) {
                // make the next node the start of the list
                n.next = Start;
           
                // make current node the new start
                Start = n;
          
                // return
                return;
             }
        
              // otherwise we need to insert in the middle of th elist
              else {
                  // insert the node between prev and trav
                  n.next = trav;
                  prev.next = n;
                  // then return
                  return;
              }
          }
      }
    
      // if we reach this point we insert at the end of the list
      prev.next = n;
  }
  
  /*
   * CodeArray returns an array of codes based upon the current
   * huffington tree
   */
  public Code[] CodeArray() {
      // create a new array of codes
      Code[] arr = new Code[256];
      
      // create a single code to be used recursively
      Code c = new Code();
     
      // call the recursive helper method
      CodeArrayHelper(arr, Start, c);
      
      // return the array
      return arr;
  }
  
  /*
   * The recursive code array helper that fills the array arr
   * with the approprate codes for each letter in the huffington 
   * tree
   */
  private void CodeArrayHelper(Code[] arr, Node n, Code c) {
      // if we're at a leaf node 
      if (n.leaf) {
           // put the code into the array then return
           arr[(int)n.key] = new Code(c);
           return;
      }
      
      // if there is a left node
      if (n.left != null) {
          // add a zero to the code
          c.addBit(0);
          
          // call the method on the left subtree
          CodeArrayHelper(arr, n.left, c);
          
          // remove the bit we added before the recursive call
          c.removeBit();
      }
      
      // if there is a right node 
      if (n.right != null) {
          // add a one to the code
          c.addBit(1);
          
          // call the method on the right subree
          CodeArrayHelper(arr, n.right, c);
          
          // remove the bit we added
          c.removeBit();
      }
  }
  
  // returns a new decoder object
  public Decoder getDecoder() {
      return new Decoder();
  }
  
  /*
   * Decoder is a public class used to decode a code and
   * return the proper character - it's a lot like an iterator
   */ 
  public class Decoder {
      // a reference to the current node
      private Node curr;
      
      // a copy of the last character we've decoded
      private char nextChar;
      
      // constructor - creates a new decoder object
      public Decoder () {
          curr = Start;
      }
      
      // returns the last char we've decoded
      public char getChar() {
          return nextChar;
      }
      
      /*
       * nextStep takes in a bit, either zero or one, iterates
       * to the next node in tree.  If that node is a leaf, it updates
       * nextChar and returns true - and upon the next call to nextStep
       * it begins at the begining of the tree. If that node is an interior, it 
       * just updates curr, does nothing to nextChar and returns false.
       */
      public boolean nextStep(int bit) {
          // if we were just at a leaf node go to root of tree
          if (curr.leaf)
              curr = Start;
          
          // if curr is not a leaf node - this case would only matter
          // if there is only one node in the tree
          if (!curr.leaf) {
              // if bit is one go to right child node
              if (bit == 1)
                  curr = curr.right;
              // otherwise go to left child node
              else if (bit == 0)
                  curr = curr.left;
          }
          
          // if we ended at a leaf
          if (curr.leaf) {
              // update nextChar and return true
              nextChar = curr.key;
              return true;
          }
          
          // otherwise return false
          else
              return false;
      }
      
  }
}