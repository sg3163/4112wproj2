public class CMetric implements Comparable<CMetric>{

  double p;
  double pfcost;

  public CMetric(double p, double pfcost) {
    this.p = p;
    this.pfcost = pfcost;
  }

  @Override
  public int compareTo(CMetric other){
    // compareTo should return < 0 if this is supposed to be
    // less than other, > 0 if this is supposed to be greater than 
    // other and 0 if they are supposed to be equal
    return 0;
  }

}
