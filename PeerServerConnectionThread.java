import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This thread accepts all the inputs from other peers. Depending on the
 * operation the task is performed. Ping response is sent. Search operation is
 * performed either by forwarding the message or accepting the search results.
 */
class PeerServerConnectionThread extends Thread {
	private Socket socket = null;
	private String routingTableType = "";

	public PeerServerConnectionThread(Socket socket, String routingTableType) {
		super("PeerServerConnectionThread");
		this.socket = socket;
		this.routingTableType = routingTableType;
	}

	public void run() {
		ObjectInputStream input;
		RoutingType routingType = null;
		String ipAddressToConnectTo = "";
		try {
			while (true) {
				try {
					input = new ObjectInputStream(socket.getInputStream());
					Peer peer = (Peer) input.readObject();
					// Ping receiver
					if (!peer.isoType()) {
						ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
						Peer peerNewObj = new Peer();
						peerNewObj.setPeerId(PeerClientServer.PEER_Id);
						String ip = socket.getInetAddress() + "";
						ip = ip.substring(1);
						peerNewObj.setAddress(ip);
						output.writeObject(peerNewObj);
						output.flush();
						if ("1".equals(routingTableType)) {
							routingType = new RandomRouting();
							routingType.addPeerToRoutingTable(peer.getPeerId(), ip);
						} else {
							routingType = new DHTRouting();
							routingType.addPeerToRoutingTable(peer.getPeerId(), ip);
						}
						PeerClientServer.requestCount = PeerClientServer.staticRoutingTable.size();
						break;
					} else {
						// Search operation receiver

						peer.setHops(peer.getHops() + 1);// incease number of
															// hops
						int searchId = peer.getRandomSearchId();
						int closestN = getClosestNeighbour(searchId);
						String ip = socket.getInetAddress() + "";
						ip = ip.substring(1);

						// If source has received the msg it has sent before
						if (peer.getPeerId() == PeerClientServer.PEER_Id) {
							RoutingType routingTable = new RandomRouting();
							if (peer.getLastPId() != PeerClientServer.PEER_Id
									|| !(peer.getLastIP()).equals(PeerClientServer.IP)) {
								routingTable.addPeerToRoutingTable(peer.getLastPId(), peer.getLastIP());
							}
							peer.setoType(false);
							peer.setHops(peer.getHops() - 1);
							long timeTaken = System.currentTimeMillis() - peer.getCurrTime();
							PeerClientServer.requestCount = PeerClientServer.staticRoutingTable.size();
							System.out.println("SEARCH OPERATION :: " + peer.getPeerId() + " : " + peer.getMessageId()
									+ " : " + peer.getRandomSearchId() + " : " + peer.getHops() + " : " + timeTaken
									+ " : " + peer.getLastPId());
							PeerClientServer.bw.write("SEARCH OPERATION :: " + peer.getPeerId() + " : "
									+ peer.getMessageId() + " : " + peer.getRandomSearchId() + " : " + peer.getHops()
									+ " : " + timeTaken + " : " + peer.getLastPId());
							PeerClientServer.bw.newLine();
							PeerClientServer.bw.flush();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							continue;

						} else if (PeerClientServer.requestCount >= 2) {
							// Forward to next suitable peer.
							ipAddressToConnectTo = PeerClientServer.staticRoutingTable.get(closestN);

							if (ipAddressToConnectTo.equals(peer.getLastIP())) {
								ipAddressToConnectTo = peer.getAddress();
								peer.setHops(peer.getHops() - 1);
							}
							PeerClientServer.requestCount = PeerClientServer.requestCount - 1;
						} else if (PeerClientServer.requestCount == 1) {
							// Received by the node with just on peer in routing
							// table
							// It will send the msg to original sender.
							peer.setoType(true);
							peer.setTimeTaken(System.currentTimeMillis() - peer.getCurrTime());
							ipAddressToConnectTo = peer.getAddress();
							RoutingType routingTable = new RandomRouting();
							if (peer.getPeerId() != PeerClientServer.PEER_Id) {
								routingTable.addPeerToRoutingTable(peer.getPeerId(), peer.getAddress());
							}
							peer.setLastIP(InetAddress.getLocalHost().getHostAddress());
							peer.setLastPId(PeerClientServer.PEER_Id);
						}
						socket = new Socket(ipAddressToConnectTo, PeerClientServer.PORT);
						ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
						output.writeObject(peer);
						output.flush();
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This function gives us suitable neighbour to which we need to send the
	 * message. We search the whole routing table and find out the closest
	 * neighbour.
	 */
	private int getClosestNeighbour(int randomSearchId) {

		Iterator<Entry<Integer, String>> it = PeerClientServer.staticRoutingTable.entrySet().iterator();
		int diff = 767665560;
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
}