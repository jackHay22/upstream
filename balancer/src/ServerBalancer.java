import java.io.*;
import java.net.*;
import org.json.*;

public class ServerBalancer {
  public static void main(String[] args) {

    if (args.length < 1) {
      System.out.println("Error: specify configuration file");
      return;
    }
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(args[0]));
    JSONObject instance_config =  (JSONObject) obj;

    JSONObject verification_config = instance_config.get("verification");
    String[] facing_messages = verification_config.get("facing").split(" ");
    String[] facing_messages = verification_config.get("action").split(" ");

    BalancedVerifier balancer = new BalancedVerifier(facing_messages, action_messages);

    JSONArray hosts = instance_config.getJSONArray("hosts");
    String host_addr;
    String host_port;
    for (int i = 0; i < hosts.length(); i++) {
        host_addr = hosts.getJSONObject(i).get("address");
        host_port = hosts.getJSONObject(i).get("port");
        multicast_port = hosts.getJSONObject(i).get("multicast-port");
        balancer.addInstance(host_addr, Integer.parseInt(host_port), Integer.parseInt(multicast_port));
    }

    //networking
    String listener_port = instance_config.getJSONObject("port");
    DatagramSocket balancer_connection = new DatagramSocket(Integer.parseInt(listener_port));
    byte[] receive_input = new byte[1024];
    byte[] send_output = new byte[1024];

    while(true) {
        DatagramPacket receivePacket = new DatagramPacket(receive_input, receive_input.length);
        serverSocket.receive(receivePacket);
        String raw_control_map = new String(receivePacket.getData());
        balancer.routePacket(raw_control_map);
    }
  }
}
