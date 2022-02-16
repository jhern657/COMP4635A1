import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angela 
 *
 */
public class TCPServer {
	public static void main(String argv[]) throws Exception {
		String clientSentence; // This is original sentence from the client.
		String capitalSentence;
		String outputSentence = ""; // This is the decrypted sentence.
		String currentSentence = ""; // current sentence we are decrypting.
		Map<String, String> codebook = new HashMap<>(); // codebook used to decrypt.
		char current; 
		Game current_session;
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true) {
			String line; // This first part is just handling the input loading of the codebook .
			BufferedReader reader = new BufferedReader(new FileReader("words.txt"));
			line = reader.readLine();
			Socket connectionSocket = welcomeSocket.accept(); // We are establishing connection here. 
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			System.out.println("Accepted TCP connection from" 
					+ connectionSocket.getInetAddress() 
					+ ":" + connectionSocket.getPort());			    	
		
			try {
				while (true) {
					clientSentence = inFromClient.readLine(); // grabs input from client side and makes is capital.
					
					if(firstWord(clientSentence).equals("start")) {

						int level=Integer.parseInt(clientSentence.split(" ")[1]);
						int failed_attempts=Integer.parseInt(clientSentence.split(" ")[2]);
						current_session = new Game(level, failed_attempts);
						
						outToClient.writeBytes(current_session.hidden);
						
					}
					else {
						outToClient.writeBytes(clientSentence); // return if input is not part of codebook.
						
					}
				}
			} catch (Exception e) {
				// TODO: handle exception, if client closed connection, print:
				System.out.println("Client closed connection.");
			}
			welcomeSocket.close();
			reader.close();
		}

	}
		
	
	// Method that returns the first word
	public static String firstWord(String input) {
	    return input.split(" ")[0]; // Create array of words and return the 0th word
	}	
	
	
		/** isPunctuation
		 * @param character
		 * @return true, if character is considered Punctuation, false otherwise.
		 */
		public static boolean isPunctuation(char character) {
			return (((character >= '!' && character <= '.') || (character >= ':' && character <= '@')) && character != '/');
	}
		
		/** needsSpace
		 * @param outputSentence
		 * @return returns original string with an added space if needed.
		 */
		public static String needsSpace(String outputSentence) {
			if(outputSentence != "") {
				if( outputSentence.substring(outputSentence.length() - 1) != " ") {
					outputSentence += " ";
				}
			}
			return outputSentence;
		}
		
		/**isInput
		 * @param character 
		 * @return true, if character is translatable input, false otherwise
		 */
		public static boolean isInput(char character) {
			return (Character.isDigit(character) || Character.isUpperCase(character) || character == '/');
		}
		
}