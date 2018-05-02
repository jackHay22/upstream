public class BalancedVerifier {
  private ArrayList<UpstreamInstance> game_instances;
  private ArrayList<String> allowed_facing_messages;
  private ArrayList<String> allowed_action_messages;
  int current_instance;
  int total_instances;

  public BalancedVerifier(String[] facing_messages, String[] action_messages) {
    this.game_instances = new ArrayList<UpstreamInstance>();
    this.current_instance = 0;

    allowed_facing_messages = new private ArrayList<String>();
    for (int i=0; i<facing_messages.length; i++) {
      allowed_facing_messages.add(facing_messages[i]);
    }

    allowed_action_messages = new private ArrayList<String>();
    for (int i=0; i<action_messages.length; i++) {
      allowed_action_messages.add(action_messages[i]);
    }
  }
  public void addInstance(String ip, int port) {
    this.game_instances.add(new UpstreamInstance(ip, port));
    this.total_instances++;
  }

  private boolean verifyContents(String control_contents) {
    String[] sub_components = control_contents.split(" ");
    //check map integrity
    if (sub_components.length != 6) {
      return false;
    }
    if (sub_components[0] != "{" || sub_components[sub_components.length-1] != "}") {
      return false;
    }

    if (sub_components[1] == ":update-facing") {
      //TODO
      if (sub_components[3] == ":update-action") {
        //TODO
      }
      return false;
    }
    elif (sub_components[1] == ":update-action") {
      //TODO
      if (sub_components[3] == ":update-facing") {
        //TODO
        return false;
      }
    }
    elif (sub_components[1] == ":user-auth") {
//TODO fix
    }
    else {
      return false;
    }

    //control message formatted in the following way: {:update-facing :symbol :update-action :symbol}
    return true;
  }
  public void routePacket(String payload) {
    //TODO: verify formatting
    String output payload.trim();
    if (verifyContents(output)) {
      game_instances.get(current_instance).routeToInstance(output);
      current_instance = (current_instance + 1) % total_instances;
      //TODO as a game server the server number should be specified in the control packet
      //TODO remove instance ID too
    } else {
      System.out.println("Error: dropped packet after verification attempt: " + payload);
    }
  }
}
