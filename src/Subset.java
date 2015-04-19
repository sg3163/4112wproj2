public class Subset {

  /*
    Quoted directly from the paper:
    The array elements are records containing the following:
    - The number n of basic terms in the corresponding subset
    - the product p of the selectivities of all terms in the subset
    - a bit b determining whether the no-branch optimization was used to get
      the best cost, initialized to 0
    - the current best cost c for the subset
    - the left child L and right child R of the subplans giving the best cost.
      L and R range over indexes for A[], and are initialized to ∅.
  */

  int n;      // number of basic terms in this subset
  double p = 1;   // product of selectivities of all terms in this subset
  boolean b;  // 1 if no-branch optimization used
  double c;   // current best cost for the subset
  int L;      // left child of subplan giving best cost; index of 2^k array
  int R;      // right child of subplan giving best cost; index of 2^k arrray
  DMetric d;

  /*
    n is the number of basic terms in this subset
    parray is an array of size n containing the selectivity of each term
  */
  public Subset(int n, int[] parray) {
    assert parray.length == n;
    this.n = n;
    for(i=0; i<parray.length; i++) {
      p = p * parray[i];
  }

  /*
    Calculates and returns the fixed cost for this Subset 
    Let E be an &-term. The fixed cost of E, written fcost(E), to
    be the part of the cost of E that does not vary with the selectivity of E. In
    particular, if E contains k basic terms using f1 through fk, then fcost(E) =
    kr + (k − 1)l + f1 +···+ fk + t.
  */
  double fcost() {

  }

  /*
    Calculates and returns the cmetric for this Subset 
    We call the pair ((p−1)/fcost(E), p) the c-metric of &-term E having
    combined selectivity p.
  */
  Cmetric cmetric() {

  }

  /*
    Calculates and returns the dmetric for this Subset 
    We call the pair (fcost(E), p) the d-metric of &-term E
    having combined selectivity p.
  */
  Dmetric dmetric() {

  }

  /*
    Calculates and returns the cost of this Subset, while also updating
    this Subset's cost field.
    Consider Algorithm Logical-And on k basic terms, with
    selectivities p1, ... , pk. The total cost for each iteration is kr + (k − 1)l+
    f1 +··· + fk + t + mq + p1 ··· pka, where q = p1 ··· pk if p1 ··· pk ≤ 0.5 and
    q = 1 − p1 ··· pk otherwise. The q term describes the branch prediction behavior:
    we assume the system predicts the branch to the next iteration will be
    taken exactly when p1 ··· pk ≤ 0.5.
  */
  double cost() {

  }

}
