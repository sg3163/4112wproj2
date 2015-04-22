public class Condition implements Comparable<Condition>{

  public double sel;    // the selectivity of this condition
  public int col;       // the column to which this condition is applied

  public Condition(double sel, int col) {
    this.sel = sel;
    this.col = col;
  }

  @Override
  public String toString() {
    return "o" + col + "[i]";
  }

  @Override
  public int compareTo(Condition other) {
    // compareTo should return < 0 if this is supposed to be
    // less than other, > 0 if this is supposed to be greater than 
    // other and 0 if they are supposed to be equal
    return this.col - other.col;
  }
}
