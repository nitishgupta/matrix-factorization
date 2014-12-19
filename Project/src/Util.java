import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


public class Util {
	
	public static Map<String,Map<String,Double>> readDataUserItem(String filename) throws Exception {
		Map<String,Map<String,Double>> r = new HashMap<String,Map<String,Double>>();
		System.out.println("Reading file " + filename + " ...");
		BufferedReader br = new BufferedReader( new FileReader(filename) );
		String line;
		int i = 0;
		int flag = 0;
		String item =null, user = null;
		Double rating = 0.0;
		int count = 0;
		while ( (line = br.readLine()) != null )  {
			//System.out.println("Reading line: " + line);
			String[] array = line.split(":");
			
			if (array.length == 1) continue;
			if (array[0].equals("product/productId")) item = array[1].trim();
			
			if (array[0].equals("review/userId")) user = array[1].trim();
						
			if (array[0].equals("review/score")){
				rating = Double.parseDouble(array[1]);
				flag = 1;
			}
			
			if (flag == 1){
				count ++;
				if( !r.containsKey(user) ) r.put(user, new HashMap<String,Double>());
				r.get(user).put(item,rating);
				flag = 0;
			}
		}
		
		System.out.println("End of reading file " + filename);
		System.out.print(count);
		System.out.print(" entries read.");
		return r;
	}
	
	public static Map<String,Map<String,Double>> extractTest(Map<String,Map<String,Double>> r, Double pct) {
		Map<String,Map<String,Double>> test = new HashMap<String,Map<String,Double>>();
		
		int count=0;
		for(String u : r.keySet()) {
			int s = r.get(u).keySet().size();
			if(s == 1)
				continue;
			
			for(String i : r.get(u).keySet()) {
				
				if(s == 1)
					continue;
				if( Math.random() <= pct/100.0 ) {
					if( !test.containsKey(u) ) 
						test.put(u, new HashMap<String,Double>());
					test.get(u).put(i, r.get(u).get(i));
					s--;
					count++;
				}
			}
		}
		
		System.out.println("Removing test ratings from r...");
		for(String u : test.keySet()) {
			for(String i : test.get(u).keySet()) {
				if( !r.get(u).containsKey(i) ) 
					continue;
				r.get(u).remove(i);				
			}
		}
		
		System.out.println(count + " ratings were extracted for test");
		return test;
	}
	
	public static Double vecdotprod(Double[] p, Double[] q) {
		Double pq = 0.0;
		for(int k=0; k<p.length; k++) 
			pq += p[k]*q[k];
		
		return pq;
	}
	
	
	public static int countSingleRevUsers(Map<String, Map<String, Double>> ri){
		int count=0;
		int pc = 0;
		for (String u: ri.keySet()){
			for(String i : ri.get(u).keySet()) {
				pc++;
			}
			if(pc == 0) {
				count++;
				//System.out.println(u);
			}
			pc = 0;
		}
		return count;
	}
	
	public static Double RMSE(MF mf, Map<String,Map<String,Double>> r, Map<String,Map<String,Double>> test) {
		
		System.out.println("Building the recommender...");
		mf.buildRecommender(r);
		System.out.println("Finished building the recommender...");
		
		System.out.println("Computing RMSE and MAE...");
		Double RMSEsum = 0.0;

		int count = 0;
		
		for(String u : test.keySet()) {
			for(String i : test.get(u).keySet()) {
				double r_ui = test.get(u).get(i); 
				double r_pred = mf.r_hat(u, i);
				
				RMSEsum += Math.pow( r_ui - r_pred, 2.0 );
				count++;	
			}
		}		
		System.out.println("Done with this test set : " + count);
	
		return RMSEsum/count ;
		//return Math.sqrt(RMSEsum/count);
	}
	
	public static Double computeMu(Map<String,Map<String,Double>> r) {
		Double sum = 0.0;
		int count = 0;
		for(String u : r.keySet()) {
			for(String i : r.get(u).keySet()) {
				sum += r.get(u).get(i);
				count++;
			}
		}
		
		return sum/count;
	}
		
	public static Double[] scalarvecprod(Double a, Double[] p) {
		Double[] q = new Double[p.length];
		for(int f=0; f<p.length; f++) 
			q[f] = a*p[f];
		
		return q;
	}
		
	public static Double[] vecvecsum(Double[] p, Double[] q) {
		Double[] p_plus_q = new Double[p.length];
		for(int f=0; f<p.length; f++) 
			p_plus_q[f] = q[f] + p[f];
		
		return p_plus_q;
	}
	
	public static Double[] vecvecminus(Double[] p, Double[] q) {
		Double[] p_plus_q = new Double[p.length];
		for(int f=0; f<p.length; f++) 
			p_plus_q[f] = q[f] - p[f];
		
		return p_plus_q;
	}	
	
	public static Set<String> r_u_i_TO_i(Map<String,Map<String,Double>> r) {
		Set<String> items = new HashSet<String>();
		
		for(String u : r.keySet())
			for(String i : r.get(u).keySet()) 
				items.add(i);
		
		return items;
	}	

	public static Set<String> r_u_i_TO_u(Map<String,Map<String,Double>> r) {
		return r.keySet();
	}	
	
}
