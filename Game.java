import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

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
	    f_a_counter = level * failed_attempts;

	    for (int i=0; i < letters.length; i++) {

//	        switch(letters[i]) {
//	            case ' ': //check if current char is a space
//	                System.out.print(" ");
//	                i++;
//	            case '\n': //check if current char is a newline character
//	                i++;
//	            default:
//	                System.out.print("-"); //replace each character with -
	              
	    	if(letters[i] == ' ') {
	    		
	             hidden.append(" ");   
	                
	        } else if(letters[i] != '\n') {
	        	hidden.append("-");
	        } else {
	        	// set failed attempt counter
	        	hidden.append("C" + f_a_counter + '\n');
	        }

	    }

	    

	     return hidden.toString();

	}




}
