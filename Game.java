import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.Scanner;

public class Game {

	int level;
	int failed_attempts;
	int f_a_counter;
	String line;
	RandomAccessFile reader;
	String phrase;
	public String hidden;


	static final int NUM_OF_WORDS = 240000;

	public Game(int input_level, int input_failed_attempts) {
		level = input_level;
		failed_attempts = input_failed_attempts;
		f_a_counter = level * failed_attempts;

		try {
			reader = new RandomAccessFile("words.txt", "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		phrase = assemble_phrase(level, reader);

		hidden = hide_phrase(phrase);

		System.out.println(hidden);
		System.out.println("phrase = " + phrase);
	}

	//asks for i(level) random words from the word repository,
	// and concatenates them to create the phrase for a game round
	public static String assemble_phrase(int level, RandomAccessFile reader) {

		Random random = new Random();
		int randomNum = random.nextInt(NUM_OF_WORDS);
		StringBuilder phrase = new StringBuilder();

		for(int i = 0; i < level; i++) {
			// Grab one word and append it to the phrase
			try {
				// Move to a random spot
				randomNum = random.nextInt(NUM_OF_WORDS);
				reader.seek(randomNum);

				// Read until end of word
				reader.readLine();

				// Append word to phrase
				phrase.append(reader.readLine());

				// Add space if needed
				if(i < level - 1)
					phrase.append(" ");
			} catch(IOException e) {
				System.out.println("Failed to seek word =(");
				e.printStackTrace();
			}
		}


		return phrase.append("\n").toString();

	}

	// hide words behind '-' and show failed attempt counter
	public String hide_phrase(String phrase) {

	    char[] letters = phrase.toCharArray();
	    System.out.println(phrase);
	    StringBuilder hidden = new StringBuilder();
	    String words = "";

	    for (int i=0; i < letters.length; i++) {

	    	if(letters[i] == ' ') {

	             hidden.append(" ");

	        } else if(letters[i] != '\n') {
	        	hidden.append("-");
	        }
	        else {
	        	// set failed attempt counter
	            words = update_counter(hidden.toString());
//	        	hidden.append("C" + f_a_counter + '\n');
	        }

	    }

	     return words;

	}

	public String update_counter(String hidden) {

	    StringBuilder hidden_copy = new StringBuilder();

	    for(int i = 0; i < hidden.length()-1; i++) {
	        hidden_copy.append(hidden.charAt(i));
	    }

	    //need to replace append with .replace to override the C counter
	    hidden_copy.append("C" + f_a_counter + "\n");

	    hidden = hidden_copy.toString();

	    return hidden;
	}

	public void guess(char letter) {

		char[] letters = phrase.toCharArray();
		StringBuilder updated = new StringBuilder();

		if(!phrase.contains(String.valueOf(letter))) {
			f_a_counter--;
			hidden = update_counter(hidden);

		} else {

			// Goes through phrase
			for(int i = 0; i < hidden.length() ; i++) {

				if(i < phrase.length() && letters[i] == letter) {
					// Copy letter to hidden
					updated.append(letter);

				} else {
					updated.append(hidden.charAt(i));
				}
			}


			hidden = updated.append('\n').toString();
		}

		return;
	}

	public String word_lookup(String word) {

		try {

		Scanner scanner = new Scanner(new File("words.txt"));
		String currentLine;

		while(scanner.hasNextLine())
		{
			currentLine = scanner.nextLine();

		    if(currentLine.contains(word))
		    {
		         return "Word found!";
		    }
		}

		scanner.close();

		} catch (Exception e) {
			System.out.println("Exception!" + e);
		}

		return "Not found!";

	}


}
