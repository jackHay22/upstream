import java.util.ArrayList;

public class MapEntry {
  private ArrayList<Integer> fields;
  public MapEntry(String raw_field_data) {
    String[] raw_fields = raw_field_data.split(",");;
    fields = new ArrayList<Integer>();
    for (int i = 0; i < raw_fields.length; i++) {
      fields.add(Integer.parseInt(raw_fields[i]));
    }
  }
  public int getField(int index) { return fields.get(index); }
  public int getImageIndex() { return fields.get(0); }
}
