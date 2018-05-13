import java.util.ArrayList;

public class Verifier {

  private ArrayList<String> allowed_facing_messages;
  private ArrayList<String> allowed_action_messages;

  public Verifier(String[] facing_messages, String[] action_messages) {

    allowed_facing_messages = new ArrayList<String>();
    for (int i=0; i<facing_messages.length; i++) {
      allowed_facing_messages.add(facing_messages[i]);
    }

    allowed_action_messages = new ArrayList<String>();
    for (int i=0; i<action_messages.length; i++) {
      allowed_action_messages.add(action_messages[i]);
    }
  }
  private boolean verifyElementPair(String element, String field) {
    if (element == ":update-facing") {
      //TODO verify configured
      return true;
    } else if (element == ":update-action") {
      //TODO verify configured
      return true;
    } else if (element == ":user-auth") {
      //TODO verify authorized
      return true;
    } else if (element == ":port") {
      return true;
    } else {
      return false;
    }
  }

  public boolean verifyContents(String[] sub_components) {
    //control message formatted in the following way:
    // {:update-facing :symbol :update-action :symbol :user-auth :symbol :current-server :symbol}
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
            verifyElementPair(sub_components[7], sub_components[8])); //TODO (remove?)
  }

  public boolean checkControlMessage(String[] sub_components) {
    return (sub_components[0] == "JOIN" || sub_components[0] == "LEAVE");
  }
}
