import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Game {
	
	int level;
	int failed_attempts;
	String line;
	BufferedReader reader;
	
	public Game(int input_level, int input_failed_attempts) {
		level = input_level;
		failed_attempts = input_failed_attempts;
		try {
			reader = new BufferedReader(new FileReader("words.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
