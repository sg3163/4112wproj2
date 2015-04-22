import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchMispredAlgo {
	
	private static List<String> queryList = new ArrayList<String>();
	private static Map<String, Integer> configHashMap = new HashMap<String, Integer>();
	private static List<Subset []> subsetArrays = new ArrayList<Subset[]>();

	public static void main(String[] args) throws IOException {
	
    // check for query file and config file as arguments
    FileReader queryFile = null;
    FileReader configFile = null;
		if(args != null && args[0] != null) {
			if(args[0] != null)
				queryFile = new FileReader(args[0]);
			if(args[1] != null)
				configFile = new FileReader(args[1]);	
		}
    else {
      System.out.println("Usage: java BranchMispredAlgo query.text config.text");
      return;
    }		

		// Read input file and add selectivities to Collection
		if(queryFile != null) {
			BufferedReader br = new BufferedReader(queryFile);
			for(String line; (line = br.readLine()) != null; ) {
				queryList.add(line);
		    }
		}
		
		// Read input config file and create an Hashmap of input parameters
		if(configFile != null) {
			BufferedReader br = new BufferedReader(configFile);
			for(String line; (line = br.readLine()) != null; ) {
				String[] arr = line.split(" = ");
				configHashMap.put(arr[0], Integer.valueOf(arr[1]));
		    }
		}
		
		// Create an array of length 2^(cardinality of selectivities in each row)
		for (String s : queryList) {

      // create a set of conditions on columns from the input
			String[] selectivities = s.split(" ");
      List<Condition> conditions = new ArrayList<Condition>();
      for (int i=0; i<selectivities.length; i++)
        conditions.add(new Condition(Double.parseDouble(selectivities[i]), i+1));
		
      // calculate the power set of the conditions	
			List<List<Condition>> powerSet =  getPowerSet(conditions);
			List<Condition []> orderedPowerSet = orderPowerSet(powerSet, selectivities.length);

      // create a Subset object for each set of conditions in the power set
			List<Subset> subsets = new ArrayList<Subset>();
			for(Condition [] subsetConditions : orderedPowerSet) {
				subsets.add(new Subset(subsetConditions,
										configHashMap.get("r"), 
										configHashMap.get("t"), 
										configHashMap.get("l"), 
										configHashMap.get("m"),
										configHashMap.get("a"),
										configHashMap.get("f")
									 ));
			}

			calculateOptimalPlan(subsets);
      printOptimalPlan(subsets, s);
		}
	  System.out.println("==================================================================");
	}

  /* Stage 2 of Algorithm 4.11 */	
	public static void calculateOptimalPlan(List<Subset> subsets) {

    // For each nonempty s in S (in increasing order)
    // s is the right child of an && in a plan
    for (int i=0; i<subsets.size(); i++) {
      Subset s = subsets.get(i);

      // For each nonempty sPrime in S (in increasing order) such that s intersection sPrime is null
      // sPrime is the left child of the &&
      for (int j=0; j<subsets.size(); j++) {
        Subset sPrime = subsets.get(j);
        if (s.intersects(sPrime))
          continue;

        // System.out.println("testing " + sPrime + " && " + s);

        if (sDominatesSPrimeCMetric(sPrime, s)) {
          // do nothing, suboptimal by Lemma 4.8    
          // System.out.println("s dominates s' in c-metric");
          continue;
        }
        else if (sPrime.p <= 0.5 && sDominatesSPrimeDMetric(sPrime, s)) {
          // do nothing, suboptimal by Lemma 4.9
          // System.out.println("s dominates s' in d-metric");
          continue;
        }
        else {
          /*
            Calculate the cost c of (sPrime && s) (the "branching cost")
            If c < A[sPrime union s].c then:
              (a) Replace A[sPrime union s].c with c.
              (b) Replace A[sPrime union s].L with sPrime.
              (c) Replace A[sPrime union s].R with s.
          */
          double c = branchingCost(sPrime, s); 
    //      System.out.println("branching cost: " + c); 
          Subset sUnionSPrime = findSubset(sPrime, s, subsets); 
          if (c < sUnionSPrime.c) { 
            sUnionSPrime.c = c;
            sUnionSPrime.left = sPrime;
            sUnionSPrime.right = s; 
          }
        }
      }
    }
  }
	public static void printOptimalPlan(List<Subset> subsets, String selectivities) {

    System.out.println("==================================================================");
    System.out.println(selectivities);
    System.out.println("------------------------------------------------------------------");
    Subset top = subsets.get(subsets.size()-1);
    System.out.print("if ");
    System.out.print(top);
    System.out.print(" {\n");
    if (top.b == 1) {
      Subset rightmost = top.getRightmostAndTerm();
      System.out.println("\tanswer[j] = i;");
      System.out.println("\tj += " + rightmost + ";");
    }
    else {
      System.out.println("\tanswer[j++] = i;");
    }
    System.out.println("}");
    System.out.println("------------------------------------------------------------------");
    System.out.println("cost: " + subsets.get(subsets.size()-1).c);
	}
	
	public static <T> List<List<T>> getPowerSet(Collection<T> list) {
	    List<List<T>> powerSet = new ArrayList<List<T>>();
      // start with the empty set
	    powerSet.add(new ArrayList<T>()); 
	    // for every item in the original list
	    for (T item : list) {
	      List<List<T>> newPs = new ArrayList<List<T>>();
	      for (List<T> subset : powerSet) {
	        // copy all of the current getPowerSet's subsets
	        newPs.add(subset);
	        // plus the subsets appended with the current item
	        List<T> newSubset = new ArrayList<T>(subset);
	        newSubset.add(item);
	        newPs.add(newSubset);
	      }
	      // getPowerSet is now powerset of list.subList(0, list.indexOf(item)+1)
	      powerSet = newPs;
	    }
	    return powerSet;
	  }
	
	/* 
    Method to Create List of arrays in increasing order
	  Example [{0.2},{0.3},{0.2,0.3}]
	*/
	public static List<Condition []> orderPowerSet(List<List<Condition>> powerSet, int setSize) {
    List<Condition []> newList = new ArrayList<Condition[]>();
    for(int i=0;i<setSize;i++) {
      for(int j=0;j<powerSet.size();j++) {
        List<Condition> subList = powerSet.get(j);
        if(subList.size() == (i+1)) {
          Condition [] subArray = new Condition[i+1];
          newList.add(subList.toArray(subArray));
        }
      }
    }
    return newList;
	}
	
	/*
    Lemma 4.8
    Let sLeft be the leftmost & term in s
    s dominates sPrime if sLeft.p <= sPrime.p and sLeft.pfcost < sPrime.pfcost
  */
	public static boolean sDominatesSPrimeCMetric(Subset sPrime, Subset s) {
    Subset sLeft = s.getLeftmostAndTerm();
		if( (sLeft.cmetric.p <= sPrime.cmetric.p) &&
        (sLeft.cmetric.pfcost < sPrime.cmetric.pfcost) )
			return true;
		return false;
	}
	
  /*
    Lemma 4.9
    For any &-term in s, call it sAndTerm
    s dominates sPrime if sAndTerm.p < sPrime.p and sAndTerm.fcost < sPrime.fcost 
  */
	public static boolean sDominatesSPrimeDMetric(Subset sPrime, Subset s) {
    List<Subset> sAndTerms = s.getAndTerms();
    for (Subset sAndTerm : sAndTerms)
      if( (sAndTerm.dmetric.p < sPrime.dmetric.p) &&
          (sAndTerm.dmetric.fcost < sPrime.dmetric.fcost) )
        return true;
    return false;
	}


  /*
    Calculate the cost c for the combined plan (sPrime && s) as:
        fcost(sPrime) + m*q + p*C
    where p = the overall combined selectivity of sPrime
          q = min(p, 1-p)
          C = cost of s
  */
	public static double branchingCost(Subset sPrime, Subset s) {
    double q = Math.min(sPrime.p, 1 - sPrime.p); 
    // System.out.println(sPrime.fcost + " + " + sPrime.m + "*" + q + " + " + sPrime.p + "*" + s.c);
    return sPrime.fcost + (sPrime.m)*q + (sPrime.p)*(s.c);
	}
	
	/*
     Find A[sPrime union s]
  */
	public static Subset findSubset(Subset s1, Subset s2, List<Subset> list) {
    // created a standardized representation of s1 U s2
		List<Condition> combined = new ArrayList<Condition>();
    combined.addAll(Arrays.asList(s1.conditions));
    combined.addAll(Arrays.asList(s2.conditions));
		Collections.sort(combined);
    // find A[s1 U s2] by comparing standardized representations
    // (the conditions of Subset objects are already in sorted order) 
		for (Subset s : list)
			if(combined.toString().equals(Arrays.asList(s.conditions).toString()))
				return s;
		return null;
	}
	
}
