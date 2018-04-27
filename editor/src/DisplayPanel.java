import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DisplayPanel extends JPanel implements Runnable, KeyListener{

	public static final int WIDTH = 1440;
	public static final int HEIGHT = 700;

	private Thread thread;
	private boolean running;
	private int FPS = 30;
	private long targetTime = 1000 / FPS;

	private BufferedImage image;
	private Graphics2D g;

	private MapManager manager;
	private String map1;
	private String map2;

	public DisplayPanel(String map1, String map2) {
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		this.map1 = map1;
		this.map2 = map2;
	}
	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
		manager = new MapManager(this.map1, this.map2, 32);
		manager.loadLayerOneTiles("tiles/test_sheet.png", 64, 32, 32, 0);
		manager.loadLayerTwoTiles("tiles/tree_set.png", 130, 325, 60, 300);
		//manager.loadLayerOneTiles("tiles/test_sheet.png", 64, 32, 32, 0);

//TODO
	}
	public void run() {
		init();
		long start;
		long elapsed;
		long wait;
		while (running) {
			start = System.nanoTime();
			update();
			draw();
			drawToScreen();

			elapsed = System.nanoTime() - start;

			wait = targetTime - elapsed / 1000000;
			if(wait < 0) wait = 5;
			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void update() {
		manager.update();
	}
	private void draw() {
		manager.draw(g);
	}
	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		g2.dispose();

	}
	public void keyTyped(KeyEvent key) {

	}
	public void keyPressed(KeyEvent key) {
		manager.keyPressed(key.getKeyCode());
	}
	public void keyReleased(KeyEvent key) {
		manager.keyReleased(key.getKeyCode());
	}
}
