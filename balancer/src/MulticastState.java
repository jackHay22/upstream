public class MulticastState {

  int port_listener;
  MulticastSocket broadcast_server;
  InetAddress group;

  public MulticastState(int port_listener, int broadcast_port) implements Runnable {
    this.port_listener = port_listener;
    this.socket_listen = new DatagramSocket(this.port_listener);
    this.broadcast_port = broadcast_port;
    broadcast_server = new MulticastSocket(this.broadcast_port);
  }

  public void run() {
    while (1) {
      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      socket_listen.receive(packet);
      String server_state = new String(packet.getData()).trim();
      sendMulticastPacket(server_state);
    }
  }

  private void sendMulticastPacket(String packet) {
    DatagramPacket state = new DatagramPacket(packet.getBytes(), packet.length(), this.group, this.broadcast_port);
    broadcast_server.send(state);
  }

  public void joinBroadcastGroup(String group_addr) {
    this.group = InetAddress.getByName(group_addr);
    broadcast_server.joinGroup(this.group);
  }
  public void leaveBroadcastGroup() {
    broadcast_server.leaveGroup(this.group);
  }
}
