import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Logistic regression helper class stores all the common function that might be
 * used by all other classes
 * 
 * @author ignatius damai a0088455r
 *
 */
public class LogisticRegressionHelper {
	/**
	 * Text file reader and returns a array of strings.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String[] readFile(String filePath) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			bufferedReader.close();
		}
		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * Model file writer
	 * 
	 * @param Model,
	 *            ModelFilePath
	 * @return
	 * @throws IOException
	 */
	public static void writeModelFile(Model model, String filePath) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filePath));
		objectOutputStream.writeObject(model);
		objectOutputStream.flush();
		objectOutputStream.close();
	}

	/**
	 * Model file reader and convert it to a model object
	 * 
	 * @param Model,
	 *            ModelFilePath
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Model readModelFile(String filePath) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));

		// start getting the objects out in the order in which they were written
		Model model = (Model) objectInputStream.readObject();

		objectInputStream.close();
		return model;
	}

	/**
	 * Text file reader and returns a array of lines.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static void writeAnswerFile(String filePath, HashMap<String, String> answer) throws IOException {
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
		try {
			SortedSet<String> keyset = new TreeSet<String>();
			keyset.addAll(answer.keySet());
			Iterator<String> iter = keyset.iterator();
			while (iter.hasNext()) {
				String lineTag = iter.next();
				bufferedWriter.write(lineTag + "\t" + answer.get(lineTag) + "\n");
			}
		} finally {
			bufferedWriter.close();
		}
		return;
	}

	/**
	 * This function loads stopwords from an external file
	 * 
	 * @param filePath
	 * @return stopwords
	 * @throws IOException
	 */
	public static Set<String> stopwordsLoader(String filePath) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
		Set<String> stopwords = new HashSet<String>();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				stopwords.add(line);
			}
		} finally {
			bufferedReader.close();
		}
		return stopwords;
	}

	/**
	 * Reads a line and tokenize it
	 * 
	 * @param line,
	 *            stopwords
	 * @return TokenizedLine
	 */
	public static TokenizedLine lineParser(String line, Set<String> stopwords) {
		TokenizedLine tl = new TokenizedLine();
		String temp = line.toLowerCase().replaceAll("\\p{P}", "");
		List<String> array = Arrays.asList(temp.split("\\s+"));
		tl.setLineTag(array.get(0));
		int beforeWordLocation = array.indexOf(">>");

		// build taggedword library
		HashMap<Integer, String> taggedWords = tl.getTaggedWords();
		HashMap<Integer, String> sanitizedTaggedWords = tl.getSanitizedTaggedWords();
		HashMap<String, Integer> sanitizedWordCounter = tl.getSanitizedWordsCounter();
		int tagCounter = -1;
		int sanitizedTagCounter = -1;
		
		// tags the words according to the location relative to the tested word
		// and also count the number of times a word appear in a line
		for (int i = beforeWordLocation - 1; i > 0; i--) {
			String word = array.get(i);
			taggedWords.put(tagCounter, word);
			tagCounter--;
			if (!stopwords.contains(word)) {
				sanitizedTaggedWords.put(sanitizedTagCounter, word);
				if (sanitizedWordCounter.containsKey(word)) {
					// Counts the number of same word
					sanitizedWordCounter.put(word, sanitizedWordCounter.get(word) + 1);
				} else {
					sanitizedWordCounter.put(word, 1);
				}
				sanitizedTagCounter--;
			}
		}

		int afterWordLocation = array.indexOf("<<");
		tagCounter = 1;
		sanitizedTagCounter = 1;
		for (int i = afterWordLocation + 1; i < array.size(); i++) {
			String word = array.get(i);
			taggedWords.put(tagCounter, word);
			tagCounter++;
			if (!stopwords.contains(word)) {
				sanitizedTaggedWords.put(sanitizedTagCounter, word);
				sanitizedTagCounter++;
			}
		}

		if ((afterWordLocation - beforeWordLocation) == 2)
			tl.setWordAnswer(array.get(beforeWordLocation + 1));

		// Build collocation library
		String leftFour = taggedWords.get(-4);
		if (leftFour == null)
			leftFour = "";
		String leftThree = taggedWords.get(-3);
		if (leftThree == null)
			leftThree = "";
		String leftTwo = taggedWords.get(-2);
		if (leftTwo == null)
			leftTwo = "";
		String leftOne = taggedWords.get(-1);
		if (leftOne == null)
			leftOne = "";
		String rightOne = taggedWords.get(1);
		if (rightOne == null)
			rightOne = "";
		String rightTwo = taggedWords.get(2);
		if (rightTwo == null)
			rightTwo = "";
		String rightThree = taggedWords.get(3);
		if (rightThree == null)
			rightThree = "";
		String rightFour = taggedWords.get(4);
		if (rightFour == null)
			rightFour = "";

		Collocation cLeftFour = new Collocation();
		cLeftFour.setI(-4);
		cLeftFour.setJ(-4);
		cLeftFour.getWordCode().add(leftThree);

		Collocation cLeftThree = new Collocation();
		cLeftThree.setI(-3);
		cLeftThree.setJ(-3);
		cLeftThree.getWordCode().add(leftThree);

		Collocation cLeftTwo = new Collocation();
		cLeftTwo.setI(-2);
		cLeftTwo.setJ(-2);
		cLeftTwo.getWordCode().add(leftTwo);

		Collocation cLeftOne = new Collocation();
		cLeftOne.setI(-1);
		cLeftOne.setJ(-1);
		cLeftOne.getWordCode().add(leftOne);

		Collocation cRightOne = new Collocation();
		cRightOne.setI(1);
		cRightOne.setJ(1);
		cRightOne.getWordCode().add(rightOne);

		Collocation cRightTwo = new Collocation();
		cRightTwo.setI(2);
		cRightTwo.setJ(2);
		cRightTwo.getWordCode().add(rightTwo);

		Collocation cRightThree = new Collocation();
		cRightThree.setI(3);
		cRightThree.setJ(3);
		cRightThree.getWordCode().add(rightThree);

		Collocation cRightFour = new Collocation();
		cRightFour.setI(4);
		cRightFour.setJ(4);
		cRightFour.getWordCode().add(rightFour);

		Collocation c43 = new Collocation();
		c43.setI(-4);
		c43.setJ(-3);
		c43.getWordCode().add(leftFour);
		c43.getWordCode().add(leftThree);

		Collocation c32 = new Collocation();
		c32.setI(-3);
		c32.setJ(-2);
		c32.getWordCode().add(leftThree);
		c32.getWordCode().add(leftTwo);

		Collocation c21 = new Collocation();
		c21.setI(-2);
		c21.setJ(-1);
		c21.getWordCode().add(leftTwo);
		c21.getWordCode().add(leftOne);

		Collocation c11 = new Collocation();
		c11.setI(-1);
		c11.setJ(1);
		c11.getWordCode().add(leftOne);
		c11.getWordCode().add(rightOne);

		Collocation c12 = new Collocation();
		c12.setI(1);
		c12.setJ(2);
		c12.getWordCode().add(rightOne);
		c12.getWordCode().add(rightTwo);

		Collocation c23 = new Collocation();
		c23.setI(2);
		c23.setJ(3);
		c23.getWordCode().add(rightTwo);
		c23.getWordCode().add(rightThree);

		Collocation c34 = new Collocation();
		c34.setI(3);
		c34.setJ(4);
		c34.getWordCode().add(rightThree);
		c34.getWordCode().add(rightFour);

		Collocation c432 = new Collocation();
		c432.setI(-4);
		c432.setJ(-2);
		c432.getWordCode().add(leftFour);
		c432.getWordCode().add(leftThree);
		c432.getWordCode().add(leftTwo);

		Collocation c321 = new Collocation();
		c321.setI(-3);
		c321.setJ(-1);
		c321.getWordCode().add(leftThree);
		c321.getWordCode().add(leftTwo);
		c321.getWordCode().add(leftOne);

		Collocation c211 = new Collocation();
		c211.setI(-2);
		c211.setJ(1);
		c211.getWordCode().add(leftTwo);
		c211.getWordCode().add(leftOne);
		c211.getWordCode().add(rightOne);

		Collocation c112 = new Collocation();
		c112.setI(-1);
		c112.setJ(2);
		c112.getWordCode().add(leftOne);
		c112.getWordCode().add(rightOne);
		c112.getWordCode().add(rightTwo);

		Collocation c123 = new Collocation();
		c123.setI(1);
		c123.setJ(3);
		c123.getWordCode().add(rightOne);
		c123.getWordCode().add(rightTwo);
		c123.getWordCode().add(rightThree);

		Collocation c234 = new Collocation();
		c234.setI(2);
		c234.setJ(4);
		c234.getWordCode().add(rightTwo);
		c234.getWordCode().add(rightThree);
		c234.getWordCode().add(rightFour);

		// Adds all generated collocation to the collocation data
		tl.getCollocationData().add(cLeftFour);
		tl.getCollocationData().add(cLeftThree);
		tl.getCollocationData().add(cLeftTwo);
		tl.getCollocationData().add(cLeftOne);
		tl.getCollocationData().add(cRightOne);
		tl.getCollocationData().add(cRightTwo);
		tl.getCollocationData().add(cRightThree);
		tl.getCollocationData().add(cRightFour);

		tl.getCollocationData().add(c43);
		tl.getCollocationData().add(c32);
		tl.getCollocationData().add(c21);
		tl.getCollocationData().add(c11);
		tl.getCollocationData().add(c12);
		tl.getCollocationData().add(c23);
		tl.getCollocationData().add(c34);
		tl.getCollocationData().add(c432);
		tl.getCollocationData().add(c321);
		tl.getCollocationData().add(c211);
		tl.getCollocationData().add(c112);
		tl.getCollocationData().add(c123);
		tl.getCollocationData().add(c234);
		return tl;
	}

	public static Model modelDeepCopy(Model orig) {
		Model obj = null;
		try {
			// Write the object out to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(orig);
			out.flush();
			out.close();

			// Make an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			obj = (Model) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return obj;
	}

}
