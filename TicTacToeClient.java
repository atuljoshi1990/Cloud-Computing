import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TicTacToeClient {
	Scanner inputKeyboard;

	public TicTacToeClient() {
		try {
			while (true) {
				Socket socket = new Socket("127.0.0.1", 12347);
				ListenThread lt = new ListenThread(socket);
				System.out.println("Please enter player type (X/O)");
				inputKeyboard = new Scanner(System.in);
				String player = inputKeyboard.nextLine();
				TicTacToe tttObj = new TicTacToe();
				tttObj.setPlayer(player);
				tttObj.setPlayerConnected(false);
				System.out.println("Request sent by Player " + tttObj.getPlayer());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				output.writeObject(tttObj);
				output.flush();
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				TicTacToe newObj = (TicTacToe) input.readObject();
				System.out.println(newObj.getMsg());
				//Check if the player is connected or not.
				if (newObj.isPlayerConnected()) {
					lt.start();
					while (true) {
						System.out.println("Please enter row number(0-2), column number(0-2).");
						System.out.println("For example, first row and first column would be: 00");
						inputKeyboard = new Scanner(System.in);
						String move = inputKeyboard.nextLine();
						//Validate if the user input is correct or not.
						if (2 == move.length()) {
							String coordinateOne = move.charAt(0) + "";
							String coordinateTwo = move.charAt(1) + "";
							boolean isInputValid = validateInput(move);
							//Validate the user input.
							if (isInputValid) {
								TicTacToe obj = new TicTacToe(newObj.getPlayer(), Integer.valueOf(coordinateOne),
										Integer.valueOf(coordinateTwo));
								obj.setPlayerConnected(true);
								ObjectOutputStream outputS = new ObjectOutputStream(socket.getOutputStream());
								outputS.writeObject(obj);
								outputS.flush();
							} else {
								System.out.println("Invalid user input !!");
							}
						} else {
							System.out.println("Invalid user input !!");
						}
					}
				}
				if (newObj.isPlayerConnected()) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean validateInput(String move) {

		boolean isInputValid = false;
		if ("00".equals(move) || "01".equals(move) || "02".equals(move) || "10".equals(move) || "11".equals(move)
				|| "12".equals(move) || "20".equals(move) || "21".equals(move) || "22".equals(move)) {
			isInputValid = true;
		}
		return isInputValid;
	}

	public static void main(String[] args) {
		new TicTacToeClient();
	}

	class ListenThread extends Thread {
		Socket socket = null;

		public ListenThread(Socket socket) {
			super("ListenThread");
			this.socket = socket;
		}

		public void run() {
			try {
				TicTacToe tttObj;
				try {
					while (true) { // wait for commands
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
						tttObj = (TicTacToe) input.readObject();
						String[][] grid = tttObj.getGrid();
						String message = "";
						if (message != null) {
							for (int i = 0; i < grid.length; i++) {
								for (int j = 0; j < grid[i].length; j++)
									System.out.print(grid[i][j] + " ");
								System.out.println();
							}
							System.out.println(tttObj.getMsg());
						}
						if (555 == tttObj.getGameStatus()) {
							System.exit(0);
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
