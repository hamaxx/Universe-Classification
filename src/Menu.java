import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Menu extends JPanel{
	private static final long serialVersionUID = 1L;
	
	Data data;
	
	JComboBox filename;
	JSlider testSize;
	
	JLabel predLabel;
	double avgConn;
	double shrinkSpeed;
	double mass;

	public Menu() {
		Border in = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		Border out = BorderFactory.createRaisedBevelBorder();
		this.setBorder(new CompoundBorder(out, in));
	}
	
	public void setMenu(Data d) {
		data = d;
		this.removeAll();
		setPane();
	}
	
	private void setPane() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		addPane(reset(), 0, c);
		
		addPane(new JPanel(), 1, c);
		
		addPane(colorPanel(), 2, c);
		
		addPane(new JPanel(), 3, c);
		
		addPane(forcePredict(), 4, c);
		addPane(distPredict(), 5, c);
		
		addPane(new JPanel(), 6, c);
		
		addPane(threshold(), 7, c);
		addPane(shrink(), 8, c);
		addPane(mass(), 9, c);
	}
	
	private void addPane(JPanel p, int i, GridBagConstraints c) {
		Border in = BorderFactory.createEmptyBorder(5, 0, 5, 0);
		p.setBorder(in);
		c.gridy = i;
		
		if (i == 1 || i == 3 || i == 6) c.weighty = 1;
		else  c.weighty = 0;
			
		this.add(p, c);
	}
	
	private JPanel reset() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		filename = new JComboBox();
		//filename.setMaximumSize(new Dimension(100, 30));
	    File folder = new File("./datasets");
	    File[] listOfFiles = folder.listFiles();
	    for (File f : listOfFiles) {
	    	if (f.isFile() && f.getName().matches(".*\\.tab$")) {
	    		filename.addItem(f.getName());
	    	}
	    }
	    
		p.add(filename, c);
		c.gridy = 1;
		p.add(new JLabel("Test size in %:"), c);
		
		testSize = new JSlider(JSlider.HORIZONTAL, 10, 90, (int)(Main.testSize * 100));
		testSize.setMajorTickSpacing(20);
		testSize.setMinorTickSpacing(2);
		testSize.setPaintTicks(true);
		testSize.setPaintLabels(true);
		c.gridy = 2;
		p.add(testSize, c);
		
		JButton reset = new JButton("Reset");
		reset.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {
				Main.reset((String)filename.getSelectedItem(), (double)testSize.getValue() / 100);
			}

		});
		c.gridy = 3;
		p.add(reset, c);
		
		return p;
	}
	
	private JPanel threshold() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set threshold:"), c);
		
		avgConn = data.avgConn;
		
		int avg = (int)(avgConn * 10);
		int min = (int)(avgConn / 2 * 10);
		int max = (int)(avgConn * 2 * 10);
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, avg);
		int space = (max - min) / 3;
		slider.setMajorTickSpacing(space);
		slider.setMinorTickSpacing(space / 10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					avgConn = (double)source.getValue() / 10;
				}
			}
		});
		
		c.gridy = 1;
		p.add(slider, c);
				
		return p;
	}
	
	private JPanel shrink() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set shrink speed:"), c);

		int min = 10;
		int avg = 30;
		int max = 50;
		
		shrinkSpeed = 0.999;
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, avg);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					shrinkSpeed = 1 - Math.pow(10, -1 * (double)(60 - source.getValue()) / 10);
				}
			}
		});
		
		c.gridy = 1;
		p.add(slider, c);
				
		return p;
	}
	
	private JPanel mass() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set inertia:"), c);

		int avg = (int)Math.log(mass);
		int min = (int)Math.log(mass / 1000);
		int max = (int)Math.log(mass * 1000);
				
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, avg);
		int space = (max - min) / 3;
		slider.setMajorTickSpacing(space);
		slider.setMinorTickSpacing(space / 10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					mass = Math.pow(Math.E, (double)source.getValue());
				}
			}
		});
		
		c.gridy = 1;
		p.add(slider, c);
				
		return p;
	}
	
	private JPanel forcePredict() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		p.add(new JLabel("Prediction with force:"));
		p.add(new JLabel("  CA: " + (new Predict(data).predictTest())));
		
		return p;
	}
	
	private JPanel distPredict() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		p.add(new JLabel("Prediction with distance:"));
		predLabel = new JLabel("  CA: ??%");
		p.add(predLabel);
		
		return p;
	}
	
	public void newDistPredict() {
		predLabel.setText("  CA: " + new Predict(data).predict() + "%");
	}
	
	private JPanel colorPanel() {		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(new JLabel("Colors:"));
		
		for (Clas c : data.clas) {
			JLabel label = new JLabel("  Class: " + c.name);
			label.setForeground(c.color());
			p.add(label);
		}
		return p;
	}
}
