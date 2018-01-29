import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicTacToeServer {

	static Map<String, Socket> playerStreamMap = new HashMap<String, Socket>();
	static List<String> players = new ArrayList<String>();
	static String[][] tttGrid = new String[3][3];

	@SuppressWarnings("resource")
	public TicTacToeServer() {
		ServerSocket serverSocket = null;

		try {
			System.out.println("Started the server, waiting for new connections.");
			serverSocket = new ServerSocket(12347);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + 12347);
			System.exit(-1);
		}

		while (true) {
			try {
				Socket s1 = serverSocket.accept();
				ConnectionThread st1 = new ConnectionThread(s1);
				st1.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new TicTacToeServer();
	}

	class ConnectionThread extends Thread {
		private Socket socket = null;

		public ConnectionThread(Socket socket) {
			super("ConnectionThread");
			this.socket = socket;
		}

		public void run() {
			try {
				//Create an empty grid.
				for (int i = 0; i < tttGrid.length; i++) {
					for (int j = 0; j < tttGrid[i].length; j++) {
						tttGrid[i][j] = "-";
					}
				}
				while (true) {
					boolean turnPlayed = false;
					boolean gameWon = false;
					ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
					// read user from client
					TicTacToe obj = (TicTacToe) input.readObject();
					System.out.println("Request recieved from: " + obj.getPlayer());
					//Check if the player not connected already.
					if (!obj.isPlayerConnected()) {
						String client = obj.getPlayer();
						ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
						//Check if a player is trying to connect as the other player.
						//Eg: If player X is connected and the other player is trying to connect as X
						//Error will be displayed to the client.
						if ("X".equals(client) || "O".equals(client)) {
							if (players.contains(client)) {
								System.out.println(
										"Player " + client + " already connected, choose the other player type.");
								obj.setPlayerConnected(false);
								obj.setMsg("Player " + client + " already connected, choose the other player type.");
								output.writeObject(obj);
								output.flush();
							} else {
								//Connect the player to the server.
								System.out.println("Player " + client + " connected.");
								obj.setPlayerConnected(true);
								obj.setMsg("Player " + client + " connected.");
								playerStreamMap.put(obj.getPlayer(), socket);
								players.add(client);
								output.writeObject(obj);
								output.flush();
							}
						} else {
							//If the player types are wrong show error.
							System.out.println("Wrong user input, choose between X and O.");
							obj.setPlayerConnected(false);
							obj.setMsg("Wrong user input, choose between X and O.");
							output.writeObject(obj);
							output.flush();
						}
					} else {
						int row = obj.getRow();
						int cloumn = obj.getColumn();
						//If the particular coordinate is empty in the grid, fill it up.
						if ("-".equals(tttGrid[row][cloumn])) {
							tttGrid[obj.getRow()][obj.getColumn()] = obj.getPlayer();
							turnPlayed = true;
							String plyr = obj.getPlayer();
							//Condition to check if the player has won or not.
							if (tttGrid[0][0].equals(plyr) && tttGrid[0][1].equals(plyr) && tttGrid[0][2].equals(plyr)
									|| tttGrid[1][0].equals(plyr) && tttGrid[1][1].equals(plyr) && tttGrid[1][2].equals(plyr)
									|| tttGrid[2][0].equals(plyr) && tttGrid[2][1].equals(plyr) && tttGrid[2][2].equals(plyr)
									|| tttGrid[0][0].equals(plyr) && tttGrid[1][0].equals(plyr) && tttGrid[2][0].equals(plyr)
									|| tttGrid[0][1].equals(plyr) && tttGrid[1][1].equals(plyr) && tttGrid[2][1].equals(plyr)
									|| tttGrid[0][2].equals(plyr) && tttGrid[1][2].equals(plyr) && tttGrid[2][2].equals(plyr)
									|| tttGrid[0][0].equals(plyr) && tttGrid[1][1].equals(plyr) && tttGrid[2][2].equals(plyr)
									|| tttGrid[2][0].equals(plyr) && tttGrid[1][1].equals(plyr) && tttGrid[0][2].equals(plyr)) {
								System.out.println("Player " + obj.getPlayer() + " won !!!");
								String player2 = "X";
								if ("X".equals(obj.getPlayer())) {
									player2 = "O";
								}
								obj.setMsg("Player " + obj.getPlayer() + " won !!!");
								obj.setGrid(tttGrid);
								obj.setPlayerConnected(true);
								obj.setGameStatus(555);
								gameWon = true;
								ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
								ObjectOutputStream out2 = new ObjectOutputStream(
										playerStreamMap.get(player2).getOutputStream());
								out2.writeObject(obj);
								output.writeObject(obj);
								output.flush();
								out2.flush();
								System.exit(0);
							}
						}
						int count = 0;
						for (int i = 0; i < tttGrid.length; i++) {
							for (int j = 0; j < tttGrid[i].length; j++) {
								if ("X".equals(tttGrid[i][j]) || "O".equals(tttGrid[i][j])) {
									count++;
								}
							}
						}
						//Check if the grid is full and finish the game as draw.
						if (count == 9) {
							ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
							String player2 = "X";
							if ("X".equals(obj.getPlayer())) {
								player2 = "O";
							}
							ObjectOutputStream out2 = new ObjectOutputStream(
									playerStreamMap.get(player2).getOutputStream());
							obj.setMsg("Game draw !!");
							obj.setGrid(tttGrid);
							obj.setPlayerConnected(true);
							obj.setGameStatus(555);
							out2.writeObject(obj);
							count = 0;
							output.writeObject(obj);
							output.flush();
							out2.flush();
							System.exit(0);
							//Check if the player is trying to fill up the already filled up coordinate of the grid
							//and display an error in that case.
						} else if (!turnPlayed) {
							ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
							obj.setMsg("Choose a different position !!");
							obj.setGrid(tttGrid);
							obj.setPlayerConnected(true);
							output.writeObject(obj);
							output.flush();
						} else if (!gameWon) {
							String player2 = "X";
							if ("X".equals(obj.getPlayer())) {
								player2 = "O";
							}
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							ObjectOutputStream output = new ObjectOutputStream(
									playerStreamMap.get(player2).getOutputStream());
							System.out.println("Player " + player2 + " turn.");
							obj.setMsg("Player " + player2 + " turn.");
							obj.setGrid(tttGrid);
							obj.setPlayer(player2);
							obj.setPlayerConnected(true);
							output.writeObject(obj);
							out.writeObject(obj);
							output.flush();
							out.flush();
						}
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
