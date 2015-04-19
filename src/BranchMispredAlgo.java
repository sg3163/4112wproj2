import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BranchMispredAlgo {
	
	private static FileReader queryFile = null;
	
	private static FileReader configFile = null;
	
	private static List<String> queryList = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		if(args != null && args[0] != null) {
			if(args[0] != null) {
				System.out.println("Query file is - " + args[0]);
				queryFile = new FileReader(args[0]);
			}
			
			if(args[1] != null) {
				configFile = new FileReader(args[1]);
				System.out.println("Config file is - " + args[1]);
			}
		}
		
		// Read input file and add selectivities to Collection
		if(queryFile != null) {
			BufferedReader br = new BufferedReader(queryFile);
			for(String line; (line = br.readLine()) != null; ) {
				queryList.add(line);

		    }
		}
		
		for (String s : queryList) {
			String[] params = s.split(" ");
			for(String a: params) {
				System.out.println(a);
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

}
