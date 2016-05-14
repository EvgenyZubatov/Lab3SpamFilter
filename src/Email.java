
import java.util.HashMap;
import java.util.Map;

public class Email {
	protected HashMap<String, Integer> Words;
	protected String Id;
	protected boolean IsSpam;
	
	public Email(String id, boolean isSpam, HashMap<String, Integer> words) {
		Id = id;
		IsSpam = isSpam;
		Words = words;
	}
}
