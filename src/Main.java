import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static String filename = "dermatology.tab";
	public static double testSize = 0.5;
	
	public static Menu menu;
	public static Board board;
	
	public static Main current;
	
	public Main() {
		super("Uni Classification");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1400, 1000);
        
        menu = new Menu();	
        board = new Board(menu);
        
        setGrid();
        
        this.setVisible(true);
	}
	
	public static void reset(String fn, double ts) {
		filename = fn;
		testSize = ts;
		board.init();
		
		current.validate();
	}
	
	public void setGrid() {
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		c.gridx = 0;
		c.weighty = 1;
		pane.add(menu, c);
			
		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 1;
		pane.add(board, c);
	}
	
	public static void main(String[] args) {
		current = new Main();
	}
}
