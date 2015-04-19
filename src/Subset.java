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
  double p;   // product of selectivities of all terms in this subset
  boolean b;  // 1 if no-branch optimization used
  double c;   // current best cost for the subset
  int L;      // left child of subplan giving best cost; index of 2^k array
  int R;      // right child of subplan giving best cost; index of 2^k arrray

  public Subset() {

  }

  double cmetric(Subset s) {

  }

  /*
    calculates and returns the dmetric of a Subset
  */
  double dmetric(Subset s) {

  }


  /*
    Consider Algorithm Logical-And on k basic terms, with
    selectivities p1, ... , pk. The total cost for each iteration is kr + (k − 1)l+
    f1 +··· + fk + t + mq + p1 ··· pka, where q = p1 ··· pk if p1 ··· pk ≤ 0.5 and
    q = 1 − p1 ··· pk otherwise. The q term describes the branch prediction behavior:
    we assume the system predicts the branch to the next iteration will be
    taken exactly when p1 ··· pk ≤ 0.5.
  */
  double cost() {

  }

  double costNoBranch() {

  }

}
