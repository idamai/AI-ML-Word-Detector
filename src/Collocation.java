import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Collocation class denotes the collocation feature of the logarithmic
 * regression classifier i and j denotes starting and ending word location
 * relative to the confusable word wordCode array list contains the word in
 * sequential order from left to right.
 * 
 * @author ignatius damai a0088455r
 *
 */
public class Collocation implements Serializable {

	private static final long serialVersionUID = 3037463889544860444L;

	private Integer i, j;
	private ArrayList<String> wordCode;

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public Integer getJ() {
		return j;
	}

	public void setJ(Integer j) {
		this.j = j;
	}

	public ArrayList<String> getWordCode() {
		if (this.wordCode == null)
			this.wordCode = new ArrayList<String>();
		return wordCode;
	}

	public void setWordCode(ArrayList<String> wordCode) {
		this.wordCode = wordCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Collocation))
			return false;
		if (((Collocation) obj).getI().equals(i) && ((Collocation) obj).getJ().equals(j)) {
			if (wordCode.equals(((Collocation) obj).getWordCode()))
				return true;
			else
				return false;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.i, this.j, this.wordCode);
	}

}
