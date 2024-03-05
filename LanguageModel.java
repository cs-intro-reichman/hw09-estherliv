import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		String window = "";
        char c;
        In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) { 
            char temp = in.readChar();
            window += temp;
        }
        while (!in.isEmpty()) {
            c = in.readChar();
            List probs = CharDataMap.get(window);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(c);
            window = (window + c).substring(1);
        }
        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		ListIterator iterator = new ListIterator(probs.getFirstNode());
        if (!iterator.hasNext()) {
            return;
        }
        int totalCount = 0;
        while (iterator.hasNext()) {
            CharData current = iterator.next();
            totalCount += current.count;
        }
        iterator = new ListIterator(probs.getFirstNode());
        double prev = 0;
        while (iterator.hasNext()) {
            CharData current = iterator.next();
            current.p = ((double)current.count) / totalCount;
            current.cp = current.p + prev;
            prev = current.cp;
        }
    }

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		calculateProbabilities(probs);
        ListIterator iterator = new ListIterator(probs.getFirstNode());
        double r = this.randomGenerator.nextDouble();
        CharData first = probs.getFirstNode().cp;
        char top =  first.chr;
        if (first.cp > r) {
            return top;
        }
        while (iterator.hasNext()) {
            CharData current = iterator.next();
            double val = current.cp;
            if (val > r) {
                return current.chr;
            }
        }
        return '_';
    }

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		if (initialText.length() < windowLength) {
            return initialText;
        }
        String window = initialText.substring(initialText.length() - windowLength);
        String generatedText = window;
        int counter = textLength + windowLength;
        while (generatedText.length() < counter) {
            List probabilitieslist = CharDataMap.get(window);
            if (probabilitieslist == null) {
                break;
            }
            char nextChar = getRandomChar(probabilitieslist);
            generatedText += nextChar;
            window = generatedText.substring(generatedText.length() - windowLength);
        }
        return generatedText;
    }

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		int lengthOfWindow = Integer.parseInt(args[0]);
        String initialString = args[1];
        int lengthOfGeneratedText = Integer.parseInt(args[2]);
        boolean isRandomGeneration = args[3].equals("random");
        String filePath = args[4];

        LanguageModel languageModel;
        if (isRandomGeneration) {
            languageModel = new LanguageModel(lengthOfWindow);
        } else {
            languageModel = new LanguageModel(lengthOfWindow, 20);
        }

        languageModel.train(filePath);
        System.out.println(languageModel.generate(initialString, lengthOfGeneratedText));
    }
}
