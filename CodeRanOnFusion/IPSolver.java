/* --------------------------------------------------------------------------
 * File: CutStock.java
 * Version 12.6.2  
 * --------------------------------------------------------------------------
 * Licensed Materials - Property of IBM
 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55 5655-Y21
 * Copyright IBM Corporation 2001, 2015. All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * --------------------------------------------------------------------------
 */

import ilog.concert.*;
import ilog.cplex.*;
import java.io.*;
import java.util.Scanner;

public class IPSolver {
	
//	public static void main(String[] args){
//		Scanner input = new Scanner(System.in);
//		readInputMakeOutput(new File(input.next()), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
//	}
	
	public static void readInputMakeOutput(File f, int n, int m, int k, int t, String filePath){
		try {
			String content = solve(f);
//			System.out.println(content);
			String filename = filePath + "/IPOutputN" + n + "M" + m + "K" + k + "Num" + t + ".txt"; 
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
		
		try {
			long startTime = System.currentTimeMillis();
			Scanner input = new Scanner(file);
			
			int n = input.nextInt(); //n can be different stuff now.
			
			int loads[] = new int[n];
			int VMTraffic[][] = new int[n][n];
			
			for(int i = 0; i <n; i++){
				loads[i] = input.nextInt();
			}
			
			for(int i = 0; i<n; i++){
				for(int j=0; j<n; j++){
					VMTraffic[i][j] = input.nextInt();
				}
			}
			
			int m = input.nextInt();
			int capacities[] = new int[m];
			int PMDistances[][] = new int[m][m];
			
			for(int i = 0; i <m; i++){
				capacities[i] = input.nextInt();
			}
			
			for(int i = 0; i<m; i++){
				for(int j=0; j<m; j++){
					PMDistances[i][j] = input.nextInt();
				}
			}
			
			int k = input.nextInt();
			int preLabels[] = new int[n];
			for(int i=0; i<n; i++){
				preLabels[i] = input.nextInt();
			}
			
			
			try{
				//define model
				IloCplex cplex = new IloCplex();
				cplex.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Primal);
				cplex.setParam(IloCplex.IntParam.ParallelMode, 1);

				
				//variables
				IloIntVar[][] a = new IloIntVar[n][];
				for(int i=0; i<n; i++){
					a[i] = cplex.intVarArray(m, 0, 1);
				}
				IloIntVar[][][][] t = new IloIntVar[n][m][n][m];
				for(int i=0; i<n; i++){
					for(int x=0; x<m; x++){
						for(int j=0; j<n; j++){
							t[i][x][j] = cplex.intVarArray(m,0,1);
						}
					}
				}
				
				
				//objective
				IloLinearIntExpr objective = cplex.linearIntExpr();
				for(int i=0; i<n; i++){
					for(int x=0; x<m; x++){
						for(int j=i+1; j<n; j++){
							for(int y=0; y<m; y++){
								objective.addTerm(VMTraffic[i][j]*PMDistances[x][y], t[i][x][j][y]);;
							}
						}
					}
				}
				
				cplex.addMinimize(objective);
				
				
				//constraints
				IloLinearIntExpr int_expr = cplex.linearIntExpr();
				
				//1
				
				for(int i=0; i<n; i++){
					for(int x=0; x<m; x++){
						for(int j=0; j<n; j++){
							for(int y=0; y<m; y++){
								cplex.addGe(t[i][x][j][y], 0);
								cplex.addLe(t[i][x][j][y], 1);
							}
						}
					}
				}
				
				//2
				for(int i=0; i<n; i++){
					for(int x=0; x<m; x++){
						cplex.addGe(a[i][x], 0);
						cplex.addLe(a[i][x], 1);
					}
				}
				
				//3
				for(int i=0; i<n; i++){
					int_expr = cplex.linearIntExpr();
					for(int x=0; x<m; x++){
						int_expr.addTerm(1, a[i][x]);
					}
					cplex.addEq(int_expr, 1);
				}
				
				//4
				for(int i=0; i<n; i++){
					for(int j=0; j<n; j++){
						int_expr = cplex.linearIntExpr();
						for(int x=0; x<m; x++){
							for(int y=0; y<m; y++){
								int_expr.addTerm(1,t[i][x][j][y]);
							}
						}
						cplex.addEq(1,int_expr);
					}
				}
				
				//5
				for(int i=0; i<n; i++){
					for(int j =i+1; j<n; j++){
						for(int x=0; x<m; x++){
							int_expr = cplex.linearIntExpr();
							for(int y=0; y<m; y++){
								int_expr.addTerm(1, t[i][x][j][y]);
							}
							cplex.addGe(int_expr, a[i][x]);
						}
					}
				}
				
				//6
				for(int i=0; i<n; i++){
					for(int j =i+1; j<n; j++){
						for(int y=0; y<m; y++){
							int_expr = cplex.linearIntExpr();
							for(int x=0; x<m; x++){
								int_expr.addTerm(1, t[i][x][j][y]);
							}
							cplex.addGe(int_expr, a[j][y]);
						}
					}
				}		
				
				//7
				for(int x=0; x<m; x++){
					int_expr = cplex.linearIntExpr();
					for(int i=0; i<n; i++){
						int_expr.addTerm(loads[i], a[i][x]);
					}
					cplex.addLe(int_expr, capacities[x]);
				}
				
				//8? This is just telling it to put every VM indexed higher than k in the prelabeled designated spot
				for(int i=0; i<(n-k); i++){
					int temp = preLabels[i+k];
					cplex.addEq(a[i+k][temp], 1);
				}
				
				long endTime;
				
				if(cplex.solve()){
					
					endTime = System.currentTimeMillis();
					int minimumCost = (int) cplex.getObjValue(); //The type casting shouldn't change anything
					int ipLabels[] = new int[n];
					for(int i=0; i<n; i++){
						for(int x=0; x<m; x++){
							if(  (  (int)cplex.getValue(a[i][x])  )==1)//The type casting shouldn't change anything
								ipLabels[i]=x;
						}
					}
					
					StringBuilder sb = new StringBuilder();
					sb.append("Minimum Cost: " + minimumCost + "\n");
					sb.append("Labels: ");
					for(int i=0; i<n; i++){
						if(i !=n-1)
							sb.append(i + "->" + ipLabels[i] + "\t");
						else
							sb.append(i + "->" + ipLabels[i]);
					}
					sb.append("\nRunningTime: " + (endTime - startTime));
					
					
//					System.out.println("obj =" + cplex.getObjValue());
//					
//					for(int i=0; i<n; i++){
//						for(int x=0; x<m; x++){
//							//if(cplex.getValue(a[i][x]) < m-1)
//								System.out.print("["+i+"]["+x+"]: " + cplex.getValue(a[i][x]) + "\t");
//						}
//						System.out.println();
//					}
					
					return sb.toString();
					
					//System.out.println("[1][2] = " + cplex.getValue(a[1][2]));
				}
				else{
					endTime = System.currentTimeMillis();
//					System.out.println("Model not solved");
					return ("Minimum Cost: " + (-4) + "\nRunningTime: " + (endTime - startTime));
				}
				
				
			}
			catch(IloException exc){
				exc.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Error reading file.";
		
	}
	
	
}