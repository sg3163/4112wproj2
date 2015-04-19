import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
			
			// called for each line
			// For each Array of subsets I'm planning to call something like new Subset(1, arr[0],r,t,l,m from config.txt)
			
			Subset [] subsetArr = new Subset [incOrderList.size()];
			
			for(String [] strArr : incOrderList) {
				// Find selectivity product.
				double selectivityProduct = 1;
				for(int i=0;i<strArr.length;i++) {
					selectivityProduct *= Double.valueOf(strArr[i]);
				}
				System.out.println(strArr.length + " - " + strArr.toString() + " - " + selectivityProduct + " - " + configHashMap.get("r") + " - " + configHashMap.get("t"));
		//		Subset ss = new Subset(strArr.length, strArr, configHashMap.get("r"), configHashMap.get("t"), selectivityProduct);
			}

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
}
