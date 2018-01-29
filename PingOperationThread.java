import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This class helps in ping operation. We send following information to all
 * neighbors Source IP Source ID
 * 
 */
class PingOperationThread extends Thread {

	public PingOperationThread() {
		super("PingOperationThread");
	}

	public void run() {

		String ipAddressToConnectTo = "";
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			// Ping after every 30 seconds and iterate through all values in
			// routing table to ping
			while (true) {
				Iterator<Entry<Integer, String>> it = PeerClientServer.staticRoutingTable.entrySet().iterator();

				while (it.hasNext()) {
					Entry<Integer, String> pair = it.next();
					int peerId = pair.getKey();
					ipAddressToConnectTo = pair.getValue();
					@SuppressWarnings("resource")
					Socket socket = new Socket(ipAddressToConnectTo, PeerClientServer.PORT);
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					Peer peer = new Peer();
					peer.setAddress(ipAddressToConnectTo);
					peer.setPort(PeerClientServer.PORT);
					peer.setPeerId(PeerClientServer.PEER_Id);
					long outTime = System.currentTimeMillis();
					output.writeObject(peer);
					output.flush();
					ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
					Peer peerObj;
					try {
						peerObj = (Peer) input.readObject();
						long inTime = System.currentTimeMillis();
						peerObj.getPeerId();
						peerObj.getAddress();
						System.out.println("PING OPERATION :: " + PeerClientServer.PEER_Id + " : " + ip + " : " + peerId
								+ " : " + ipAddressToConnectTo + " : " + (inTime - outTime));
						PeerClientServer.bw.write("PING OPERATION :: " + PeerClientServer.PEER_Id + " : " + ip + " : "
								+ peerId + " : " + ipAddressToConnectTo + " : " + (inTime - outTime));
						PeerClientServer.bw.newLine();
						PeerClientServer.bw.flush();

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				Thread.sleep(30000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}