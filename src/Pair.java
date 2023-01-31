package src.build;

import java.util.ArrayList;
import java.util.List;

public class Pair {

    private String left;
    private List<String> right;
  
    public Pair(String left, List<String> right) {
      assert left != null;
      assert right != null;
      this.left = left;
      this.right = new ArrayList<>(right);
    }

    public Pair() {
      this.left = "";
      this.right = new ArrayList<>();
    }
  
    public String getLeft() { 
      return this.left; 
    }
    public List<String> getRight() { 
      return this.right; 
    }
  
    /**
     * Returns a new copy of this Pair.
     */
    @Override
    public Pair clone() {
      Pair p = new Pair();
      p.left = left;
      p.right = new ArrayList<>(right);
      return p;
    }

    /**
     * Checks whether an object is equal to this Pair.
     */
    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Pair)) return false;
      Pair pairo = (Pair) o;
      return this.left.equals(pairo.getLeft()) &&
             this.right.containsAll(pairo.getRight()) && pairo.right.containsAll(this.right);
    }
  }