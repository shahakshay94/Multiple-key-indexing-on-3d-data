import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Nodes {

  Nodes left, right;
  double data;
  double height;
  String tuple;

  public Nodes(double x, String tuple) {
    left = null;
    right = null;
    data = x;
    height = 0;
    this.tuple = tuple;
  }

}

class BalancingBinarySearchTree implements Runnable {

  public Nodes root;
  PrintWriter writer = null;
  private String fileName;
  public double lessthan;
  public double greaterthan;
  HashMap<String, List<String>> machedTup;

  public BalancingBinarySearchTree(String fileName) {
    root = null;
    this.fileName = fileName;
    this.machedTup = new HashMap<String, List<String>>();
  }


  public void insert(double data, String tuple) {

    root = insert(data, root, tuple);


  }

  private double height(Nodes t) {

    return t == null ? -1 : t.height;
  }

  private double max(double lhs, double rhs) {
    return lhs > rhs ? lhs : rhs;
  }

  private Nodes insert(double x, Nodes t, String tuple) {
    if (t == null) {
      t = new Nodes(x, tuple);
    } else if (x < t.data) {
      t.left = insert(x, t.left, tuple);
      if (height(t.left) - height(t.right) == 2) {
        if (x < t.left.data) {
          t = rotateWithLeftChild(t);
        } else {
          t = doubleWithLeftChild(t);
        }
      }
    } else if (x > t.data) {
      t.right = insert(x, t.right, tuple);
      if (height(t.right) - height(t.left) == 2) {
        if (x > t.right.data) {
          t = rotateWithRightChild(t);
        } else {
          t = doubleWithRightChild(t);
        }
      }
    } else {
      ;
    }
    t.height = max(height(t.left), height(t.right)) + 1;
    return t;
  }

  private Nodes rotateWithLeftChild(Nodes k2) {
    Nodes k1 = k2.left;
    k2.left = k1.right;
    k1.right = k2;
    k2.height = max(height(k2.left), height(k2.right)) + 1;
    k1.height = max(height(k1.left), k2.height) + 1;
    return k1;
  }

  private Nodes rotateWithRightChild(Nodes k1) {
    Nodes k2 = k1.right;
    k1.right = k2.left;
    k2.left = k1;
    k1.height = max(height(k1.left), height(k1.right)) + 1;
    k2.height = max(height(k2.right), k1.height) + 1;
    return k2;
  }

  private Nodes doubleWithLeftChild(Nodes k3) {
    k3.left = rotateWithRightChild(k3.left);
    return rotateWithLeftChild(k3);
  }

  private Nodes doubleWithRightChild(Nodes k1) {
    k1.right = rotateWithLeftChild(k1.right);
    return rotateWithRightChild(k1);
  }


  public void preorder() {

    try {
      preorder(root);

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  private void preorder(Nodes r)
      throws NoSuchAlgorithmException {
    if (r != null) {
      if (r.data <= lessthan && r.data >= greaterthan) {
        //  System.out.println(
        //  "data " + r.data + "is less than" + lessthan + " and greater than " + greaterthan
        //   + " Adding in the mached tuples" + " tuple is " + r.tuple);
        writer.append(r.tuple + "\n");
        String hash = getHashOfTuple(r.tuple);
        if (machedTup.containsKey(hash)) {
          machedTup.get(hash).add(r.tuple);
          System.out.println("duplicate tuple" + r.tuple);
        } else {
          ArrayList<String> temp = new ArrayList<String>();
          temp.add(r.tuple);
          machedTup.put(hash, temp);
        }
      }
      preorder(r.left);
      preorder(r.right);
    }
  }


  public String getHashOfTuple(String s) throws NoSuchAlgorithmException {
    String encryptedString = null;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      messageDigest.update(s.getBytes());
      encryptedString = new String(messageDigest.digest());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return encryptedString;
  }

  @Override
  public void run() {
    System.out.println("new Thread started");
    try {
      writer = new PrintWriter(fileName, "UTF-8");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    preorder();
    writer.close();
  }

}

public class BalancedBTree {

  public static void main(String[] args) throws IOException, InterruptedException {
    Scanner scan = new Scanner(System.in);

    BalancingBinarySearchTree index_X_Tree = new BalancingBinarySearchTree("x");
    BalancingBinarySearchTree index_Y_Tree = new BalancingBinarySearchTree("y");
    BalancingBinarySearchTree index_Z_Tree = new BalancingBinarySearchTree("z");
    File file = new File("C:\\Users\\prash\\Downloads\\LA2_10.txt");
    BufferedReader br = new BufferedReader(new FileReader(file));

    String st;
    System.out.println("Indexing started \n");
    ArrayList<String> indexedTuple = new ArrayList<String>();
    int counter = 0;
    while ((st = br.readLine()) != null) {
      //  System.out.println(st.replace("(", "").split(",")[0]);
      String tuple = st.replace("(", "").replace(")", "");
      String index[] = st.replace("(", "").replace(")", "").split(",");
      index_X_Tree.insert(Double.parseDouble(index[0]), tuple.trim());//B-Tree for X index
      index_Y_Tree.insert(Double.parseDouble(index[1]), tuple.trim());//B-Tree for Y index
      index_Z_Tree.insert(Double.parseDouble(index[2]), tuple.trim());//B-Tree for Z index
      indexedTuple.add(tuple.trim());
      counter++;
    }
    System.out.println("Indexing done for  " + counter + " tuples");
    Scanner sc = new Scanner(System.in);
    System.out.println("for Query x1<X<x2 : please enter x1");
    index_X_Tree.greaterthan = sc.nextInt();
    System.out.println("For Query x1<X<x2 : Please enter x2");
    index_X_Tree.lessthan = sc.nextInt();
    System.out.println("for Query y1<Y<y2 : please enter y1");
    index_Y_Tree.greaterthan = sc.nextInt();
    System.out.println("For Query y1<Y<y2 : Please enter y2");
    index_Y_Tree.lessthan = sc.nextInt();
    System.out.println("for Query z1<Z<z2 : please enter z1");
    index_Z_Tree.greaterthan = sc.nextInt();
    System.out.println("For Query z1<Z<z2 : Please enter z2");
    index_Z_Tree.lessthan = sc.nextInt();

    index_X_Tree.writer = new PrintWriter("x", "UTF-8");
    index_Y_Tree.writer = new PrintWriter("y", "UTF-8");
    index_Z_Tree.writer = new PrintWriter("z", "UTF-8");
    System.out.println("Searching For matched tuples");
    long startTime = System.nanoTime();
    index_X_Tree.preorder();
    index_Y_Tree.preorder();
    index_Z_Tree.preorder();
    long endTime = System.nanoTime();
    long totalTime = endTime - startTime;
    System.out.println("Total Time " + totalTime + "nano seconds");

    /*System.out.println(finalTuples.size());
    for (String tuple : index_X_Tree.machedTuples.subList(0, index_X_Tree.machedTuples.size() / 3)) {
      if (index_Y_Tree.machedTuples.contains(tuple) && index_Z_Tree.machedTuples.contains(tuple)) {
        finalTuples.add(tuple);
      }
    }*/
    index_X_Tree.machedTup.keySet().retainAll(index_Y_Tree.machedTup.keySet());
    index_X_Tree.machedTup.keySet().retainAll(index_Z_Tree.machedTup.keySet());
    System.out.println("Total Matched Tuples" + index_X_Tree.machedTup.values().size());
    ArrayList<String> finalTuples = new ArrayList<String>();
    for (Map.Entry<String, List<String>> entry : index_X_Tree.machedTup.entrySet()) {

      finalTuples.addAll(entry.getValue());
    }

    //System.out.println(index_X_Tree.machedTuples);
    //  System.out.println(index_X_Tree.machedTuples.retainAll(index_Y_Tree.machedTuples));
    //  System.out.println(index_X_Tree.machedTuples.retainAll(index_Z_Tree.machedTuples));
    //System.out.println(index_X_Tree.machedTuples);
    String formattedValues = String.join("\n", finalTuples)
        .replaceAll("(\\w*,\\w*,\\w*,)", "$1" + System.lineSeparator());
    //System.out.println(formattedValues);
    PrintWriter writer = new PrintWriter("FinalResult", "UTF-8");
    writer.write(formattedValues);
    System.out.println("Result is written in FinalResult.txt");
    writer.close();
    scan.close();
  }


}