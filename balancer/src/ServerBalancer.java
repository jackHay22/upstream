import java.io.*;
import java.net.*;

public class ServerBalancer {
  public static void main(String[] args) {

    if (args.length < 1) {
      System.out.println("Error: specify configuration file");
      return;
    }

    BufferedReader config_reader = new BufferedReader(new FileReader(args[0]));

    //determine verifiable symbols from file
    String line;
    line = config_reader.readLine();
    String[] facing_messages = line.split(" ")
    line = config_reader.readLine();
    String[] action_messages = line.split(" ")
    BalancedVerifier balancer = new BalancedVerifier(facing_messages, action_messages);

    //determine port to listen on from file
    line = config_reader.readLine();
    int listener_port = Integer.parseInt(line);

    //load IP, PORT pairs from file
    line = config_reader.readLine();
    while (line != null) {
        String[] components = line.split(" ");
        System.out.println("Loading instance at: " + components[0]);
        balancer.addInstance(components[0], Integer.parseInt(components[1]));
        line = config_reader.readLine();
    }

    //networking
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
