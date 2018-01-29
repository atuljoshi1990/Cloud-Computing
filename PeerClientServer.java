import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
/**
Initial program.
This part will accept information from user and make connections with other peers.
Initial thread are also created and started.
*/
public class PeerClientServer {
	Scanner inputKeyboard;
	static int PORT = 0;
	static String IP = "";
	static int PEER_Id = 0;
	static Socket sSocket = null;
	static int requestCount = 0;
	static BufferedWriter bw = null;
	static Map<Integer, String> staticRoutingTable = new HashMap<Integer, String>();

	public PeerClientServer() {
		System.out.println("Pick routing table type. 1 for RANDOM, 2 for DHT.");
		inputKeyboard = new Scanner(System.in);
		String routingTableType = inputKeyboard.nextLine();
		System.out.println("Choose initial IP address to connect to.");
		inputKeyboard = new Scanner(System.in);
		String ipAddressToConnectTo = inputKeyboard.nextLine();
		System.out.println("Choose port number for communications.");
		inputKeyboard = new Scanner(System.in);
		String portNumber = inputKeyboard.nextLine();
		PORT = Integer.parseInt(portNumber);
		IP = ipAddressToConnectTo;
		int peerId = generateRandomId(65536);
		PEER_Id = peerId;
		try {
			bw = new BufferedWriter(new FileWriter("Random_"+PeerClientServer.PEER_Id+".txt", true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectToAPeer(routingTableType, ipAddressToConnectTo, portNumber, peerId);
	}

	//Helps connecting to a peer.
	@SuppressWarnings("resource")
	private void connectToAPeer(String routingTableType, String ipAddressToConnectTo, String portNumber, int peerId) {

		ServerSocket serverSocket = null;
		String hostIp = "";
		int port = 0;
		Socket socket;
		boolean isFirstPeer = false;
		//Give own information to user.
		try {
			hostIp = InetAddress.getLocalHost().getHostAddress();
			port = Integer.parseInt(portNumber);
			System.out.println("Peer Id :: " + peerId);
			System.out.println("Peer IP Address :: " + hostIp);
			System.out.println("Peer Port :: " + port);
			serverSocket = new ServerSocket(Integer.parseInt(portNumber));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//This try-catch will handle the error case for first peer.
		try {
			//Do three operations: Ping, Search, Printing routing tables.
			socket = new Socket(ipAddressToConnectTo, Integer.parseInt(portNumber));
			PingOperationThread thread1 = new PingOperationThread();
			PrintRoutingTableThread thread2 = new PrintRoutingTableThread(socket);
			SearchOperationThread thread3 = new SearchOperationThread(socket, routingTableType);
			thread1.start();
			thread2.start();
			thread3.start();
			while (true) {
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				Peer peer = new Peer();
				peer.setoType(false);
				peer.setPeerId(peerId);
				output.writeObject(peer);
				output.flush();
				break;
			}
		} catch (Exception e) {
			isFirstPeer = true;
		}
		while (true) {
			try {
				//Handing the error in case of first peer.
				if (isFirstPeer) {
					PingOperationThread thread1 = new PingOperationThread();
					PrintRoutingTableThread thread2 = new PrintRoutingTableThread(
							new Socket(hostIp, Integer.parseInt(portNumber)));
					SearchOperationThread thread3 = new SearchOperationThread(
							new Socket(hostIp, Integer.parseInt(portNumber)), routingTableType);
					thread1.start();
					thread2.start();
					thread3.start();
					isFirstPeer = false;
				}
				socket = serverSocket.accept();
				PeerServerConnectionThread thread = new PeerServerConnectionThread(socket, routingTableType);
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private int generateRandomId(int length) {

		Random rand = new Random();
		int n = rand.nextInt(length) + 1;
		return n;
	}

	public static void main(String[] args) {
		new PeerClientServer();
	}
}
