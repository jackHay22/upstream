import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tile {
	private BufferedImage image;
	private int offset_x;
	private int offset_y;
	public Tile(BufferedImage image, int draw_offset_x, int draw_offset_y) {
			this.image = image;
			this.offset_x = draw_offset_x;
			this.offset_y = draw_offset_y;
	}
	public BufferedImage getImage() { return image; }
	public int getDrawOffsetX() {return this.offset_x;}
	public int getDrawOffsetY() {return this.offset_y;}
}
