import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Subset {

  double k;       // number of basic terms in this subset
  double p;       // product of selectivities of all terms in this subset
  double r;       // cost of accessing rj[i]
  double t;       // cost of performing if test
  double l;       // cost of performing &
  double m;       // cost of branch misprediction
  double a;       // cost of writing answer to answer array and incrementing array counter 
  double f;       // cost of applying a condition, constant for all f1...fk

  // TODO where exactly is this optimization applied?
  int b = 0;      // 1 if no-branch optimization used
  double c;       // current best cost for the subset
  double fcost;   // fixed cost of this subset

  Condition [] conditions;  // set of (column, selectivity) for conditions in this subset 

  Subset left;
  Subset right;

  /*
  Quote: We call the pair ((p−1)/fcost(E), p) the c-metric of &-term E
  having combined selectivity p.
   */
  CMetric cmetric; 
  
  /*
  Quote: We call the pair (fcost(E), p) the d-metric of &-term E
  having combined selectivity p.
  */
  DMetric dmetric; 

  /*
    conditions = the set of Condition objects (column, selectivity) that make up this subset
    r = cost of accessing rj[i]
    t = cost of performing if test
    l = cost of performing &
    m = cost of branch misprediction
    a = cost of writing answer to answer array and incrementing array counter
    f = array of costs of applying each of the conditions f1...fk    
  */
  public Subset(Condition [] conditions, int r, int t, int l, int m, int a, int f) {

    this.r = r;
    this.t = t;
    this.l = l;
    this.m = m;
    this.a = a;
    this.f = f;

    this.conditions = conditions;
    this.p = 1;
    for (int i=0; i < conditions.length; i++)
      p *= conditions[i].sel; 
    Collections.sort(Arrays.asList(this.conditions));

    this.k = conditions.length;

    this.fcost = calculateFcost();
    calculateInitialCost();
    calculateCandDMetrics();
    this.left = null;
    this.right = null;

    // System.out.println("p: " + p + ", subset: " + this.toString() + ", fcost: " + fcost + ", cost: " + c);

  }
  
  public boolean intersects(Subset other) {
    for (Condition c : this.conditions)
      for (Condition otherC : other.conditions)
        if (c.col == otherC.col)
          return true;
    return false;
  }
  
  private void calculateInitialCost() {
	  double noBranchCost = calculateNoBranchCost();
	  double oneBranchCost = logicalAndCost();
	  if(noBranchCost < oneBranchCost) {
		  this.b = 1;
      this.c = noBranchCost;
    }
    else {
      this.c = oneBranchCost; 
    }
  }

  /*
    Calculates and returns the fixed cost for this Subset 
    Quote:
    Let E be an &-term. The fixed cost of E, written fcost(E), to
    be the part of the cost of E that does not vary with the selectivity of E. In
    particular, if E contains k basic terms using f1 through fk, then fcost(E) =
    kr + (k − 1)l + f1 +···+ fk + t.
  */
  double calculateFcost() {
    double cost = 0;
    cost += k*r;          // reading k elements
    cost += (k-1)*l;      // k-1 logical and's
    cost += k*f;          // applying k condition functions
    cost += t;            // cost of if test
    return cost;
  }

  /*
    Calculates and returns the cost of this Subset assuming only &'s used.
    Quote:
    Consider Algorithm Logical-And on k basic terms, with
    selectivities p1,...,pk. The total cost for each iteration is
        kr + (k − 1)l + f1 + ... + fk + t + mq + p1...pk*a
    where q = p1...pk if p1...pk <= 0.5 and q = 1 - p1...pk otherwise.
    The q term describes the branch prediction behavior: we assume the system
    predicts the branch to the next iteration will be taken exactly when
    p1...pk <= 0.5.
  */
  double logicalAndCost() {
    double cost = fcost;    // start with the fixed cost of this subset
    if(p <= 0.5)				    // cost of branch misprediction
    	cost += m*p;
    else
    	cost += m*(1-p);
    cost += p*a;            // cost of writing answer 
    return cost;
  }
  
  /*
    Calculates and returns the cost of this Subset
    assuming no-branch optimization used.
    Quote:
    Consider Algorithm No-Branch on k basic terms. The total cost for each iteration: 
	      kr + (k - 1)l + f1 + ... + fk + a
	*/
  double calculateNoBranchCost() {
	  return k*r + (k-1)*l + f*k + a;
  }

  private void calculateCandDMetrics() {
	  this.cmetric  = new CMetric(p,(p-1)/fcost);
	  this.dmetric = new DMetric(p,fcost);
  }

  public Subset getLeftmostAndTerm() {
    Subset s = this;
    while (s.left != null)
      s = s.left;
    return s;
  }

  public Subset getRightmostAndTerm() {
    Subset s = this;
    while (s.right != null)
      s = s.right;
    return s;
  }

  public List<Subset> getAndTerms() {
    List<Subset> sAndTerms = new ArrayList<Subset>();
    getAndTermsInner(this, sAndTerms);
    return sAndTerms;
  }

  private void getAndTermsInner(Subset s, List<Subset> sAndTerms) {
    if (s.left == null && s.right == null) {
      sAndTerms.add(s); // s is a leaf
      return;
    }
    if (s.left != null)
      getAndTermsInner(s.left, sAndTerms);
    if (s.right != null)
      getAndTermsInner(s.right, sAndTerms); 
  }

  public String toString() {
    String s = "";

    if (left == null && right == null) {
    // the optimal plan is to use only &'s
      if (k > 1)
        s += "(";
      for (Condition c : conditions)
        s += ("t" + c.col + "[" + c + "]" + " & ");
      s = s.substring(0, s.length() - 3);   
      if (k > 1)
        s += ")";
    }

    // Note: it is not possible for only one of left and right to be null
    // since when they are updated, they are both updated together
    else {
      s = "(" + left.toString() + " && " + right.toString() + ")"; 
    }

    return s;
  }

}
