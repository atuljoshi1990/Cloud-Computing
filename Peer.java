import java.io.Serializable;

@SuppressWarnings("serial")

/**
This is a Plain Old Java Object.
The Peer object has following variables which contain all 
the information to be transferred between two peers.
ALl the getters and setters help in assigning the values to
fields and fetching the values from them.
*/


public class Peer implements Serializable {

	private int peerId;
	private String address;
	private int port;
	private String message;
	private int hops;
	private int messageId;
	private int randomSearchId;
	private long timeTaken;
	private long currTime;
	boolean oType;
	private int lastPId;
	private String lastIP;

	public String getLastIP() {
		return lastIP;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public int getLastPId() {
		return lastPId;
	}

	public void setLastPId(int lastPId) {
		this.lastPId = lastPId;
	}

	public boolean isoType() {
		return oType;
	}

	public void setoType(boolean oType) {
		this.oType = oType;
	}

	public long getCurrTime() {
		return currTime;
	}

	public void setCurrTime(long currTime) {
		this.currTime = currTime;
	}

	public int getHops() {
		return hops;
	}

	public void setHops(int hops) {
		this.hops = hops;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getRandomSearchId() {
		return randomSearchId;
	}

	public void setRandomSearchId(int randomSearchId) {
		this.randomSearchId = randomSearchId;
	}

	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPeerId() {
		return peerId;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public void setPeerId(int peerId2) {
		this.peerId = peerId2;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPort(int port) {
		this.port = port;
	}
}