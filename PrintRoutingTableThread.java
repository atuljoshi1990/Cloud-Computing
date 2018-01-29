import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * We print the routing tables using this class. We iterate through all the peer
 * from routing table and print them in instructed format. Also, We introduce
 * the delay of 5 minutes (300000 milliseconds)
 */
class PrintRoutingTableThread extends Thread {
	@SuppressWarnings("unused")
	private Socket socket = null;

	public PrintRoutingTableThread(Socket socket) {
		super("PrintRoutingTableThread");
		this.socket = socket;
	}

	public void run() {
		try {
			while (true) {
				Iterator<Entry<Integer, String>> it = PeerClientServer.staticRoutingTable.entrySet().iterator();
				PeerClientServer.bw.write("\n****Routing Table Start****");
				PeerClientServer.bw.newLine();
				System.out.println("\n****Routing Table Start****");
				while (it.hasNext()) {
					Entry<Integer, String> pair = it.next();
					PeerClientServer.bw.write(pair.getKey() + " : " + pair.getValue());
					System.out.println(pair.getKey() + " : " + pair.getValue());
					PeerClientServer.bw.newLine();
				}
				PeerClientServer.bw.write("****Routing Table End****");
				PeerClientServer.bw.newLine();
				System.out.println("\n****Routing Table End****");
				PeerClientServer.bw.flush();

				try {
					Thread.sleep(300000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
		}
	}
}