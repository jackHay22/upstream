import javax.swing.JFrame;

public class MapEditor {
	public static void main(String[] args) {
    if (args.length > 1){
        JFrame editor = new JFrame("Upstream Map Editor");
        editor.setContentPane(new DisplayPanel(args[0], args[1]));
    		editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		editor.setResizable(false);
    		editor.pack();
    		editor.setVisible(true);
    } else {
      System.out.println("Error: please provide 2 map filenames.");
    }
	}
}
