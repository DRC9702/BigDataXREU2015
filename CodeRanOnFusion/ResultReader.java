import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class ResultReader {

	public static void writeResults(String dest) {
		
		
		try {
			String content = makeContent(dest);
//			System.out.println(content);
			String filename = dest + "/Results.txt"; 
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
