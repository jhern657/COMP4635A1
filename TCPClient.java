import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 
 * @author Matt Smith, Janel Hernandez, Angela Li
 *
 * This class is the client application that uses a TCP connection to communicate to the server.
 * The server this client connects with runs a phrase guessing game.
 */
public class TCPClient {

	public static void main(String argv[]) throws Exception {
		
		if(argv.length != 2) {
			System.out.println("USAGE: java TCPClient [host] [port]");
			System.exit(1);
		}
		
		String sentence;
		String modifiedSentence;
		
		int port = 0; // 6789
		String host = "localhost";

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			
			host = argv[0];
			port = Integer.parseInt(argv[1]);

			Socket clientSocket = new Socket(host, port);
			System.out.println("Client successfully established TCP connection.\n"
					+ "Client(local) end of the connection uses port " + clientSocket.getLocalPort()
					+ " and server(remote) end of the connection uses port " + clientSocket.getPort());

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			System.out.println("Please enter level and failed attempts factor: ");
			sentence = inFromUser.readLine();

			while (sentence.toLowerCase().compareTo(".") != 0) {
				outToServer.writeBytes(sentence + '\n');

				modifiedSentence = inFromServer.readLine();

				System.out.println("FROM SERVER: " + modifiedSentence);
				sentence = inFromUser.readLine();
			}
			
			System.out.println("Thanks for playing!");

			clientSocket.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}