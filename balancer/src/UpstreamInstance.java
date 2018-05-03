public class UpstreamInstance {

  private int lifetime_route_total;
  private DatagramSocket() socket;
  InetAddress IPAddress;
  private int port;
  MulticastState multicastClient;

  public UpstreamInstance(String ip, int port, int multicast_listen_port){
    IPAddress = InetAddress.getByName(ip);
    socket = new DatagramSocket(port);
    this.port = port;
    multicastClient = new MulticastState(multicast_listen_port);
    multicastClient.start(); //start runnable
  }

  public void routeToInstance(String packet) {
    //route control packets to server instance
    data = packet.getBytes();
    DatagramPacket send_packet = new DatagramPacket(data, data.length, this.IPAddress, this.port);
    this.socket.send(send_packet);
    this.lifetime_route_total++;
  }

  public int getRoutingTotal() {
    return this.lifetime_route_total;
  }
}
