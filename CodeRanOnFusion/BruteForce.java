import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class BruteForce {

	
	public static void readInputMakeOutput(File f, int n, int m, int k, int t, String filePath){
		try {
			String content = solve(f);
//			System.out.println(content);
			String filename = filePath + "/BFOutputN" + n + "M" + m + "K" + k + "Num" + t + ".txt"; 
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
	
	public static String solve(File f){
		
		try{
			long startTime = System.currentTimeMillis();
			
			Scanner input = new Scanner(f);
//			Scanner input = new Scanner(System.in);
			
			
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
			
			//Only the first k need to be reassigned
			int fixedLabels[] = new int[n-k];
			for(int i =0; i<n-k; i++){
				fixedLabels[i] = preLabels[i+k];
			}
			
			
			
			int minimumCost = Integer.MAX_VALUE;
			int optimalLabels[] = new int[n];
			
			int labels[] = new int[n];
			int zeroLabels[] = new int[n];
			Arrays.fill(zeroLabels, 0);
			Arrays.fill(labels, 0);
			boolean pass = true;
			while(pass){
				/*for(int i=0; i<n;i++){
					System.out.printf(labels[i] + " ");
				}
				System.out.println();*/

				//checking fixedLabels
				for(int i=0; i<(n-k); i++){
					if(labels[i+k] != fixedLabels[i]){
						pass=false;
						break;
					}
				}
				
				if(pass!=false){
					//Check capacities for each pm
					for(int i=0; i<m; i++){
						int sum = 0;
						for(int j=0; j<n; j++){
							if(labels[j]==i)
								sum+=loads[j];
						}
						if(sum > capacities[i]){
							pass = false;
							break;
						}
					}
				}
				
				if(pass){
					//Summing Up Traffic*Distance EdgeWeights(Double counting)
					int cost = 0;
					for(int i=0; i<n; i++){
						for(int j=0; j<n; j++){
							cost += VMTraffic[i][j] * PMDistances[labels[i]][labels[j]];
						}
					}
					cost /= 2; //Possible rounding issue here, but if the math is correct, then no.
					if( cost< minimumCost){
						minimumCost = cost;
						optimalLabels = labels.clone();					
					}
					
					labels = increment(labels,m);
					pass = true;
					if(Arrays.equals(labels,zeroLabels))
						break;
				}
				else{
					labels = increment(labels,m);
					pass = true;
					if(Arrays.equals(labels,zeroLabels))
						break;
				}
				
			}
			
			
			for(int i=0; i<(n-k); i++){
				if(optimalLabels[i+k] != fixedLabels[i]){
					System.out.println("Nope!");
					break;
				}
			}
			
			
			long endTime = System.currentTimeMillis();
			
			StringBuilder sb = new StringBuilder();
			sb.append("Minimum Cost: " + minimumCost + "\n");
			sb.append("Labels: ");
			for(int i=0; i<n; i++){
				if(i !=n-1)
					sb.append(i + "->" + optimalLabels[i] + "\t");
				else
					sb.append(i + "->" + optimalLabels[i]);
			}
			sb.append("\nRunningTime: " + (endTime - startTime));
			
//			System.out.println("Minimum Cost: " + minimumCost);
//			System.out.print("Labels: ");
//			for(int i=0; i<n; i++)
//				System.out.print(optimalLabels[i] + "\t");
			
			input.close();
			return(sb.toString());
			

		} catch (Exception ex) {
            ex.printStackTrace();
        }
		return "Error Reading in input file.";

	}
	
	public static int[] increment(int x[], int modBase){
//		System.out.print("Running increment on: ");
//		for(int i=0; i<x.length;i++){
//			System.out.print(x[i] + " ");
//		}
//		System.out.print("vs: ");
		
		
		
		int fullLabels[] = new int[x.length];
		Arrays.fill(fullLabels, modBase-1);
		
//		for(int i=0; i<x.length;i++){
//			System.out.print(fullLabels[i] + " ");
//		}
//		System.out.println("to get: " + Arrays.equals(x, fullLabels));
		
		boolean done = false;
		int backCount = 1;
		while(!done){
			
			if(Arrays.equals(x,fullLabels)){
				Arrays.fill(x, 0);
				return x;			
			}
			
			x[x.length - backCount] += 1;
			if(x[x.length - backCount] % modBase == 0){
				x[x.length - backCount] = 0;
				backCount++;
			}
			else
				done = true;
		}
		return x;
	}
}
