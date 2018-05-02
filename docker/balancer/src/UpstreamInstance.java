public class UpstreamInstance {
  private int lifetime_route_total;
  private DatagramSocket() socket;
  InetAddress IPAddress;
  private int port;
  public UpstreamInstance(String ip, int port) {
    IPAddress = InetAddress.getByName(ip);
    socket = new DatagramSocket();
    this.port = port;
  }
  public void routeToInstance(String packet) {
    data = packet.getBytes();
    DatagramPacket send_packet = new DatagramPacket(data, data.length, this.IPAddress, this.port);
    this.socket.send(send_packet);
    this.lifetime_route_total++;
  }
  public int getRoutingTotal() {
    return this.lifetime_route_total;
  }
}
