import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
//There should be a better way to prevent these (these happen when some algorithms cant find feasible solutions)
//But in the meantime, I'm just going to catch the NoSuchElementExceptions
import java.util.NoSuchElementException;

public class ResultReaderImproved {

	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
		writeResults(input.next());
		input.close();
	}

	public static void writeResults(String dest) {
		
		
		try {
			// String content = makeContent(dest);
//			System.out.println(content);
			String filename = dest + "/Results2.txt"; 
			File file = new File(filename);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			File folder = new File(dest + "/Inputs/");
			// File folder = new File(dest + "/LPOutputs/");
			/************//////
			File[] listOfFiles = folder.listFiles();
			
			bigLoop: for (File f : listOfFiles) {
		    if (f.isFile()) {
				
				StringBuilder sb = new StringBuilder();
		        sb.append(f.getName());
		        
		        String BFName = f.getName();
		        BFName = BFName.substring(2);
		        BFName = "BFOut" + BFName;
		        
		        String AAName = f.getName();
		        AAName = AAName.substring(2);
		        AAName = "AAOut" + AAName;
		        
		        String IPName = f.getName();
		        IPName = IPName.substring(2);
		        IPName = "IPOut" + IPName;
		       
				String LPName = f.getName();
				LPName = LPName.substring(2);
				LPName = "LPOut" + LPName;


		        try {
					try{
					Scanner BFReader = new Scanner(new File(dest + "/BFOutputs/" + BFName));
					while(!BFReader.next().equals("Cost:"));
					// BFReader.next();
					// BFReader.next();
					int BFCost = BFReader.nextInt();
					while(!BFReader.next().equals("RunningTime:"));
					int BFTime = BFReader.nextInt();
					
					Scanner AAReader = new Scanner(new File(dest + "/AAOutputs/" + AAName));
					while(!AAReader.next().equals("Cost:"));
					// AAReader.next();
					// AAReader.next();
					int AACost = AAReader.nextInt();
					while(!AAReader.next().equals("RunningTime:"));
					int AATime = AAReader.nextInt();
					
					Scanner IPReader = new Scanner(new File(dest + "/IPOutputs/" + IPName));
					while(!IPReader.next().equals("Cost:"));
					// IPReader.next();
					// IPReader.next();
					int IPCost = IPReader.nextInt();
					while(!IPReader.next().equals("RunningTime:"));
					int IPTime = IPReader.nextInt();
					
					Scanner LPReader = new Scanner(new File(dest + "/LPOutputs/" + LPName));
					while(!LPReader.next().equals("Cost:"));
					// LPReader.next();
					// LPReader.next();
					int LPCost = LPReader.nextInt();
					while(!LPReader.next().equals("Runtime:")); /***********************THIS TOOK ME 15 MINUTES TO FIND BECAUSE THE OUTPUT FILE I ASKED FOR WASN'T FORMATTED CORRECTLY >:[     ******/
					int LPTime = LPReader.nextInt();
					
					
					sb.append("\tBFCost:\t" + BFCost + "\tBFTime:\t" + BFTime + "\t\tAACost:\t" + AACost + "\tAATime:\t" + AATime + "\tIPCost:\t" + IPCost + "\tIPTime:\t" + IPTime + "\tLPCost:\t" + LPCost + "\tLPTime:\t" + LPTime +"\n");
					System.out.println(sb.toString());
					bw.write(sb.toString());
					} catch(NoSuchElementException nse){
						nse.printStackTrace();
						continue;
					}
					

				} catch (FileNotFoundException e) {
					e.printStackTrace();
					continue bigLoop;
				}
		        

		    }
		}
			
			
			// bw.write(content);
			bw.close();
 
//			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
	}
	
	public static String makeContent(String bigFolder){

		StringBuilder sb = new StringBuilder();
		
		File folder = new File(bigFolder + "/Inputs/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        sb.append(file.getName());
		        
		        String BFName = file.getName();
		        BFName = BFName.substring(2);
		        BFName = "BFOut" + BFName;
		        
		        String AAName = file.getName();
		        AAName = AAName.substring(2);
		        AAName = "AAOut" + AAName;
		        
		        String IPName = file.getName();
		        IPName = IPName.substring(2);
		        IPName = "IPOut" + IPName;
		        
		        try {
					Scanner BFReader = new Scanner(new File(bigFolder + "/BFOutputs/" + BFName));
					while(!BFReader.next().equals("Cost:"));
					// BFReader.next();
					// BFReader.next();
					int BFCost = BFReader.nextInt();
					while(!BFReader.next().equals("RunningTime:"));
					int BFTime = BFReader.nextInt();
					
					Scanner AAReader = new Scanner(new File(bigFolder + "/AAOutputs/" + AAName));
					while(!AAReader.next().equals("Cost:"));
					// AAReader.next();
					// AAReader.next();
					int AACost = AAReader.nextInt();
					while(!AAReader.next().equals("RunningTime:"));
					int AATime = AAReader.nextInt();
					
					Scanner IPReader = new Scanner(new File(bigFolder + "/IPOutputs/" + IPName));
					while(!IPReader.next().equals("Cost:"));
					// IPReader.next();
					// IPReader.next();
					int IPCost = IPReader.nextInt();
					while(!IPReader.next().equals("RunningTime:"));
					int IPTime = IPReader.nextInt();
					
					
					sb.append("\tBFCost:\t" + BFCost + "\tBFTime:\t" + BFTime + "\t\tAACost:\t" + AACost + "\tAATime:\t" + AATime + "\tIPCost:\t" + IPCost + "\tIPTime:\t" + IPTime + "\n");
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		        

		    }
		}
		
		return sb.toString();

	}

}
