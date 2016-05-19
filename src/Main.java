import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class Main {

	// Please change root folder location to point to your folder with test and train files 
	private static final String ROOT_FOLDER_PATH = "C:\\Users\\Eugene\\Documents\\PMP\\Machine Learning\\Lab3";
    private static final String TEST_FILE_PATH = ROOT_FOLDER_PATH + "\\test";
    private static final String TRAINING_FILE_PATH = ROOT_FOLDER_PATH + "\\train";
	
    // This is Vocabulary from lecture and Mitchel's book
    private static HashMap<String, WordInfo> Dictionary;
    
    // "Probability" of class "Spam" for training set
    private static double pSpam;
    
    //"Probability" of class "Ham" for training set
    private static double pHam;
    
	public static void main(String[] args) throws Exception {
		ParseFileAndTrainModel(TRAINING_FILE_PATH);
		RunPrediction(TEST_FILE_PATH);
	}
	
	// In this method I'm parsing input trainig set and train Naive Bayes "model" 
	private static void ParseFileAndTrainModel(String filePath) throws Exception {
		Dictionary = new HashMap<String, WordInfo>();
		
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        long nSpam = 0;
        long nEmails = 0;
        
        long nTotalWordsSpam = 0;
        long nTotalWordsHam = 0;
        
        while ((line = br.readLine()) != null) {
        	String[] tokens = line.split(" ");
        	String id = tokens[0];
        	if (!tokens[1].equalsIgnoreCase("ham") && !tokens[1].equalsIgnoreCase("spam")) {
        		throw new Exception();
        	}
        	
        	boolean isSpam = tokens[1].equalsIgnoreCase("spam");
        	for(int i=2; i < tokens.length; i += 2) {
        		String word = tokens[i].toLowerCase();  
        		Integer n = Integer.parseInt(tokens[i+1]);
        		
        		if (!Dictionary.containsKey(word)) {
        			Dictionary.put(word, new WordInfo());
        		}

        		if (isSpam) {
        			nTotalWordsSpam += n;
        			Dictionary.get(word).nSpam += n;
        		} else {
        			nTotalWordsHam += n;
        			Dictionary.get(word).nHam += n;
        		}
        	}
        	
        	nEmails++;
        	if (isSpam) {
        		nSpam++;
        	}
        	
        	System.out.println("Read email record with id: " + id);
        }
        br.close();
        System.out.println("Done reading email data file");
        
        pSpam = (double)nSpam / (double)nEmails;
        pHam = (double)(nEmails - nSpam) / (double)nEmails;
        
        for (Entry<String, WordInfo> e: Dictionary.entrySet()) {
        	e.getValue().ComputeProbability(nTotalWordsSpam, nTotalWordsHam, Dictionary.size());
        }        
	}
	
	private static void RunPrediction(String filePath) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filePath));        
        String line;

        long total = 0;
        long tp = 0, tn = 0, fp = 0, fn = 0;
        		
        while ((line = br.readLine()) != null) {
        	String[] tokens = line.split(" ");
        	String id = tokens[0];
        	if (!tokens[1].equalsIgnoreCase("ham") && !tokens[1].equalsIgnoreCase("spam")) {
        		throw new Exception();
        	}
        	
        	boolean isSpam = tokens[1].equalsIgnoreCase("spam");
        	SpamPrediction prediction = ComputeEmailPredictionLog(tokens);
        	
        	boolean isPredictedAsSpam = (prediction.pSpam > prediction.pHam);
        	total++;
        	if (isSpam) {
        		if (isPredictedAsSpam) tp++; else fn++; 
        	} else {
        		if (isPredictedAsSpam) fp++; else tn++;
        	}
        	
        	System.out.println("Read email record with id: " + id + " Actual IsSpam: " + isSpam + " Predicted IsSpam: " + isPredictedAsSpam +
        			" pEmailIsSpam: " + prediction.pSpam + " pEmailIsHam: " + prediction.pHam);
        }        
		
        System.out.println("Vocabulary size: " + Dictionary.size() + " Total number of emails: " + total + " Correct predictions: " + (tp + tn));
        System.out.println("True Positive: " + tp + " True negative: " + tn + " False positive: " + fp + " False negative: " + fn);
        System.out.println("Accuracy: " + (tp + tn)*100.0/total);
        System.out.println("Recall: " + tp*100.0/(tp + fn));
    }
	
	// Keeping this method for experiments
	// it's not used in final model since I'm doing all computations in Log-space 
	private static SpamPrediction ComputeEmailPredictionRegular(String[] tokens) {
    	double pEmailIsSpam = pSpam;
    	double pEmailIsHam = pHam;
    	for(int i=2; i < tokens.length; i += 2) {
    		String word = tokens[i].toLowerCase();  
    		Integer n = Integer.parseInt(tokens[i+1]);
    		
    		if (Dictionary.containsKey(word)) {
        		pEmailIsSpam *= Math.pow(Dictionary.get(word).pSpam, n);
        		pEmailIsHam *= Math.pow(Dictionary.get(word).pHam, n);
    		}
    	}
    	
		return new SpamPrediction(pEmailIsSpam, pEmailIsHam);
	}

    // I'm using Log-space conversion in order to avoid precision/error accumulation (when we multiply small 'double' numbers)
    // And rounding 'very small' doubles to zero 
	private static SpamPrediction ComputeEmailPredictionLog(String[] tokens) {
    	double pEmailIsSpam = Math.log(pSpam);
    	double pEmailIsHam = Math.log(pHam);
    	for(int i=2; i < tokens.length; i += 2) {
    		String word = tokens[i].toLowerCase();  
    		Integer n = Integer.parseInt(tokens[i+1]);
    		
    		if (Dictionary.containsKey(word)) {
        		pEmailIsSpam += n * Math.log(Dictionary.get(word).pSpam);
        		pEmailIsHam += n * Math.log(Dictionary.get(word).pHam);
    		}
    	}
    	
		return new SpamPrediction(pEmailIsSpam, pEmailIsHam);
	}	
}
