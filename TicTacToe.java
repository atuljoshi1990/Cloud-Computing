import java.io.Serializable;

@SuppressWarnings("serial")
public class TicTacToe implements Serializable{

	String player;
	int row;
	int column;
	boolean playerConnected;
	String msg;
	String[][] grid;
	int gameStatus;

	public TicTacToe(String player, int row, int column) {
		this.player = player;
		this.row = row;
		this.column = column;
	}
	
	public TicTacToe() {}
	
	public int getGameStatus() {
		return gameStatus;
	}
	public void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
	}
	public String[][] getGrid() {
		return grid;
	}
	public void setGrid(String[][] grid) {
		this.grid = grid;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	public boolean isPlayerConnected() {
		return playerConnected;
	}
	public void setPlayerConnected(boolean playerConnected) {
		this.playerConnected = playerConnected;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
}
