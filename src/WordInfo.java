
public class WordInfo {
	public static double Smoothing = 15;
	public static boolean UseLectureApproach = true;

	public double pSpam = 0.0;
	public double pHam = 0.0;

	public long nSpam = 0;
	public long nHam = 0;
	
	// Compute conditional probability for given word
	public void ComputeProbability(long nTotalWordsSpam, long nTotalWordsHam, long nDictionary) {
		if (UseLectureApproach) {
			pSpam = (double)(nSpam + Smoothing * 1) / (double)(nTotalWordsSpam + Smoothing * nDictionary);
			pHam = (double)(nHam + 1) / (double)(nTotalWordsHam + nDictionary);
		} else {
			double p = 1.0/nDictionary;
			pSpam = (double)(nSpam + Smoothing * p) / (double)(nTotalWordsSpam + Smoothing);
			pHam = (double)(nHam + Smoothing * p) / (double)(nTotalWordsHam + Smoothing);
		}
	}
}
