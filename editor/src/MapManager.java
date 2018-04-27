import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.event.*;

public class MapManager {

  private int displayAcross;
	private int displayDown;

  private int grid_dim;

  private MapEntry[][] map1;
  private MapEntry[][] map2;

  private BufferedImage tileSet;
	private ArrayList<Tile> tiles_one;
  private ArrayList<Tile> tiles_two;
  private BufferedReader mapFile;

	private int mapTilesAcross;
	private int mapTilesDown;

	private int startXDisplay;
	private int startYDisplay;

  private int width;
  private int height;
  private int x = 0;
  private int y = 0;
  private int xmin;
  private int ymin;
  private int xmax;
  private int ymax;

  public MapManager(String layer_one, String layer_two, int grid_dimension) {
    this.grid_dim = grid_dimension;
    this.displayAcross = (DisplayPanel.WIDTH / grid_dimension) + 2;
    this.displayDown = (DisplayPanel.HEIGHT / grid_dimension) + 2;
    tiles_one = new ArrayList<Tile>();
    tiles_two = new ArrayList<Tile>();
    loadMapOne(layer_one, 2000, 2000);
    loadMapTwo(layer_two, 2000, 2000);
  }

  private MapEntry[][] loadMap(String resource, int rows, int cols) {
    String[] rawstrArray;
		String input; //line read from file
    mapTilesAcross = cols;
    mapTilesDown = rows;
    MapEntry[][] temp = new MapEntry[mapTilesAcross][mapTilesDown];

		try {
			//try reading from map file
			InputStream in = getClass().getResourceAsStream(resource);
			mapFile = new BufferedReader(new InputStreamReader(in));

			width = mapTilesAcross * this.grid_dim;
			height = mapTilesDown * this.grid_dim;

			//limits on position
			xmin = DisplayPanel.WIDTH - width; //(negative if map width is greater than 320)
			xmax = 0;	//max x set
			ymin = DisplayPanel.HEIGHT - height; //(negative if map height is greater than 240)
			ymax = 0;	//max y set

			//build map according to size
      MapEntry current;

			for (int y = 0; y < mapTilesDown; y++) {
				//for each line of mapfile, read line, split by commas, then use ints for line to find a tile in set
				input = mapFile.readLine();
				rawstrArray = input.split(" "); //TODO: split by spaces and use commas to split each field

				for(int x = 0; x < mapTilesAcross; x++) {
					//parse current value and find image in tileset to load into map 2d array
            current = new MapEntry(rawstrArray[x]);
				    temp[x][y] = current;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
      System.exit(1);
		}
    return temp;
  }

  public void loadMapOne(String resource, int r, int c) {
    this.map1 = loadMap(resource, r, c);
  }
  public void loadMapTwo(String resource, int r, int c) {
    this.map2 = loadMap(resource, r, c);
  }

  private ArrayList<Tile> loadTiles(String resource, int local_tile_dimx, int local_tile_dimy, int draw_offset_x, int draw_offset_y) {
    ArrayList<Tile> res = new ArrayList<Tile>();
    try {
      BufferedImage newtileSet = ImageIO.read(getClass().getResourceAsStream(resource));

			//calculate tiles in image (x and y)
			//load tiles into arrayList as subimages of tileset
			for (int row = 0; row < newtileSet.getHeight(); row += local_tile_dimy) {
				for (int col = 0; col < newtileSet.getWidth(); col += local_tile_dimx) {
						res.add(new Tile(newtileSet.getSubimage(col, row, local_tile_dimx, local_tile_dimy),
            draw_offset_x, draw_offset_y));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
      System.exit(1);
		}
    return res;
  }
  public void loadLayerOneTiles(String r, int dimx, int dimy, int offx, int offy) {
    tiles_one.addAll(loadTiles(r, dimx, dimy, offx, offy));
  }
  public void loadLayerTwoTiles(String r, int dimx, int dimy, int offx, int offy) {
    tiles_two.addAll(loadTiles(r, dimx, dimy, offx, offy));
  }
  public void draw(Graphics2D g) {
    for (int x = this.startXDisplay; x < this.startXDisplay + displayAcross; x++) {

			if(x >= mapTilesAcross) break;
			if(x < 0) break;

			for (int y = this.startYDisplay; y < this.startYDisplay + displayDown; y++) {
				if(y >= mapTilesDown) break;
				if(y < 0) break;
				int rc = map1[x][y].getImageIndex();
        Coordinates transform = new Coordinates(x * this.grid_dim, y * this.grid_dim);
        Tile current = tiles_one.get(rc); //out of bounds exception here
        g.drawImage(current.getImage(),
                    (int) this.x + transform.getIsometricX() - current.getDrawOffsetX(),
                    (int) this.y + transform.getIsometricX() - current.getDrawOffsetY(), null);
			}
		}
    for (int x = this.startXDisplay; x < this.startXDisplay + displayAcross; x++) {

			if(x >= mapTilesAcross) break;
			if(x < 0) break;

			for (int y = this.startYDisplay; y < this.startYDisplay + displayDown; y++) {
				if(y >= mapTilesDown) break;
				if(y < 0) break;
				int rc = map2[x][y].getImageIndex();
        Coordinates transform = new Coordinates(x * this.grid_dim, y * this.grid_dim);
        Tile current = tiles_two.get(rc);
				g.drawImage(current.getImage(),
                    (int) this.x + transform.getIsometricX() - current.getDrawOffsetX(),
                    (int) this.y + transform.getIsometricX() - current.getDrawOffsetY(), null);
			}
		}
  }
  public void update() {

  }
  public void setPosition(double x, double y) {

		//position change
		this.x += x - this.x;
		this.y += y - this.y;

		fixBounds();

		//get tile location for start of display
		this.startXDisplay = (int)-this.x / this.grid_dim;
		this.startYDisplay = (int)-this.y / this.grid_dim;

	}
  private void fixBounds() {
		if(x < xmin) x = xmin;
		if(y < ymin) y = ymin;
		if(x > xmax) x = xmax;
		if(y > ymax) y = ymax;
	}
  public void keyPressed(int k) {
    if (k == KeyEvent.VK_S) {
      System.out.println("Running save sequence...");
    }
  }
  public void keyReleased(int k) {

  }
  public void mouseClick() {

  }

}
