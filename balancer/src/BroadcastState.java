import java.util.ArrayList;

public class BroadcastState implements Runnable{

  private final Object lock = new Object();
  private DatagramSocket sender;
  private ArrayList<Subscriber> subscribers;

  public BroadcastState(int port_listener, int broadcast_port) {
    this.socket_listen = new DatagramSocket(port_listener);
    this.broadcast_port = broadcast_port;

    this.sender = new DatagramSocket();
    Logger.write("upstream state broadcast listening on port: " + port_listener);
  }

  public void run() {
    Logger.write("starting broadcast on port: " + this.broadcast_port);
    while (1) {
      byte[] state_buffer = new byte[1024];
      DatagramPacket packet = new DatagramPacket(state_buffer, state_buffer.length);
      socket_listen.receive(packet);
      broadcastToCurrentGroup(state_buffer);
    }
  }

  private void broadcastToCurrentGroup(byte[] packet) {
    DatagramPacket out;
    synchronized(lock) {
      for (int i=0;i<this.subscribers.length();i++){
        out = new DatagramPacket(packet, packet.length,
                                 subscribers.get(i).getAddr(),
                                 this.broadcast_port);
        this.sender.send(out);
      }
    }
  }

  public void joinGroup(String host, int port) {
    synchronized (lock) {
      Logger.write("host joining group: " + host);
      this.subscribers.add(new Subscriber(InetAddress.getByName(addr)));
    }
  }

  public void leaveGroup(String addr) {
    Logger.write("host leaving group: " + addr);
    //TODO
  }
}
