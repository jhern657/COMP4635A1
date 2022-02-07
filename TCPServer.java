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

		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true) {
			String line; // This first part is just handling the input loading of the codebook .
			BufferedReader reader = new BufferedReader(new FileReader("codebook.txt"));
			line = reader.readLine();
			while (line != null) // codebook will have to be in the format of [code]'\t'[translation]'\n' for this to work.
		    {
		        String[] parts = line.split("\t", 2);
		        if (parts.length >= 2)
		        {
		            String key = parts[0];
		            String value = parts[1];
		            codebook.put(key, value);
		        } else {
		            System.out.println("ignoring line: " + line);
		        }
		        line = reader.readLine();
		    }

			Socket connectionSocket = welcomeSocket.accept(); // We are establishing connection here. 
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			System.out.println("Accepted TCP connection from" 
					+ connectionSocket.getInetAddress() 
					+ ":" + connectionSocket.getPort());			    	
		
			try {
				while (true) {
					clientSentence = inFromClient.readLine(); // grabs input from client side and makes is capital.
					capitalSentence = clientSentence.toUpperCase();
					Character[] clientSentenceChars = 
							capitalSentence.chars().mapToObj(c -> (char)c).toArray(Character[]::new); // turns string into char array
					
					for(int i=0; i < clientSentenceChars.length; i++) {
						current = clientSentenceChars[i];
						
						if(isPunctuation(current)) {						
							outputSentence = needsSpace(outputSentence);
							if(codebook.get(currentSentence) == null) {
								outputSentence += currentSentence + current;
							}
							else {	
								outputSentence += codebook.get(currentSentence) + clientSentenceChars[i];
							}
							currentSentence = "";
						}
						
						else if(current == ' ' && codebook.get(currentSentence)!= null) {
							outputSentence = needsSpace(outputSentence);
							outputSentence += codebook.get(currentSentence);
							currentSentence = "";
						}
						
						else if(isInput(current)) {
								currentSentence += current;
						}
						else {
							if(currentSentence != "") {
								outputSentence = needsSpace(outputSentence);
							}
							outputSentence += currentSentence;
							currentSentence = "";
						}
					}
					
					if(outputSentence!="") {

						outToClient.writeBytes(outputSentence + '\n'); // return translated message
						outputSentence = "";
					}
					else {
						outToClient.writeBytes(clientSentence + " (undecrypted)" + '\n'); // return if input is not part of codebook.
						
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