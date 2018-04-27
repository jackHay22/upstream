public class Coordinates {

  private int x_cartesian;
  private int y_cartesian;
  private int x_isometric;
  private int y_isometric;

  public Coordinates(int x, int y) {
    this.x_cartesian = x;
    this.y_cartesian = y;
    this.x_isometric = x - y;
    this.y_isometric = (int) (x + y) / 2;
  }
  public void setIsometric(int iso_x, int iso_y) {
    this.x_isometric = iso_x;
    this.y_isometric = iso_y;
    this.x_cartesian = (int) iso_y + (iso_x / 2);
    this.y_cartesian = (int) iso_y - (iso_x / 2);
  }
  public void setCartesian(int cart_x, int cart_y) {
    this.x_cartesian = cart_x;
    this.y_cartesian = cart_y;
    this.x_isometric = cart_x - cart_y;
    this.y_isometric = (int) (cart_x + cart_y) / 2;
  }
  public int getIsometricX() {
    return this.x_isometric;
  }
  public int getIsometricY() {
    return this.y_isometric;
  }
  public int getCartesianX() {
    return this.x_cartesian;
  }
  public int getCartesianY() {
    return this.y_cartesian;
  }
}
