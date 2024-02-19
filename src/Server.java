package src;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

	private ServerSocket serverSocket;

	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public static void main(String[] args) throws IOException {

		ServerSocket serverSocket = new ServerSocket(4999);
		Server server = new Server(serverSocket);
		server.startServerSocket();
	}

	public void startServerSocket() {

		try {
			while (!serverSocket.isClosed()) {

				Socket clientSocket = serverSocket.accept();

				ClientManager client = new ClientManager(clientSocket);

				Thread newThread = new Thread(client);
				newThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ClientManager implements Runnable {

	public static ArrayList<ClientManager> clients = new ArrayList<>();
	public static ArrayList<String> usernames = new ArrayList<>();
	private Socket clientSocket;
	private BufferedReader bufRead;
	private BufferedWriter bufWrite;
	private String username;
	// private volatile Boolean canStart = false;

	public ClientManager(Socket clientSocket) {

		try {
			this.clientSocket = clientSocket;
			bufRead = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			bufWrite = new BufferedWriter(new OutputStreamWriter(
					clientSocket.getOutputStream()));
			username = bufRead.readLine();

			if (usernames.contains(username)) {
				bufWrite.write("##USERNAMETAKEN");
				bufWrite.newLine();
				bufWrite.flush();
				closeAllStreams();
			}

			clients.add(this);
			usernames.add(username);

			System.out.println(username + " connected");

			bufWrite.write("/==Active Users==");
			bufWrite.newLine();
			bufWrite.flush();

			for (String username_ : usernames) {
				bufWrite.write('/' + username_);
				bufWrite.newLine();
				bufWrite.flush();
			}

			bufWrite.write("/================");
			bufWrite.newLine();
			bufWrite.flush();

			for (ClientManager client_ : clients) {
				try {
					if (!client_.username.equals(username)) {
						client_.bufWrite.write("##ClientJoin: " + username);
						client_.bufWrite.newLine();
						client_.bufWrite.flush();

					}
				} catch (IOException e) {

				}
			}

		} catch (Exception e) {

		}
		// canStart = true;
	}

	@Override
	public void run() {
		try {
			String msg;
			while (!clientSocket.isClosed()) {
				msg = bufRead.readLine();

				bufWrite.write("You: " + msg);
				bufWrite.newLine();
				bufWrite.flush();
				for (ClientManager client_ : clients) {
					if (!client_.username.equals(username)) {
						client_.bufWrite.write(username + ": " + msg);
						client_.bufWrite.newLine();
						client_.bufWrite.flush();
					}
				}

			}
		} catch (Exception e) {
			closeAllStreamsBroadcast();
		}

	}

	public void closeAllStreamsBroadcast() {

		System.out.println(username + " disconnected");
		clients.remove(this);
		usernames.remove(username);
		try {
			for (ClientManager client_ : clients) {
				if (!client_.username.equals(username)) {
					client_.bufWrite.write("##ClientLeft: " + username);
					client_.bufWrite.newLine();
					client_.bufWrite.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		closeAllStreams();

	}

	public void closeAllStreams() {
		try {

			if (bufRead != null)
				bufRead.close();
			if (bufWrite != null)
				bufWrite.close();
			if (clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
}
