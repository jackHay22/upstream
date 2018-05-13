import java.io.*;
import java.net.*;
import org.json.*;
import java.util.HashMap;

public class ServerBalancer {
  public static void main(String[] args) {

    if (args.length < 1) {
      log.write("ERROR: specify configuration file as arg1");
      return;
    }

    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader(args[0]));
    JSONObject instance_config =  (JSONObject) obj;

    JSONObject verification_config = instance_config.get("verification");
    String[] facing_messages = verification_config.get("facing").split(" ");
    String[] action_messages = verification_config.get("action").split(" ");

    Verifier standard_verifier = new Verifier(facing_messages, action_messages);

    HashMap<Integer, UpstreamInstance> routable_instances = new HashMap<Integer, UpstreamInstance>();

    JSONArray hosts = instance_config.getJSONArray("hosts");
    String host_addr;
    String host_port;
    for (int i = 0; i < hosts.length(); i++) {
        host_addr = hosts.getJSONObject(i).get("address");
        host_port = hosts.getJSONObject(i).get("port");
        multicast_port = hosts.getJSONObject(i).get("balancer-multicast-listen");
        multicast_dist = hosts.getJSONObject(i).get("balancer-multicast-distribute");
        routable_instances.put(i, new UpstreamInstance(host_addr,
                                                       Integer.parseInt(host_port),
                                                       Integer.parseInt(multicast_port),
                                                       Integer.parseInt(multicast_dist),
                                                       standard_verifier));
    }

    //networking

    //TODO
    String listener_port = instance_config.getJSONObject("port");
    DatagramSocket balancer_connection = new DatagramSocket(Integer.parseInt(listener_port));

    Logger.write("starting balancing service...");
    System.out.println("--------------------------------");
    Logger.write("using verification tags: " + facing_messages + " and " + action_messages);
    Logger.write("starting network listener on port: " + listener_port);
    System.out.println("--------------------------------");

    while(true) {
      //listen on external port
        DatagramPacket packet = socket.receive();

    }
  }
}
