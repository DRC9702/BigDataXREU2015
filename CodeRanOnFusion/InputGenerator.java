import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class InputGenerator {
	
	
	public static void generateInput(String filePath){ 
		
		for(int n=10; n<= 10; n+=1){
			for(int m=5; m<=5; m+=1){
				for(int k=n/3; k<=n/3; k++){
					for(int t=0; t<5; t++){
						try {
							String content = makeInput(n,m,k);
//							System.out.println(content);
							String filename = filePath + "/InputN" + n + "M" + m + "K" + k + "Num" + t + ".txt"; 
							File file = new File(filename);
				 
							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}
			 	
							FileWriter fw = new FileWriter(file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(content);
							bw.close();
			 	
//							System.out.println("Done");
			 	
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}
				}
			}
			System.out.println(n);
		}
		
		
		
		
		
//		boolean temp = true;
//		FeasibleChecker Fs = new FeasibleChecker();
//		int count=0;
//		while(temp){
//			String s = makeInput();
//			System.out.println("Attempt: " + (count++));
//			System.out.println(s);
//			if(Fs.CheckInput(s)){
//				System.out.println(s);
//				temp = false;
//			}
//		}
	}
	

	public static String makeInput(int n, int m, int k) {
		Scanner input = new Scanner(System.in);
		
		int VMLoads[]  = new int[n];
		int VMTraffic[][] = new int[n][n];
		int PMCapacities[] = new int[m];
		int PMDistances[][] = new int[m][m];
		int preLabels[] = new int[n];
				
		boolean isValid = false;
		
		while(isValid == false)
		{
			//Make VMLoads
			//VMLoads = new int[n];
			for(int i=0; i<n; i++){
				VMLoads[i] = (int)Math.ceil(Math.random()*5);
			}
			//Make VMTraffic
			//VMTraffic = new int[n][n];
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					if(i==j)
						VMTraffic[i][j]=0;
					else if(j < i)
						VMTraffic[i][j] = VMTraffic[j][i];
					else	
						VMTraffic[i][j] = (int)Math.ceil(Math.random()*10);
				}
			}
			//Make PMCapacities
			//PMCapacities = new int[m];
			for(int i=0; i<m; i++){
				PMCapacities[i] = (int)Math.ceil(Math.random()*15+5);
			}
			//Make PMDistances
			//PMDistances = new int[m][m];
			for(int i=0; i<m; i++){
				for(int j=0; j<m; j++){
					if(i==j)
						PMDistances[i][j]=0;
					else if(j < i)
						PMDistances[i][j] = PMDistances[j][i];
					else	
						PMDistances[i][j] = (int)Math.ceil(Math.random()*10);
				}
			}
			//Make preLabels
			//preLabels = new int[n];
			preLabels  = findAnySol(n,m,VMLoads,VMTraffic,PMCapacities,PMDistances);
			
			if(preLabels[0]==-2)
				isValid=false;
			else
				isValid=true;
		}
		
		
		StringBuilder s = new StringBuilder();
		
		
		//Append n
		s.append("" + n + "\n\n");
		//Append VMLoads
		for(int i=0; i<n; i++){
			s.append("" + VMLoads[i] + " ");
//			System.out.print(VMLoads[i] + " ");
		}
		//Append VMTraffic
		s.append("\n\n");
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				s.append("" + VMTraffic[i][j] + " ");
//				System.out.print(VMTraffic[i][j] + " ");
			}
			s.append("\n");
//			System.out.println();
		}
		//Append m
		s.append("\n" + m + "\n\n");
		//Append PMCapacities
		for(int i=0; i<m; i++){
			s.append("" + PMCapacities[i] + " ");
//			System.out.print(PMCapacities[i] + " ");
		}
		//Append PMDistances
		s.append("\n\n");
		for(int i=0; i<m; i++){
			for(int j=0; j<m; j++){
				s.append("" + PMDistances[i][j] + " ");
//				System.out.print(PMDistances[i][j] + " ");
			}
			s.append("\n");
//			System.out.println();
		}
		//Append k
		s.append("\n" + k + "\n\n");
		//Append preLabels
		for(int i=0; i<n; i++){
			s.append("" + preLabels[i] + " ");;
		}
		return s.toString();

	}
	
	
	public static int[] findAnySol(int n, int m, int VMLoads[], int VMTraffic[][], int PMCapacities[], int PMDistances[][]){
		
		int minimumCost = Integer.MAX_VALUE;
		int anyLabels[] = new int[n];
		
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

			//Check capacities for each pm
			for(int i=0; i<m; i++){
				int sum = 0;
				for(int j=0; j<n; j++){
					if(labels[j]==i)
						sum+=VMLoads[j];
				}
				if(sum > PMCapacities[i])
					pass = false;
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
					anyLabels = labels.clone();
					break;
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
		
		if(minimumCost ==2147483647){
			for(int i=0; i<n; i++)
				anyLabels[i] = -2;
		}
		
		System.out.println();
		System.out.println("N" + n + "M" + m);
		System.out.println("Minimum Cost: " + minimumCost);
		System.out.print("Labels: ");
		for(int i=0; i<n; i++)
			System.out.print(anyLabels[i] + "\t");
		
		return anyLabels;
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
	
	
	
//	public boolean CheckInput(int n, int loads[], int VMTraffic[][], int m, int capacities[], int PMDistances[][], int k, int preLabels[]) {
//
//		//Only the first k need to be reassigned
//		//fixedLabels[0] denotes the first fixed element which is the the kth VM (indexing from 0).
//		int fixedLabels[] = new int[n-k];
//		for(int i =0; i<n-k; i++){
//			fixedLabels[i] = preLabels[i];
//		}
//
//
//
//		int minimumCost = Integer.MAX_VALUE;
//		int optimalLabels[] = new int[n];
//
//		int labels[] = new int[n];
//		int zeroLabels[] = new int[n];
//		Arrays.fill(zeroLabels, 0);
//		Arrays.fill(labels, 0);
//		boolean pass = true;
//		while(pass){
//			/*for(int i=0; i<n;i++){
//						System.out.printf(labels[i] + " ");
//					}
//					System.out.println();*/
//
//			//checking fixedLabels
//			for(int i=0; i<n-k; i++){
//				if(labels[i] != fixedLabels[i]){
//					pass=false;
//					break;
//				}
//			}
//
//			if(pass!=false){
//				//Check capacities for each pm
//				for(int i=0; i<m; i++){
//					int sum = 0;
//					for(int j=0; j<n; j++){
//						if(labels[j]==i)
//							sum+=loads[j];
//					}
//					if(sum > capacities[i]){
//						pass = false;
//						break;
//					}
//				}
//			}
//
//			if(pass){
//				//Summing Up Traffic*Distance EdgeWeights(Double counting)
//				int cost = 0;
//				for(int i=0; i<n; i++){
//					for(int j=0; j<n; j++){
//						cost += VMTraffic[i][j] * PMDistances[labels[i]][labels[j]];
//					}
//				}
//				cost /= 2; //Possible rounding issue here, but if the math is correct, then no.
//				if( cost< minimumCost){
//					minimumCost = cost;
//					optimalLabels = labels.clone();				
//					//WE'RE DONE!! We just need to find one, not all of them
//					return true;
//				}
//
//				labels = increment(labels,m);
//				pass = true;
//				if(Arrays.equals(labels,zeroLabels))
//					return false;
//			}
//			else{
//				labels = increment(labels,m);
//				pass = true;
//				if(Arrays.equals(labels,zeroLabels))
//					return false;
//			}
// 
//		}
//		System.out.println("Shouldn't have gotten here, woops!");
//		return false;
//	}
//	
//	public static int[] increment(int x[], int modBase){
////		System.out.print("Running increment on: ");
////		for(int i=0; i<x.length;i++){
////			System.out.print(x[i] + " ");
////		}
////		System.out.print("vs: ");
//		
//		
//		
//		int fullLabels[] = new int[x.length];
//		Arrays.fill(fullLabels, modBase-1);
//		
////		for(int i=0; i<x.length;i++){
////			System.out.print(fullLabels[i] + " ");
////		}
////		System.out.println("to get: " + Arrays.equals(x, fullLabels));
//		
//		boolean done = false;
//		int backCount = 1;
//		while(!done){
//			
//			if(Arrays.equals(x,fullLabels)){
//				Arrays.fill(x, 0);
//				return x;			
//			}
//			
//			x[x.length - backCount] += 1;
//			if(x[x.length - backCount] % modBase == 0){
//				x[x.length - backCount] = 0;
//				backCount++;
//			}
//			else
//				done = true;
//		}
//		return x;
//	}

}
