import java.io.File;
import java.util.Date;
import java.util.Scanner;

public class TesterBeta {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Scanner input = new Scanner(System.in);
		System.out.println("Files will be created in local directory.");
//		String inputDir = input.next();
		
		
		Date currDate = new Date();
		String timeString = currDate.toString().replaceAll("\\s","-").replaceAll(":","-");
		String dirName = "TestData-" + timeString;
		
		
		File theDir = new File(dirName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + (dirName));
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        
		        String inputDir = dirName + "/Inputs";
		        String AADir = dirName + "/AAOutputs";
		        String BFDir = dirName + "/BFOutputs";
		        String IPDir = dirName + "/IPOutputs";
		        String LPDir = dirName + "/LPOutputs";
		        
		        new File(inputDir).mkdir();
		        new File(AADir).mkdir();
		        new File(BFDir).mkdir();
		        new File(IPDir).mkdir();
		        new File(LPDir).mkdir();
		        result = true;

			//InputGenerator IG = new InputGenerator();
			//IG.generateInput(inputDir);
			FunkyInputGenerator FIG = new FunkyInputGenerator();
			FIG.generateInput(inputDir);

			System.out.println("Finished creating inputs");

			GeneralTester GT = new GeneralTester();
			GT.generateOutput(inputDir, dirName);

			System.out.println("Finished running All Algorithms");

			System.out.println("Reading and Reporting Results");
			ResultReaderImproved RR = new ResultReaderImproved();
			RR.writeResults(dirName);
			System.out.println("Done reporting");

		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIRs created");  
		    }
		}
		else
			System.out.println("Nothing happened");
		
		
	}

}
