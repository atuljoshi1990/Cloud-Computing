import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

/**
This class/thread is initiator of search operation at each peer.
We send following information to suitable peer:
Address of source
PeerID of source
Number of hops
Message ID
Type of message (in this case search)
Last IP address visited
Random searchID generated
Current system time
*/
class SearchOperationThread extends Thread {
	private Socket socket = null;
	private String routingTableType = "";

	public SearchOperationThread(Socket socket, String routingTableType) {
		super("SearchOperationThread");
		this.socket = socket;
		this.routingTableType = routingTableType;
	}

	public void run() {
		if (routingTableType.equals("1")) {
			try {
				int randomSearchId = 0;
				int messageId = 100;
				int hops = 1;
				int closestNeighbour = 0;
				String ipAddressToConnectTo = "";
				while (PeerClientServer.staticRoutingTable.isEmpty()) {
					Thread.sleep(30000);
				}
				while (true) {
					PeerClientServer.requestCount = PeerClientServer.staticRoutingTable.size();
					randomSearchId = generateRandomId(65536);
					closestNeighbour = getClosestNeighbour(randomSearchId);
					ipAddressToConnectTo = PeerClientServer.staticRoutingTable.get(closestNeighbour);
					socket = new Socket(ipAddressToConnectTo, PeerClientServer.PORT);
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					String ip = InetAddress.getLocalHost().getHostAddress();
					Peer peer = new Peer();
					peer.setAddress(ip);
					peer.setPeerId(PeerClientServer.PEER_Id);
					peer.setMessageId(messageId);
					peer.setHops(hops);
					peer.setCurrTime(System.currentTimeMillis());
					peer.setoType(true);
					peer.setLastIP(ip);
					peer.setRandomSearchId(randomSearchId);
					messageId++;
					output.writeObject(peer);
					output.flush();
					Thread.sleep(600000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}

	/**
	This function gives us suitable neighbour to which we need to send the message.
	We search the whole routing table and find out the closest neighbour.
	*/
	private int getClosestNeighbour(int randomSearchId) {

		Iterator<Entry<Integer, String>> it = PeerClientServer.staticRoutingTable.entrySet().iterator();
		int diff = 65560;
		int closestN = 0;
		while (it.hasNext()) {
			Entry<Integer, String> pair = it.next();
			int d = Math.abs(randomSearchId - pair.getKey());
			if (d < diff) {
				diff = d;
				closestN = pair.getKey();
			}
		}
		return closestN;
	}
	/**
	Generated the random ID from : 1 to length : range.
	*/
	private int generateRandomId(int length) {

		Random rand = new Random();
		int n = rand.nextInt(length) + 1;
		return n;
	}
}