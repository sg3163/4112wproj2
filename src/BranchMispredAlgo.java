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
	
	private static FileReader queryFile = null;
	
	private static FileReader configFile = null;
	
	// List of all selectivites from query.txt file
	// Length of this would give us the total input lines
	private static List<String> queryList = new ArrayList<String>();
	
	private static List<Subset []> subsetArrays = new ArrayList<Subset[]>();
	
	private static Map<String, Integer> configHashMap = new HashMap<String, Integer>();
	
	//List of Map. Each row in input will have one Map. Each map will contain the subset of array elements as key and Subset object as Value. 
	private static List<Map<String[], Subset>> subsetStringArrayMap = new ArrayList<Map<String[],Subset>>();

	public static void main(String[] args) throws IOException {
		if(args != null && args[0] != null) {
			if(args[0] != null) {
				System.out.println("Query file is - " + args[0]);
				queryFile = new FileReader(args[0]);
			}
			
			if(args[1] != null) {
				System.out.println("Config file is - " + args[1]);
				configFile = new FileReader(args[1]);	
				
			}
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
		
		/* Create an array of length 2 to the power of cardinality of selectivities in each row */
		for (String s : queryList) {
			String[] params = s.split(" ");
			
			int arrayLenght = (int) Math.pow(2, (params.length));
			
			Subset [] subsets = new Subset[arrayLenght];
			
			subsetArrays.add(subsets);
			
			List<List<String>> list =  powerset(Arrays.asList(params));
			List<String []> incOrderList = increasingOrderList(list, params.length);
			System.out.println("Completed");
			
		//	Map<String[], Subset> subsetMap = new HashMap<String[], Subset>();
		//	subsetStringArrayMap.add(subsetMap);
			
			List<Subset> subSets = new ArrayList<Subset>();
			
			Subset [] subsetArr = new Subset [incOrderList.size()];
			
			for(String [] strArr : incOrderList) {
				// Find selectivity product.
				double selectivityProduct = 1;
				for(int i=0;i<strArr.length;i++) {
					selectivityProduct *= Double.valueOf(strArr[i]);
				}
				Subset ss = new Subset(strArr.length, 
										strArr, 
										selectivityProduct,
										configHashMap.get("r"), 
										configHashMap.get("t"), 
										configHashMap.get("l"), 
										configHashMap.get("m"),
										configHashMap.get("a"),
										configHashMap.get("f")
										);
				
				subSets.add(ss);
				
				System.out.println("Cost of Subset - " + ss.c + " - is logicalAnd or noBranch - " + ss.b);
			}
			
			calculateOptimalPlan(subSets,params.length, subSets);

		}


/*		
    Algorithm for combining 
    Consider the plan P1 given by
      if (E && E1) {answer[j++] = i;},
    where E is an &-term and E1 is a nonempty expression. Then the cost of this plan is
      fcost(E) + mq + pC, (1)
    where p is the overall combined selectivity of E, q = min(p, 1 − p), and C is the
    cost of the plan P2:
      if (E1) {answer[j++] = i;}
*/

	}
	
	/*
	 // Method Implemention for below part
	  * 
	 	For each nonempty s in S (in increasing order)
		// s is the right child of an && in a plan //
		For each nonempty s prime in S (in increasing order) such that s intersection s prime is null set ; // s prime is the left child //
		if (the c-metric of s prime is dominated by the c-metric of the leftmost &-term in s) then
		// do nothing; suboptimal by Lemma 4.8 //
		else if (A[s prime].p <= 1/2 and the d-metric of s prime is dominated by the d-metric of some
		other &-term in s) then
		// do nothing; suboptimal by Lemma 4.9 //
		else f
		Calculate the cost c for the combined plan (s prime && s) using Eq. (1). If c < A[s prime union s].c
		then:
		(a) Replace A[s prime union s].c with c.
		(b) Replace A[s prime union s].L with s prime.
		(c) Replace A[s prime union s].R with s.
	 */
	public static void calculateOptimalPlan(List<Subset> subSets, int size, List<Subset> completeSet) {
		// Iterate to the times of size for s prime && s
		for(int i=0;i<size;i++) {
			List<Subset> sPrime;
			List<Subset> newSPrime = new ArrayList<Subset>();
			
			if(i==0) {
				sPrime = subSets;
			} else {
				sPrime = newSPrime;
			}
			
			// Start iterating with s prime
			// For the first iteration both s prime array and s array will be same
			for(int j=0;j<sPrime.size();j++) {
				
				Subset sPrimeSubSet = sPrime.get(j);
				
				for(int k=0;k<subSets.size();k++) {
					Subset sSubSet = subSets.get(k);
					
					// Check for s intersection s prime is null
					if(setIntersectionCheck(sPrimeSubSet, sSubSet)) {
						// if (the c-metric of s prime is dominated by the c-metric of the leftmost &-term in s) then
						// do nothing; suboptimal by Lemma 4.8 //
						if(checkForCMetric(sPrimeSubSet, sSubSet)) {
							// Sub-optimal
						}else if((sPrimeSubSet.p <= 0.5) && checkforDMetric(sPrimeSubSet, sSubSet)) {
							/*  A[s prime].p <= 1/2 and the d-metric of s prime is dominated by the d-metric of some
								other &-term in s) then
							 do nothing; suboptimal by Lemma 4.9 */
						} else {
							//Calculate the cost c for the combined plan (s prime && s)
							double cost = calculateBranchingCost(sPrimeSubSet, sSubSet);
							Subset s = findSubset(sPrimeSubSet, sSubSet,completeSet);
							
							if(s != null) {
								if(cost < s.c) {
									Subset newSubSet = new Subset(cost, sPrimeSubSet.selectivityArray, sSubSet.selectivityArray);
									newSPrime.add(newSubSet);
								}
							}
						}
					}
				}
			}
			
		}
	}
	
	
	public static void printOptimalPlan() {
		
	}
	
	public static <T> List<List<T>> powerset(Collection<T> list) {
	    List<List<T>> ps = new ArrayList<List<T>>();
	    ps.add(new ArrayList<T>());   // start with the empty set
	 
	    // for every item in the original list
	    for (T item : list) {
	      List<List<T>> newPs = new ArrayList<List<T>>();
	 
	      for (List<T> subset : ps) {
	        // copy all of the current powerset's subsets
	        newPs.add(subset);
	 
	        // plus the subsets appended with the current item
	        List<T> newSubset = new ArrayList<T>(subset);
	        newSubset.add(item);
	        newPs.add(newSubset);
	      }
	 
	      // powerset is now powerset of list.subList(0, list.indexOf(item)+1)
	      ps = newPs;
	    }
	    return ps;
	  }
	
	/* Method to Create List of arrays in increasing order
	 * Example [{0.2},{0.3},{0.2,0.3}]
	 */
	public static List<String []> increasingOrderList(List<List<String>> list, int size) {
		if(list != null) {
			List<String []> newList = new ArrayList<String[]>();
			for(int i=0;i<size;i++) {
				for(int j=0;j<list.size();j++) {
					List<String> subList = list.get(j);
					if(subList.size() == (i+1)) {
						String [] subArray = new String[i+1];
						newList.add(subList.toArray(subArray));
					}
				}
			}
			return newList;
			
		} else {
			return null;
		}
		
	}
	
	// TO-DO need to include L and R
	public static boolean setIntersectionCheck(Subset s1, Subset s2) {
		for (String a : s1.selectivityArray) {
			for(String b: s2.selectivityArray) {
				if (a.equals(b)) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Lemma 4.8
	public static boolean checkForCMetric(Subset s1, Subset s2) {
		if( (s2.getCmetric().p <= s1.getCmetric().p) && (s2.getCmetric().pfcost < s1.getCmetric().pfcost)) {
			return true;
		}
		return false;
	}
	
	// Lemma 4.9
	// TO-DO need to include L and R
	public static boolean checkforDMetric(Subset s1, Subset s2) {
		if((s2.getDmetric().p < s1.getDmetric().p) && (s2.getDmetric().fcost < s1.getDmetric().fcost)) {
			return true;
		}
		return false;
	}
	
	// TO-DO need to include L and R
	public static double calculateBranchingCost(Subset s1, Subset s2) {
		double totalCost = 2*s1.r + (2-1)* s1.l + (s1.f*s1.k + s2.f*s2.k) + s1.t;
		if((s1.p*s2.p)<= 0.5) {
			totalCost += s1.m*(s1.p*s2.p);
		} else {
			totalCost += s1.m*(1- (s1.p*s2.p));
		}
		totalCost += s1.a*(s1.p*s2.p);
		return totalCost;
	}
	
	// Find A[s prime union s]
	// TO-DO need to include L and R
	public static Subset findSubset(Subset s1, Subset s2, List<Subset> list) {
		
		List<String> combinedArray = Arrays.asList(s1.selectivityArray);
		
		for(String s: s2.selectivityArray) {
			combinedArray.add(s);
		}
		
		Collections.sort(combinedArray);
		String scombinedArrayString = null;
		for(String s: combinedArray) {
			scombinedArrayString += s;
		}
		
		for(Subset sub : list) {
			List<String> subArray = Arrays.asList(sub.selectivityArray);
			
			Collections.sort(subArray);
			
			String subArrayString = null;
			
			for(String s: subArray) {
				subArrayString += s;
			}
			
			if(subArrayString.equals(scombinedArrayString)) {
				return sub;
			}
			
		}
		
		return null;
	}
	
	
}
