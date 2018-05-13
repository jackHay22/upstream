public class Logger {
  private static String prefix = "UpstreamBalancer => ";
  public static void write(String message) {
    System.out.println(this.prefix + message);
  }
  public static void generateLog(String message) {
    System.out.println("agent=upstreambalancer, type=log, message=" + message);
  }
  public static void generateError(String error) {
    System.out.println("agent=upstreambalancer, type=error, message=" + message);
  }
}
