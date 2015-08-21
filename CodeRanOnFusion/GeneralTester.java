import java.io.File;
import java.util.Date;

public class GeneralTester {

	
	
	public static void generateOutput(String sourceFilePath,String destFilePath) {
		
		
		File folder = new File(sourceFilePath);
		File[] listOfFiles = folder.listFiles();
		
		//BruteForce Bf = new BruteForce();
		BruteForceImproved Bf = new BruteForceImproved();
		IPSolver IP = new IPSolver();
		AppAware AA = new AppAware();
		int n=0,m=0,k=0,t=0;

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		        String s = file.getName();
		        
		        
		        n = Integer.parseInt(s.substring(s.indexOf('N')+1, s.indexOf('M')));
		        m = Integer.parseInt(s.substring(s.indexOf('M')+1, s.indexOf('K')));
		        k = Integer.parseInt(s.substring(s.indexOf('K')+1, s.indexOf("Num")));
		        t = Integer.parseInt(s.substring(s.indexOf("Num")+3, s.length()-4));
		        Bf.readInputMakeOutput(file, n, m, k, t, destFilePath + "/BFOutputs");
			IP.readInputMakeOutput(file, n, m, k, t, destFilePath + "/IPOutputs");
			AA.readInputMakeOutput(file, n, m, k, t, destFilePath + "/AAOutputs");

		    }
		}

	}

}
