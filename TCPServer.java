import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Angela
 *
 */
public class TCPServer {
	public static void main(String argv[]) throws Exception {

		ServerSocket welcomeSocket = new ServerSocket(6789);
		welcomeSocket.setReuseAddress(true);

		while (true) {
			BufferedReader reader = new BufferedReader(new FileReader("words.txt"));


			try {
			    ExecutorService gameThread = Executors.newFixedThreadPool(20);

	          while (true) {
	              Socket connectionSocket = welcomeSocket.accept(); // We are establishing connection here.
	              System.out.println("Accepted TCP connection from"
	                      + connectionSocket.getInetAddress()
	                      + ":" + connectionSocket.getPort());

	              ReverseEchoClientHandler echo = new ReverseEchoClientHandler(connectionSocket);

	              gameThread.execute(echo);


				}

			}
			catch (IOException e) {
	            System.out.println(
	                    "Exception caught when trying to listen on port " + welcomeSocket + " or listening for a connection");
	            System.out.println(e.getMessage());
	        }
			catch (Exception e) {
				// TODO: handle exception, if client closed connection, print:
				System.out.println("Client closed connection.");
			}

			welcomeSocket.close();
			reader.close();
		}

	}


	//handle multiple players simultaneously
	private static class ReverseEchoClientHandler implements Runnable{
	    private Socket clientSocket;

        public ReverseEchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            String clientSentence; // This is original sentence from the client.
            Game current_session = null;

         // Default game settings
            int level = 1;
            int failed_attempts = 1;

            System.out.println("Connected, handling new client: " + clientSocket);

            try {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

                // run game
                while(true) {

                    clientSentence = inFromClient.readLine(); // grabs input from client side and makes is capital.

                    if(firstWord(clientSentence).equals("start")) {

                        level=Integer.parseInt(clientSentence.split(" ")[1]);
                        failed_attempts=Integer.parseInt(clientSentence.split(" ")[2]);
                        current_session = new Game(level, failed_attempts);

                        outToClient.writeBytes(current_session.hidden);


                    }
                    else if (clientSentence.equals("*")) {

                        current_session = new Game(level, failed_attempts);
                        outToClient.writeBytes(current_session.hidden);

                    }
                    else if (clientSentence.charAt(0) == '?') {

                        String word = clientSentence.substring(1);
                        String message = current_session.word_lookup(word);
                        outToClient.writeBytes(message + '\n');

                    }
                    else if (clientSentence.length() == 1){ // handles one letter guesses

                        // Handle character guess
                        current_session.guess(clientSentence.charAt(0));
                        outToClient.writeBytes(current_session.hidden);
                    }
                    else { //handles whole word guesses
                        current_session.guess(clientSentence);
                        outToClient.writeBytes(current_session.hidden);
                    }

                }

            } catch (SocketException e) {
                System.out.println("Error: " + e.getMessage());
            }catch (NullPointerException e) {
                System.out.println("Game Ended");
            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                }
                System.out.println("Closed: " + clientSocket);
            }
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