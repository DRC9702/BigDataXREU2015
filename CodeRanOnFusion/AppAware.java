import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class AppAware {

//	public static void main(String[] args){
//		File f =  new File("C:\\Users\\REU2015\\Desktop\\SamInputs\\InputN10M4K4Num4.txt");
//		readInputMakeOutput(f,10,4,4,4);
//		
//	}
	
	public static void readInputMakeOutput(File f, int n, int m, int k, int t, String filePath){
		try {
			String content = solve(f);
//			System.out.println(content);
			String filename = filePath + "/AAOutputN" + n + "M" + m + "K" + k + "Num" + t + ".txt"; 
			File file = new File(filename);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

//			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String solve(File file){
		
		try{
			
			long startTime = System.currentTimeMillis();
			Scanner input = new Scanner(file);

			//input n
			int n = input.nextInt();
			//input loads
			int loads[] = new int[n];
			for(int i = 0; i <n; i++){
				loads[i] = input.nextInt();
			}
			//input traffic
			int VMTraffic[][] = new int[n][n];
			for(int i = 0; i<n; i++){
				for(int j=0; j<n; j++){
					VMTraffic[i][j] = input.nextInt();
				}
			}
			//input m
			int m = input.nextInt();
			//input capacities
			int capacities[] = new int[m];
			for(int i = 0; i <m; i++){
				capacities[i] = input.nextInt();
			}
			//input distances
			int PMDistances[][] = new int[m][m];
			for(int i = 0; i<m; i++){
				for(int j=0; j<m; j++){
					PMDistances[i][j] = input.nextInt();
				}
			}
			//input k
			int k = input.nextInt();
			//input pre-assigned labels
			int preLabels[] = new int[n];
			for(int i =0; i<n; i++){
				preLabels[i] = input.nextInt();
			}
			//Closing Input
			input.close();
			//Create the labels the the algorithm will be manipulating
			int currLabels[] = new int[n];
			for(int i =0; i<n; i++){
				currLabels[i] = preLabels[i];
			}
			

			//Creating the set of Vi in O
			//I wanted to make this all array base, but that was painful. Sorry.
			Set<Integer> O = new HashSet<Integer>();
			for(int i=0; i<k; i++){
				O.add(i);
			}

			Map<Integer, Integer> TW = new HashMap<Integer, Integer>();
			for(int i : O){
				int sum=0;
				for(int j=0; j<n; j++){
					sum += VMTraffic[i][j];
				}
				TW.put(i, sum);
			}

			//Sorting O in decreasing order of TW
			TW = sortByComparator(TW);


			//Making migration set (I'm trying to avoid using sets and use arrays to minimize overhead)
			//				int M[]= new int[0]; //M is empty
			Map<Integer, Integer> M = new HashMap<Integer, Integer>();

			//for each Vi in O
			
			reportAllServerLoads(loads, capacities, O, currLabels);
			while(!O.isEmpty()){

				//Assuming TW is ordered by value... this is me attempting to get the first key (or the first element of O ordered by decreasing TW value)
				int i = -2;
				for(int temp : TW.keySet()){
					i = temp;
					break;
				}
				double ImpactMin = Double.MAX_VALUE;
				int IndexMin = -1;

				for(int x=0; x<m; x++){
					if(checkServerConstraints(i,x,loads,capacities,O,currLabels)==false)
						continue;
					double ImpactViPx = computeImpact(i,x, VMTraffic, PMDistances, O, currLabels);
					if(ImpactViPx < ImpactMin){
						ImpactMin = ImpactViPx;
						IndexMin = x;
					}
				}

				if(IndexMin != -1){
					M.put(i,IndexMin);
					O.remove(i);
					TW.remove(i);
					//Update Capacities of source & destination PMS 
					/* ******************************************* I DON'T THINK I NEED TO DO ANYTHING for that**********************/
					//I did have to do something for this and it was change preLabels... Instead i made currLabels
					currLabels[i]=M.get(i);

				}
				else{
					M.put(i, -1); //No PM found for Vi
					O.remove(i);
					TW.remove(i);
					currLabels[i]=M.get(i);//NotEntirelySureIfINeedThisLine
					continue;
					
//					if(O.size()==1)
//						break;
//					else
//						continue;
				}
				System.out.println("O size: " + O.size());
				reportAllServerLoads(loads, capacities, O, currLabels);
			}
			
//			System.out.print("Labels: ");
//			for(int i=0; i<n; i++){
//				System.out.print(preLabels[i] + "\t");
//			}
	//
//			System.out.print("MigrationLabels: ");
//			for(int i : M.keySet()){
//				System.out.print("" + i + "->" + M.get(i) + "\t");
//			}

			
			//Defining the final labels by putting in the Migration labels into the preLabels
			int newLabels[] = new int[n];
			for(int i=0; i<n; i++){
				if(currLabels[i]==-1)
					return ("Minimum Cost: " + (-4) + "\n");
				
				newLabels[i] = currLabels[i];
			}
//			for(int i : M.keySet()){
//				newLabels[i] = M.get(i);
//			}
			
			//Defining minimumCost for the output file
			int minimumCost = 0;
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					minimumCost += VMTraffic[i][j] * PMDistances[newLabels[i]][newLabels[j]];
				}
			}
			minimumCost /= 2; //This is because we're double-counting.
			
			long endTime = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder();

			sb.append("Minimum Cost: " + minimumCost + "\n");
			sb.append("Labels: ");
			for(int i=0; i<n; i++){
				if(i !=n-1)
					sb.append(i + "->" + newLabels[i] + "\t");
				else
					sb.append(i + "->" + newLabels[i]);
			}
			sb.append("\nRunningTime: " + (endTime - startTime));
			return sb.toString();
			
		} catch (Exception ex) {
            ex.printStackTrace();
        }
		return "Error Reading in input file.";
	}
	
	
	private static Map<Integer, Integer> sortByComparator(Map<Integer, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1,
                                           Map.Entry<Integer, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		for (Iterator<Map.Entry<Integer, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	static boolean checkServerConstraints(int i, int x, int loads[], int capacities[], Set<Integer> O, int preLabels[]){
		int sum=0;
		for(int j=0; j<loads.length; j++){
			if(j==i)
				continue;
			if(O.contains(j))
				continue;
			if(preLabels[j] != x)
				continue;
			
			sum += loads[j];
		}		
		return (sum+loads[i])<=capacities[x];
		
	}
	
	//DebuggingMethod
	static int reportServerLoad(int x, int loads[], int capacities[], Set<Integer> O, int preLabels[]){
		int sum=0;
		for(int j=0; j<loads.length; j++){
			if(O.contains(j))
				continue;
			if(preLabels[j] != x)
				continue;
			sum += loads[j];
		}
		return sum;		
	}
	
	static void reportAllServerLoads(int loads[], int capacities[], Set<Integer> O, int preLabels[]){
		for(int x=0; x<capacities.length; x++){
			System.out.print("Server[" + x + "]: " + reportServerLoad(x,loads,capacities,O,preLabels) + "\t");
		}
		System.out.println();
	}
	
	
	static double computeImpact(int i, int x, int VMTraffic[][], int PMDistances[][], Set<Integer> O, int preLabels[]){
		return computeImpact_simple(i,x,VMTraffic,PMDistances,O,preLabels);
	}
	
	static double computeImpact_simple(int i, int x, int VMTraffic[][], int PMDistances[][], Set<Integer> O, int preLabels[]){
		Set<Integer> DepSet = DependentSet(i, VMTraffic, O);
		double sum =0;
		for(int j : DepSet){
			sum += Cost(i,x,j,preLabels[j], VMTraffic, PMDistances);
		}
		double denominator = ( (double) DistanceMax(PMDistances) ) * ( (double) TrafficMax(VMTraffic)  );
		return sum/denominator;
	}
	
	static Set<Integer> DependentSet(int i, int VMTraffic[][], Set<Integer> O){
		//In order to tell if j shares an edge with i, I'm checking whether the traffic is zero or not.
		Set<Integer> DepSet = new HashSet<Integer>();
		for(int j=0; j<VMTraffic.length; j++){
			if(VMTraffic[i][j]==0||VMTraffic[j][i]==0)
				continue;
			if(O.contains(j))
				continue;
			DepSet.add(j);
		}
		return DepSet;
	}
	
	//This is just W(i,j) * D(x,y). Use this assuming i is on x, j is on y.
	static int Cost(int i, int x, int j, int y, int VMTraffic[][], int PMDistances[][]){
		return VMTraffic[i][j] * PMDistances[x][y];
	}
	
	static int DistanceMax(int PMDistances[][]){
		int max=0;
		for(int i=0; i<PMDistances.length; i++){
			for(int j=0; j<PMDistances[i].length; j++){
				if(max < PMDistances[i][j])
					max = PMDistances[i][j];
			}
		}
		return max;
	}

	static int TrafficMax(int VMTraffic[][]){
		int max=0;
		for(int i=0; i<VMTraffic.length; i++){
			for(int j=0; j<VMTraffic[i].length; j++){
				if(max < VMTraffic[i][j])
					max = VMTraffic[i][j];
			}
		}
		return max;
	}
}
