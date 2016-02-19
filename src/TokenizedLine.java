import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TokenizedLine class stores the important and relevant information for the
 * algorithm to process a line data
 * 
 * @author ignatius damai a0088455r
 *
 */
public class TokenizedLine implements Serializable {

	private static final long serialVersionUID = 2436988496278611759L;

	private String lineTag;

	// tagged words with stop words included
	private HashMap<Integer, String> taggedWords;

	// tagged words with stop words excluded
	private HashMap<Integer, String> sanitizedTaggedWords;

	// sanitized words count
	private HashMap<String, Integer> sanitizedWordsCounter;

	// stores collocation data
	private List<Collocation> collocationData;

	// confusable word if there is any from the lines
	private String wordAnswer;

	public String getLineTag() {
		return lineTag;
	}

	public void setLineTag(String lineTag) {
		this.lineTag = lineTag;
	}

	public HashMap<Integer, String> getTaggedWords() {
		if (this.taggedWords == null)
			this.taggedWords = new HashMap<Integer, String>();
		return taggedWords;
	}

	public void setTaggedWords(HashMap<Integer, String> taggedWords) {
		this.taggedWords = taggedWords;
	}

	public HashMap<Integer, String> getSanitizedTaggedWords() {
		if (this.sanitizedTaggedWords == null)
			this.sanitizedTaggedWords = new HashMap<Integer, String>();
		return sanitizedTaggedWords;
	}

	public void setSanitizedTaggedWords(HashMap<Integer, String> sanitizedTaggedWords) {
		this.sanitizedTaggedWords = sanitizedTaggedWords;
	}

	public HashMap<String, Integer> getSanitizedWordsCounter() {
		if (this.sanitizedWordsCounter == null)
			this.sanitizedWordsCounter = new HashMap<String, Integer>();
		return sanitizedWordsCounter;
	}

	public void setSanitizedWordsCounter(HashMap<String, Integer> sanitizedWordsCounter) {
		this.sanitizedWordsCounter = sanitizedWordsCounter;
	}

	public String getWordAnswer() {
		return wordAnswer;
	}

	public void setWordAnswer(String wordAnswer) {
		this.wordAnswer = wordAnswer;
	}

	public List<Collocation> getCollocationData() {
		if (this.collocationData == null)
			this.collocationData = new ArrayList<Collocation>();
		return collocationData;
	}

	public void setCollocationData(List<Collocation> collocationData) {
		this.collocationData = collocationData;
	}

}
