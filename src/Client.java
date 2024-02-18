package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

	public volatile ArrayList<String> clients = new ArrayList<>();

	private Socket socket = null;
	private BufferedReader bufRead = null;
	private BufferedWriter bufWrite = null;
	private String userName;

	public static void main(String[] args) {

		Scanner inputText = new Scanner(System.in);
		System.out.println("Enter unique username: ");
		String userName = inputText.nextLine();
		try {
			Socket socket = new Socket("localhost", 4999);
			Client client = new Client(socket, userName);
			client.receiver();
			client.sender();
		} catch (Exception e) {
			System.out.println("Server is offline");
			System.exit(0);
		}

	}

	public Client(Socket socket, String userName) {
		try {

			this.socket = socket;
			this.bufRead = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			this.bufWrite = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			this.userName = userName;

		} catch (IOException e) {
			closeAllSreams();
		}

	}

	public void sender() {
		try {
			bufWrite.write(userName);
			bufWrite.newLine();
			bufWrite.flush();

			Scanner inputText = new Scanner(System.in);
			while (socket.isConnected()) {
				String msg = inputText.nextLine();
				bufWrite.write(msg);
				bufWrite.newLine();
				bufWrite.flush();
			}
		} catch (IOException e) {
			closeAllSreams();
		}
	}

	public void receiver() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					// message from server
					String msg;

					while (socket.isConnected()) {
						msg = bufRead.readLine();
						if (msg.equals("##USERNAMETAKEN")) {
							System.out.println("User name taken!\nTry again");
							closeAllSreams();
						}
						if (msg.length() > 14) {
							if ((msg.substring(0, 14)).equals("##ClientJoin: ")) {
								clients.add(msg.substring(14));
								System.out.println(msg.substring(14) + " joined");
							}
							if ((msg.substring(0, 14)).equals("##ClientLeft: ")) {
								clients.remove(msg.substring(14));
								System.out.println(msg.substring(14) + " left");
							}

						}
						if (msg.charAt(0) != '#') {
							System.out.println(msg);
						}
					}
				} catch (Exception e) {
					closeAllSreams();
				}
			}
		}).start();
	}

	public void closeAllSreams() {
		System.out.println("Server disconnected");
		try {
			if (bufRead != null)
				bufRead.close();
			if (bufWrite != null)
				bufWrite.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
