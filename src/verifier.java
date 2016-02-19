import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is a simple line by line comparer verification file
 * 
 * @author ignatius damai a0088455r
 *
 */
public class verifier {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("2arguments expected: <template_answer_file> <answer_file>");
			System.out.println(String.format("Got: %s", Arrays.toString(args)));
			return;
		}
		String template_answer_file = args[0];
		String answer_file = args[1];
		BufferedReader bufferedReader = new BufferedReader(new FileReader(template_answer_file));
		ArrayList<String> answerLines = new ArrayList<String>();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				answerLines.add(line);
			}
		} finally {
			bufferedReader.close();
		}

		bufferedReader = new BufferedReader(new FileReader(answer_file));
		ArrayList<String> answerCalcLines = new ArrayList<String>();
		try {
			while ((line = bufferedReader.readLine()) != null) {
				answerCalcLines.add(line);
			}
		} finally {
			bufferedReader.close();
		}

		int correctCounter = 0;
		for (int i = 0; i < answerCalcLines.size(); i++) {
			// use simple counter to cound how many answers are correct
			if (answerCalcLines.get(i).equals(answerLines.get(i))) {
				correctCounter++;
			}
		}
		double percentageCorrect = ((double) correctCounter) / ((double) answerLines.size());
		System.out.println(percentageCorrect);
	}
}
