import java.io.PrintWriter;

public class MapWriter {
  public MapWriter() {

  }
  public void writeToFile(String filename, MapEntry[][] mapstore) {
    // String path = System.getProperty("user.home") + File.separator + ".upstream-dev";
    // path += File.separator + filename
    // File save_directory = new File(path);

    // if (save_directory.exists()) {
    //   System.out.println("Writing map file to: " + save_directory);
    // } else if (save_directory.mkdirs()) {
    //   System.out.println("Writing map file to new directory: " + save_directory);
    // } else {
    //   System.out.println("Error: could not create " + save_directory);
    //   return;
    // }
    try {
      System.out.println("UpstreamEdit => writing to " + filename);
      PrintWriter map_file_writer = new PrintWriter(filename, "UTF-8");
      map_file_writer.println("test");
      //TODO
      map_file_writer.close();
    } catch (Exception e) {
      System.out.println("Failed to write map to file");
    }
  }
}
