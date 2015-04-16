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
		

	}

}
