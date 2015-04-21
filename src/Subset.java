import javax.annotation.PostConstruct;

public class Subset {

  /*
    Quoted directly from the paper:
    The array elements are records containing the following:
    - The number k of basic terms in the corresponding subset
    - the product p of the selectivities of all terms in the subset
    - a bit b determining whether the no-branch optimization was used to get
      the best cost, initialized to 0
    - the current best cost c for the subset
    - the left child L and right child R of the subplans giving the best cost.
      L and R range over indexes for A[], and are initialized to ∅.
  */

  int k;          // number of basic terms in this subset
  double p;   // product of selectivities of all terms in this subset
  int b = 0;      // 1 if no-branch optimization used
  double c;       // current best cost for the subset
  String [] L;    // left child subplan array
  String [] R;    // right child subplan array

  double r;       // cost of accessing rj[i]
  double t;       // cost of performing if test
  double l;       // cost of performing &
  double m;       // cost of branch misprediction
  double a;       // cost of writing answer to answer array and incrementing array counter 
  double f;     // array of costs of applying conditions f1...fk - constant for all functions, passed from config
  String [] selectivityArray; 
  
  /*
  cmetric for this Subset 

  Quote:
  We call the pair ((p−1)/fcost(E), p) the c-metric of &-term E having
  combined selectivity p.
   */
  private CMetric cmetric; 
  
  /*
  Calculates and returns the dmetric for this Subset 

  Quote:
  We call the pair (fcost(E), p) the d-metric of &-term E
  having combined selectivity p.
*/
  private DMetric dmetric; 

  /*
    k = the number of basic terms in this subset
    r = cost of accessing rj[i]
    t = cost of performing if test
    l = cost of performing &
    m = cost of branch misprediction
    a = cost of writing answer to answer array and incrementing array counter
    f = array of costs of applying each of the conditions f1...fk    
    p = product of selectivities of terms in this subset
  */
  public Subset(int k, String[] selectivityArray, double p, int r, int t, int l, int m, int a, int f) {
    assert selectivityArray.length == k;
    this.k = k;
    this.selectivityArray = selectivityArray;
    this.f = f;
    this.r = r;
    this.t = t;
    this.l = l;
    this.m = m;
    this.a = a;
    this.p = p;
    calculateInitialCost();
    calculateCandDMetrics();
  }
  
  public Subset(double cost, String[] L, String[] R) {
	  this.c = cost;
	  this.L = L;
	  this.R = R;	  
  }
  
  private void calculateInitialCost() {
	  double noBranchCost = calculateNoBranchCost();
	  double oneBranchCost = logicalAndCost();
	  if(noBranchCost < oneBranchCost) {
		  this.c = noBranchCost;
		  this.b = 1;
	  }else {
		  this.c = oneBranchCost;
	  }
  }
  
  private void calculateCandDMetrics() {
	  CMetric cmetric = new CMetric(p,(p-1)/c);
	  this.cmetric = cmetric;
	  
	  DMetric dmetric = new DMetric(c,p);
	  this.dmetric = dmetric;
  }

  /*
    Calculates and returns the fixed cost for this Subset 

    Quote:
    Let E be an &-term. The fixed cost of E, written fcost(E), to
    be the part of the cost of E that does not vary with the selectivity of E. In
    particular, if E contains k basic terms using f1 through fk, then fcost(E) =
    kr + (k − 1)l + f1 +···+ fk + t.
  */
  double fcost() {
    double cost = 0;
    return cost;
  }


  public CMetric getCmetric() {
	  return cmetric;
  }

  
  public DMetric getDmetric() {
	  return dmetric;
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
    double totalCost = 0;
    totalCost += k*r;            // cost of reading k elements
    totalCost += (k-1)*l;        // cost of performing k-1 &'s
    totalCost += f*k;     		 // cost of applying conditions f1...fk  
    totalCost += + t;            // cost of performing if test
    if(p <= 0.5) {				 // cost of branch misprediction and writing answer
    	totalCost += m*p*a;
    }else {
    	totalCost += m*(1-p)*a;
    }
                
    return totalCost;
  }
  
  /* Calculates the no-branch cost
     The total
	 cost for each iteration is kr + (k - 1)l + f1 + ... + fk + a
	*/
  double calculateNoBranchCost() {
	  double totalCost = k * r + (k-1) * l + f * k + a;
	  return totalCost;
  }

}
