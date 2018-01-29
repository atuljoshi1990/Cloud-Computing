/**
Random routing table is implemented and peers are added by calling 
PeerClientServer.staticRoutingTable.put method
*/
public class RandomRouting implements RoutingType {

	@Override
	public void addPeerToRoutingTable(int peerId, String IPAddress) {

		//we check the if routing table is full or not (limit is 5)
		//No duplicates
		if (PeerClientServer.staticRoutingTable.size() < 5 && !PeerClientServer.staticRoutingTable.containsKey(peerId)
				&& peerId != 0 && !("".equals(IPAddress) || " ".equals(IPAddress) || null == IPAddress)) {
			PeerClientServer.staticRoutingTable.put(peerId, IPAddress);
		}
	}

}
