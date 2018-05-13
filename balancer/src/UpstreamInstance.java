import java.util.concurrent;

public class UpstreamInstance implements Runnable{

  private int lifetime_route_total;
  private DatagramSocket socket;
  private InetAddress IPAddress;
  private String host;
  private int port;
  private Logger log;
  private BlockingQueue<String> exterior_pipeline;
  private BroadcastState broadcast;
  private Verifier verify;

  public UpstreamInstance(String ip, int port, int broadcast_listen_port, int broadcast_port, Verifier verify) {
    IPAddress = InetAddress.getByName(ip);
    socket = new DatagramSocket(port);
    this.port = port;
    this.host = ip;
    Logger.write("starting upstream instance: " + ip + ":" + port);
    this.exterior_pipeline = exterior_pipeline;
    this.verify = verify;
    this.exterior_pipeline = new LinkedBlockingQueue<String>();
    this.broadcast = new BroadcastState(broadcast_listen_port, broadcast_port);
    new Thread(this.broadcast).start();
  }

  public void run() {
    while (true) {
      String packet;
      while ((packet = this.exterior_pipeline.poll()) != null) {
         //received external control packet
         String contents = packet.trim();
         String[] subcomponents = contents.split(" ");
         if (this.verify.checkControlMessage(subcomponents)) {
           //check if packet is control method
           executeControlProtocol(subcomponents);
         } else if (this.verify.verifyContents(subcomponents)) {
           //verify control message, route
           routeToInstance(contents);
         } else {
           Logger.write("dropped packet in verification stage: " + contents);
         }
      }

    }
  }

  private void routeToInstance(String packet) {
    byte[] data = packet.getBytes();
    //route control packets to server instance
    DatagramPacket send_packet = new DatagramPacket(data, data.length, this.IPAddress, this.port);
    this.socket.send(send_packet);
  }

  private void executeControlProtocol(String[] packet_components) {
    if (packet_components[0] == "JOIN") {
      try {
        this.broadcast.joinGroup(packet_components[1], Integer.parseInt(packet_components[2]));
        //TODO: send control message to sender with server id (instance id)
      } catch (Exception e) {
        Logger.write("ERROR parsing host join message: " + packet_components[1]);
      }
    } else {
      //TODO
      //this.broadcast.leaveGroupGroup();
    }
  }

  public void routePacket(String message) {
    this.exterior_pipeline.put(message);
  }
}
