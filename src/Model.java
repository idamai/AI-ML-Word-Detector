import java.io.Serializable;
import java.util.HashMap;

/**
 * Model class is the model for the logarithmic regression classifier This class
 * stores the mapping from the features to weights wordWeightMapping stores the
 * mapping between each word and the respective weight collocationWeightMapping
 * stores the mapping between each collocation and the respective weight
 * 
 * @author ignatius damai a0088455r
 *
 */
public class Model implements Serializable {

	private static final long serialVersionUID = -4396868054922007690L;

	private HashMap<String, Double> wordWeightMapping;
	private HashMap<Collocation, Double> collocationWeightMapping;

	public HashMap<String, Double> getWordWeightMapping() {
		if (wordWeightMapping == null)
			this.wordWeightMapping = new HashMap<String, Double>();
		return wordWeightMapping;
	}

	public void setWordWeightMapping(HashMap<String, Double> wordWeightMapping) {
		this.wordWeightMapping = wordWeightMapping;
	}

	public HashMap<Collocation, Double> getCollocationWeightMapping() {
		if (this.collocationWeightMapping == null)
			this.collocationWeightMapping = new HashMap<Collocation, Double>();
		return collocationWeightMapping;
	}

	public void setCollocationWeightMapping(HashMap<Collocation, Double> collocationWeightMapping) {
		this.collocationWeightMapping = collocationWeightMapping;
	}

}
