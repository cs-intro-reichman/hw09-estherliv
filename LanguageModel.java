import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of character data objects.
    HashMap<String, List> CharDataMap;

    // The window length used in this model.
    int windowLength;

    // The random number generator used by this model.
    private Random randomGenerator;

    /**
     * Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the
     * same seed value will produce the same random texts. Good for debugging.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
    public void train(String fileName) {
        String window = "";
        char chr;
        In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) {
            window = window + in.readChar();
        }
        while (!in.isEmpty()) {
            chr = in.readChar();
            List probs = CharDataMap.get(window);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(chr);
            window = window + chr;
            window = window.substring(1);
        }
        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probs) {
        int totalChars = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            CharData curr = probs.get(i);
            totalChars += curr.count;
        }
        double totalProb = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            CharData curr = probs.get(i);
            curr.p = (double) curr.count / totalChars;
            totalProb += curr.p;
            curr.cp = totalProb;
        }
    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        double random = randomGenerator.nextDouble();
        ListIterator itr = probs.listIterator(0);
        while (itr.hasNext()) {
            CharData curr = itr.next();
            if (random <= curr.cp) {
                return curr.chr;
            }
        }
        return probs.get(probs.getSize() - 1).chr;
    }

    /**
     * Generates a random text, based on the probabilities that were learned during
     * training.
     * 
     * @param initialText     - text to start with. If initialText's last substring
     *                        of size numberOfLetters
     *                        doesn't appear as a key in Map, we generate no text
     *                        and return only the initial text.
     * @param numberOfLetters - the size of text to generate
     * @return the generated text
     */
    public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength){
            return initialText;
        }
        String window = initialText.substring(initialText.length() - windowLength);
        StringBuilder generatedText = new StringBuilder(window);
        for (int i = 0; i < textLength; i++) {
            List probs = CharDataMap.get(window);
            if (probs == null) {
                return generatedText.toString();
            } else {
                char chr = getRandomChar(probs);
                generatedText.append(chr);
                window = generatedText.substring(generatedText.length() - windowLength);
            }
        }
        return generatedText.toString();
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
        int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int lengthGeneratedText = Integer.parseInt(args[2]);
        Boolean randomGenerate = args[3].equals("random");
        String fileName = args[4];
        LanguageModel lm;
        if (randomGenerate){
            lm = new LanguageModel(windowLength);
        } else {
            lm = new LanguageModel(windowLength, 20);
        }
        lm.train(fileName);
        System.out.println(lm.generate(initialText,lengthGeneratedText));
    }
}