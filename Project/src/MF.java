import java.util.*;


public class MF {
	Map<String,Map<String,Double>> r;
	Map<String,Map<String,Double>> ri;
	Set<String> users, items;
	Double mu;
	
	Map<String,Double> bu = new HashMap<String,Double>();
	Map<String,Double> bi = new HashMap<String,Double>();
	
	int K = 5;
	
	Map<String,Double[]> pu = new HashMap<String,Double[]>();
	Map<String,Double[]> qi = new HashMap<String,Double[]>();
	
	Double gamma = 0.005;
	Double lambda_bu = 0.02;
	Double lambda_bi = 0.02;
	Double lambda_pu = 0.05;
	Double lambda_qi = 0.05;
	
	int iternum = 35;
	
	
	public void buildRecommender(Map<String,Map<String,Double>> r) {
		this.r = r;
		initialize();
		this.mu = Util.computeMu(r); // Checkpoint: mu - 0.5
		computeBuBiPuQi();
	}
	
	//predict rating
	public Double r_hat(String u, String i) {
		Double bu_u = 0.0, bi_i = 0.0, pu_qi=0.0;
		if ( bu.containsKey(u) ) bu_u = bu.get(u);			
		if ( bi.containsKey(i) ) bi_i = bi.get(i);
		if ( pu.containsKey(u) && qi.containsKey(i) ) 
			pu_qi = Util.vecdotprod(pu.get(u), qi.get(i));
		
		return mu+bu_u+bi_i+pu_qi;
	}
	
	void initialize() {
		this.items = Util.r_u_i_TO_i(r);
		this.users = Util.r_u_i_TO_u(r);
		
		for(String u : users) bu.put(u, 0.0);
				
		for(String i : items) bi.put(i, 0.0);
		
		for(String u : users) {
			Double[] vec = new Double[K];
			for(int k=0; k<K; k++) 
				vec[k] = Math.random()/100.0;
			pu.put(u, vec);
		}
		
		for(String i : items) {
			Double[] vec = new Double[K];
			for(int k=0; k<K; k++) 
				vec[k] = Math.random()/100.0;
			qi.put(i, vec);
		}
	}
	
	
	void computeBuBiPuQi() {
		for(int iter=0; iter<this.iternum; iter++) {

			for(String u : r.keySet()) {
				for(String i : r.get(u).keySet()) {
					Double e_ui = e(u,i);				// Calculating Error
					
					Double b_u = bu.get(u);
					Double b_i = bi.get(i);
					Double[] p_u = pu.get(u);
					Double[] q_i = qi.get(i);
					
					bu.put(u, b_u + gamma*(e_ui-lambda_bu*b_u));
					bi.put(i, b_i + gamma*(e_ui-lambda_bi*b_i));
					pu.put(u, Util.vecvecsum(p_u, Util.scalarvecprod(gamma, Util.vecvecsum(
																		Util.scalarvecprod(e_ui, q_i), 
																		Util.scalarvecprod(-lambda_pu, p_u)))));
					qi.put(i, Util.vecvecsum(q_i, Util.scalarvecprod(gamma, Util.vecvecsum(
							   											Util.scalarvecprod(e_ui, p_u), 
							   											Util.scalarvecprod(-lambda_qi, q_i)))));
				}
			}
		}
	}
	
	int getF() {return K;}
	void setF(int f) {K = f;}

	Double e(String u, String i) {
		return r.get(u).get(i) - r_hat(u,i);
	}
	
	Double getLambda_bu() {return lambda_bu;}
	void setLambda_bu(Double lambda_bu) {this.lambda_bu = lambda_bu;}

	Double getLambda_bi() {return lambda_bi;}
	void setLambda_bi(Double lambda_bi) {this.lambda_bi = lambda_bi;}

	Double getLambda_pu() {return lambda_pu;}
	void setLambda_pu(Double lambda_pu) {this.lambda_pu = lambda_pu;}

	Double getLambda_qi() {return lambda_qi;}
	void setLambda_qi(Double lambda_qi) {this.lambda_qi = lambda_qi;}

	int getIternum() {return iternum;}
	void setIternum(int iternum) {this.iternum = iternum;}

	////////////// MAIN /////////////////////////////////////////////
	public static void main(String[] args) throws Exception {
		String datafile = "Movies_&_TV.txt";
			
		Map<String,Map<String,Double>> r = Util.readDataUserItem(datafile);
		int count = Util.countSingleRevUsers(r);
		System.out.println(count);
		Map<String,Map<String,Double>> test = Util.extractTest(r, 50.0);
		count = Util.countSingleRevUsers(r);
		System.out.println(count);
		
		MF mf = new MF();
		
		System.out.println("RMSE="+Util.RMSE(mf,r,test));
		
		
	}

}
