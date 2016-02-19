import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is implements the trainer algorithm using batch gradient ascent
 * for logistic regression
 * 
 * @author ignatius damai a0088455r
 *
 */
public class sctrain {
	private static String STOPWORDS_FILE = "stopwd.txt";

	public static void main(String[] args) throws IOException {
		// Verify that the correct arguments are passed in.
		if (args.length < 4) {
			System.out.println("4 arguments expected: <word1> <word2> <train_file> <model_file>");
			System.out.println(String.format("Got: %s", Arrays.toString(args)));
			return;
		}

		// Extract the various arguments to the program.
		String word1 = args[0];
		String word2 = args[1];
		String trainFilePath = args[2];
		String modelFilePath = args[3];

		Set<String> stopwords = LogisticRegressionHelper.stopwordsLoader(STOPWORDS_FILE);

		String[] trainLines = LogisticRegressionHelper.readFile(trainFilePath);

		// Prepare tokenized lines for training
		List<TokenizedLine> tokenizedLines = new ArrayList<TokenizedLine>();
		for (int i = 0; i < trainLines.length; i++) {
			TokenizedLine tl = LogisticRegressionHelper.lineParser(trainLines[i], stopwords);
			tokenizedLines.add(tl);
		}

		// Timestamp before training starts
		Date startTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedStartTime = sdf.format(startTime);
		System.out.println("Start training time: " + formattedStartTime);

		// Untrained model builder
		Model model = modelBuilder(tokenizedLines);

		/*
		 * Comment until the horizontal line and uncomment the tenfold verifier
		 * to run verifier
		 */
		model = modelTrainer(word1, word2, model, tokenizedLines);
		LogisticRegressionHelper.writeModelFile(model, modelFilePath);
		/*---------------------------------------------------*/
		// tenFoldVerifier(word1, word2, model, tokenizedLines);

		Date endTime = new Date();
		String formattedEndTime = sdf.format(endTime);
		System.out.println("End training time: " + formattedEndTime);
		return;
	}

	private static Model modelBuilder(List<TokenizedLine> tokenizedLines) {
		Model model = new Model();
		HashMap<Collocation, Double> collocationWeightMapping = model.getCollocationWeightMapping();
		HashMap<String, Double> wordWeightMapping = model.getWordWeightMapping();
		Iterator<TokenizedLine> iter = tokenizedLines.iterator();
		// Build vocabulary and collocation List
		while (iter.hasNext()) {
			TokenizedLine tl = iter.next();
			HashMap<Integer, String> sanitizedTaggedWords = tl.getSanitizedTaggedWords();
			for (int i = 0; i < tl.getCollocationData().size(); i++)
				collocationWeightMapping.put(tl.getCollocationData().get(i), 0.0);

			// store all sanitized word to wordWeightMapping;
			for (Integer key : sanitizedTaggedWords.keySet()) {
				wordWeightMapping.put(sanitizedTaggedWords.get(key), 0.0);
			}

		}
		return model;
	}

	/**
	 * Model Trainer
	 */
	private static Model modelTrainer(String word1, String word2, Model model, List<TokenizedLine> tokenizedLines) {
		// learning rate = alpha/2q where q = number of tokenized line
		// therefore alpha = rate * 2q
		double alpha = 0.01225 * 2 * tokenizedLines.size();
		double treshold = 0.0005;
		boolean isMoreThanTreshold = true;
		int k = 0;
		// batch ascent

		while (isMoreThanTreshold) {
			HashMap<Collocation, Double> collocationWeightMapping = model.getCollocationWeightMapping();
			HashMap<String, Double> wordWeightMapping = model.getWordWeightMapping();
			Model newModel = new Model();
			double maxDeltaL = 0.0;
			double prevMaxDeltaL = 1.0;
			// Calculate y(j) - 1/(1+exp(w(k).x(j), store it as
			// helperFunctionResult[j]
			// class 1 is defined as word 1 (mapped to yj=1), class 2 is defined
			// as word 2 (mapped to yj = 0)
			double[] helperFunctionResult = new double[tokenizedLines.size()];
			for (int j = 0; j < tokenizedLines.size(); j++) {
				TokenizedLine tl = tokenizedLines.get(j);
				double yj = 0.0;
				if (tl.getWordAnswer().equals(word1))
					yj = 1.0;
				double wkDotX = 0.0;
				HashMap<String, Integer> sanitizedWordCounter = tl.getSanitizedWordsCounter();
				Set<String> words = sanitizedWordCounter.keySet();
				Iterator<String> iterWordCounter = words.iterator();
				while (iterWordCounter.hasNext()) {
					String word = iterWordCounter.next();

					if (wordWeightMapping.containsKey(word))
						wkDotX += wordWeightMapping.get(word) * sanitizedWordCounter.get(word);
				}

				List<Collocation> collocationData = tl.getCollocationData();
				Iterator<Collocation> iterCollocation = collocationData.iterator();
				while (iterCollocation.hasNext()) {
					Collocation collocation = iterCollocation.next();
					if (collocationWeightMapping.containsKey(collocation))
						wkDotX += collocationWeightMapping.get(collocation);
				}

				helperFunctionResult[j] = (yj - (1 / (1 + Math.exp(-1 * wkDotX))));
			}

			// start testing weight k performance and generate weight k+1
			HashMap<String, Double> newWordWeightMapping = newModel.getWordWeightMapping();
			HashMap<Collocation, Double> newCollocationWeightMapping = newModel.getCollocationWeightMapping();
			// update weight on Words
			Iterator<String> iterWord = model.getWordWeightMapping().keySet().iterator();
			// Iterate through the word features and use the calculation to update weight for iteration k+1
			while (iterWord.hasNext()) {
				String word = iterWord.next();
				double deltaL = 0.0;
				// batch regression test;
				for (int j = 0; j < tokenizedLines.size(); j++) {
					TokenizedLine tl = tokenizedLines.get(j);
					if (tl.getSanitizedWordsCounter().containsKey(word))
						deltaL += tl.getSanitizedWordsCounter().get(word) * helperFunctionResult[j];
				}
				deltaL = deltaL / tokenizedLines.size();
				if (Math.abs(deltaL) > maxDeltaL)
					maxDeltaL = Math.abs(deltaL);
				double newWeight = wordWeightMapping.get(word) + alpha * deltaL;
				newWordWeightMapping.put(word, newWeight);
			}

			// Update weight on collocations
			Iterator<Collocation> iterCollocation = model.getCollocationWeightMapping().keySet().iterator();
			// Iterate through collocation features and use the calculation result to update weight for iteration k+1
			while (iterCollocation.hasNext()) {
				Collocation collocation = iterCollocation.next();
				double deltaL = 0.0;
				for (int j = 0; j < tokenizedLines.size(); j++) {
					TokenizedLine tl = tokenizedLines.get(j);
					if (tl.getCollocationData().contains(collocation))
						deltaL += helperFunctionResult[j];
				}
				deltaL = deltaL / tokenizedLines.size();
				if (Math.abs(deltaL) > maxDeltaL)
					maxDeltaL = Math.abs(deltaL);
				double newWeight = collocationWeightMapping.get(collocation) + alpha * deltaL;
				newCollocationWeightMapping.put(collocation, newWeight);
			}

			// Update model to use new model from this iteration
			model = newModel;
			isMoreThanTreshold = maxDeltaL > treshold;

			// If the trainer diverges, update the alpha, and reduce it to half
			if (prevMaxDeltaL < maxDeltaL)
				alpha = alpha / 2;
			prevMaxDeltaL = maxDeltaL;
			k++;
		}
		// Uncomment this if you want to know the final alpha value or to check
		// whether there has been an update to it.
		// System.out.println("Ending alpha = " + alpha);
		return model;
	}

	/**
	 * 10 fold verifier system
	 * 
	 * @param word1
	 * @param word2
	 * @param model
	 * @param tokenizedLines
	 */
	private static void tenFoldVerifier(String word1, String word2, Model model, List<TokenizedLine> tokenizedLines) {
		// Divide batch by ten
		int batchSize = tokenizedLines.size() / 10;
		for (int batchNo = 0; batchNo < 10; batchNo++) {
			// Add all other train sets that are not for testing as trainer
			List<TokenizedLine> tokenizedLinesTrainer = new ArrayList<TokenizedLine>();
			tokenizedLinesTrainer.addAll(tokenizedLines.subList(0, batchNo * batchSize));
			tokenizedLinesTrainer.addAll(tokenizedLines.subList((batchNo + 1) * batchSize, tokenizedLines.size()));
			// Train model
			Model newModel = modelTrainer(word1, word2, model, tokenizedLinesTrainer);
			int correctCounter = 0;
			HashMap<String, String> res = sctest.executeTest(word1, word2, newModel,
					tokenizedLines.subList((batchNo * batchSize), ((batchNo + 1) * batchSize)));
			for (int i = 0; i < res.size(); i++) {
				TokenizedLine tl = tokenizedLines.get((batchNo * batchSize) + i);
				String lineTag = tl.getLineTag();
				String calculatedAnswer = res.get(lineTag);
				if (calculatedAnswer.equals(tl.getWordAnswer()))
					correctCounter++;
			}
			double percentageCorrect = ((double) correctCounter) / ((double) batchSize);
			System.out.println("Batch no. " + (batchNo + 1) + " accuracy: " + percentageCorrect);

		}

	}
}
