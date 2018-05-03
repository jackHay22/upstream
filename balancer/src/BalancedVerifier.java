public class BalancedVerifier {

  private ArrayList<UpstreamInstance> game_instances;
  private ArrayList<String> allowed_facing_messages;
  private ArrayList<String> allowed_action_messages;
  int current_instance;
  int total_instances;

  public BalancedVerifier(String[] facing_messages, String[] action_messages) {
    this.game_instances = new ArrayList<UpstreamInstance>();
    this.current_instance = 0;

    allowed_facing_messages = new ArrayList<String>();
    for (int i=0; i<facing_messages.length; i++) {
      allowed_facing_messages.add(facing_messages[i]);
    }

    allowed_action_messages = new ArrayList<String>();
    for (int i=0; i<action_messages.length; i++) {
      allowed_action_messages.add(action_messages[i]);
    }
  }

  public void addInstance(String ip, int port, int multicast_listener) {
    this.game_instances.add(new UpstreamInstance(ip, port, multicast_listener));
    this.total_instances++;
  }

  private boolean verifyElementPair(String element, String field) {
    if (element == ":update-facing") {
      //TODO verify configured
    } elif (element == ":update-action") {
      //TODO verify configured
    } elif (sub_components[1] == ":user-auth") {
      //TODO verify authorized
    } elif (sub_components[1] == ":current-server") {
      //TODO verify registered
    }
  }

  private boolean verifyContents(String control_contents) {
    //control message formatted in the following way:
    // {:update-facing :symbol :update-action :symbol :user-auth :symbol :current-server :symbol}
    String[] sub_components = control_contents.split(" ");
    //check map integrity
    if (sub_components.length != 10) {
      return false;
    }
    if (sub_components[0] != "{" || sub_components[sub_components.length-1] != "}") {
      return false;
    }
    return (verifyElementPair(sub_components[1], sub_components[2]) &&
            verifyElementPair(sub_components[3], sub_components[4]) &&
            verifyElementPair(sub_components[5], sub_components[2]) &&
            verifyElementPair(sub_components[7], sub_components[8]));
  }

  public void routePacket(String payload) {
    //TODO: verify formatting
    String output payload.trim();
    if (verifyContents(output)) {
      //TODO: route to current instance (hash map?)
      game_instances.get(current_instance).routeToInstance(output);
      current_instance = (current_instance + 1) % total_instances;
    } else {
      System.out.println("DEBUG: packet dropped after verification attempt: " + payload);
    }
  }
}
