import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class FunkyInputGenerator {
	
	
	public static void generateInput(String filePath){ 
		
		for(int n=10; n<= 16; n+=2){
			for(int m=n/3; m<=n/2; m+=2){
				for(int k=n/3; k<=n/2; k+=2){
					for(int t=0; t<20; t++){
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
		
		bigLoop: while(isValid == false)
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
			
			
			for(int i=0; i<n; i++){
				if(preLabels[i]<0){
					isValid=false;
					continue bigLoop;
				}
			}
			isValid = true;
			
			//This didn't work and I'm done trying to figure out why
			// if(Arrays.asList(preLabels).contains(-2))
				// isValid=false;
			// else
				// isValid=true;
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
		
		int cost = 0;
		int anyLabels[] = new int[n];
		Arrays.fill(anyLabels,-2); //I think this should solve the issue of bad inputs, maybe?
		
		int labelLoad[] = new int[m];
		for(int i=0; i<m; i++){
			labelLoad[i]=0;
		}
		
		int VMBigToSmall[] = new int[n];
		for(int i=0; i<n; i++){
			VMBigToSmall[i] = i;
		}
		for(int i=n-1; i>=0; i--){
			for(int j=0; j<i; j++){
				if(VMLoads[VMBigToSmall[j]] < VMLoads[VMBigToSmall[j+1]]){
					int temp = VMBigToSmall[j];
					VMBigToSmall[j] = VMBigToSmall[j+1];
					VMBigToSmall[j+1] = temp;
				}
			}
		}
		
		int PMBigToSmall[] = new int[m];
		for(int i=0; i<m; i++){
			PMBigToSmall[i] = i;
		}
		for(int i=m-1; i>=0; i--){
			for(int j=0; j<i; j++){
				if(PMCapacities[PMBigToSmall[j]] < PMCapacities[PMBigToSmall[j+1]]){
					int temp = PMBigToSmall[j];
					PMBigToSmall[j] = PMBigToSmall[j+1];
					PMBigToSmall[j+1] = temp;
				}
			}
		}
		
		
		for(int i=0; i<n; i++){
			insideLoop: for(int j=0; j<m; j++){
				if(VMLoads[VMBigToSmall[i]] < PMCapacities[PMBigToSmall[j]]-labelLoad[PMBigToSmall[j]]){
					anyLabels[VMBigToSmall[i]]=PMBigToSmall[j];
					labelLoad[PMBigToSmall[j]] += VMLoads[VMBigToSmall[i]];
					break insideLoop;
				}
			}
		}
		
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
	
	
	


}
