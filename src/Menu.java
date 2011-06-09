import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Menu extends JPanel {
	private static final long serialVersionUID = 1L;
	
	Data data;
	
	JComboBox filename;
	JSlider testSize;
	JSlider tresh;
	JButton play;
	
	JLabel predLabel;
	double avgConn;
	double shrinkSpeed;
	double mass;
	boolean showPre = false;
	double border;

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
		
		addPane(play(), 0, c);
		addPane(reset(), 1, c);
		addPane(export(), 2, c);
		
		addPane(new JPanel(), 3, c);
		
		addPane(colorPanel(), 4, c);
		
		addPane(new JPanel(), 5, c);
		
		addPane(forcePredict(), 6, c);
		addPane(distPredict(), 7, c);
		addPane(showPredict(), 8, c);
		
		addPane(new JPanel(), 9, c);
		
		addPane(border(), 10, c);
		addPane(threshold(), 11, c);
		addPane(shrink(), 12, c);
		addPane(mass(), 13, c);
		addPane(attrScore(), 14, c);
	}
	
	private void addPane(JComponent p, int i, GridBagConstraints c) {
		Border in = BorderFactory.createLineBorder(this.getBackground(), 5);
		p.setBorder(BorderFactory.createCompoundBorder(in, p.getBorder()));
		c.gridy = i;
		
		if (i == 3 || i == 5 || i == 9) c.weighty = 1;
		else  c.weighty = 0;
			
		this.add(p, c);
	}
	
	private JComponent export() {
		JButton export = new JButton("Export");

		export.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {
				data.export();
			}

		});
		
		return export;
	}
	
	private JComponent play() {
		play = new JButton();
		if (Main.play) play.setText("Pause");
		else play.setText("Play");
		
		play.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {
				Main.playPause();
				if (Main.play) play.setText("Pause");
				else play.setText("Play");
			}

		});
		
		return play;
	}
	
	private JButton attrScore() {
		JButton open = new JButton("Set attributes");
		
		open.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {
				openAttrScore();
			}

		});
		
		return open;
	}

	
	private void openAttrScore() {
		JFrame frame = new JFrame("Set attributes");
		frame.setPreferredSize(new Dimension(300, 700));
		
		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		Container cont = frame.getContentPane();
		JScrollPane scroll = new JScrollPane(pane);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		pane.setLayout(gridbag);
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
				
		c.gridy = 0;
		JButton reset = new JButton("Reset");
		reset.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent e) {
				Container cont = ((JButton)e.getSource()).getParent();
				for (Component c : cont.getComponents()) {
					if (c instanceof JSlider) { 
						AttrMeta.scores[Integer.parseInt(c.getName())] = 1;
						((JSlider)c).setValue(100);
					}
				}
				resetCon();
			}

		});
		pane.add(reset, c);
		
		c.gridy = 1;
		JButton infg = new JButton("Inf gain");
		infg.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent e) {
				AttrMeta.parseStats(data.entity, data.clas.length > 1);
				Container cont = ((JButton)e.getSource()).getParent();
				for (Component c : cont.getComponents()) {
					if (c instanceof JSlider) { 
						((JSlider) c).setValue((int)(AttrMeta.scores[Integer.parseInt(c.getName())] * 100));
					}
				}
				resetCon();
			}

		});
		pane.add(infg, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1;
		int i;
		for (i = 0; i < AttrMeta.size; i++) {
			c.gridy = i * 2 + 2;
			pane.add(new JLabel(AttrMeta.name[i] + ":"), c);
			
			c.gridy = i * 2 + 3;
			JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 200, (int)(AttrMeta.scores[i] * 100));
			slider.setName(Integer.toString(i));
			slider.setMajorTickSpacing(50);
			slider.setMinorTickSpacing(10);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			pane.add(slider, c);
		}
		
		c.gridy = i * 2 + 2;
		JButton submit = new JButton("Recalculate");
		
		submit.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent e) {
				Container cont = ((JButton)e.getSource()).getParent();
				for (Component c : cont.getComponents()) {
					if (c instanceof JSlider) { 
						double val = (double)((JSlider)c).getValue() / 100;
						AttrMeta.scores[Integer.parseInt(c.getName())] = val;
					}
				}
				resetCon();
			}

		});
		pane.add(submit, c);
		
		cont.add(scroll);
		frame.setVisible(true);
		frame.pack();
	}
	
	private void resetCon() {
		data.calculateConnections();
		avgConn = data.avgConn;
		int avg = (int)(avgConn * 10);
		int min = (int)(avgConn / 4 * 10);
		int max = (int)(avgConn * 4 * 10);
		int space = (max - min) / 3;
		tresh.setMajorTickSpacing(space);
		tresh.setMinorTickSpacing(space / 10);
		tresh.setMinimum(min);
		tresh.setMaximum(max);
		tresh.setValue(avg);
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
	    filename.setSelectedItem(Main.filename);
	    
		p.add(filename, c);
		c.gridy = 1;
		p.add(new JLabel("Sample size in %:"), c);
		
		testSize = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(Main.testSize * 100));
		testSize.setMajorTickSpacing(20);
		testSize.setMinorTickSpacing(2);
		testSize.setPaintTicks(true);
		testSize.setPaintLabels(true);
		c.gridy = 2;
		p.add(testSize, c);
		
		JButton reset = new JButton("Load");
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
	
	private JPanel border() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set border:"), c);
		
		avgConn = data.avgConn;
		
		int avg = (int)(border);
		int min = (int)(border / 10);
		int max = (int)(border * 2);
		
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
					double d = border;
					border = (double)source.getValue();
					d = d - border;
					for (Entity en : data.entity) {
						en.x -= d;
						en.y -= d;
					}
				}
			}
		});
		
		c.gridy = 1;
		p.add(slider, c);
				
		return p;
	}
	
	private JPanel threshold() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set threshold:"), c);
		
		avgConn = data.avgConn;
		
		int avg = (int)(avgConn * 10);
		int min = (int)(avgConn / 4 * 10);
		int max = (int)(avgConn * 6 * 10);
		
		tresh = new JSlider(JSlider.HORIZONTAL, min, max, avg);
		int space = (max - min) / 3;
		tresh.setMajorTickSpacing(space);
		tresh.setMinorTickSpacing(space / 10);
		tresh.setPaintTicks(true);
		tresh.setPaintLabels(true);
		tresh.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					avgConn = (double)source.getValue() / 10;
				}
			}
		});
		
		c.gridy = 1;
		p.add(tresh, c);
				
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
		int min = (int)Math.log(mass / 2000);
		int max = (int)Math.log(mass * 2000);
				
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, avg);
		int space = (max - min) / 3;
		slider.setMajorTickSpacing(space);
		slider.setMinorTickSpacing(space / 20);
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
	
	private JCheckBox showPredict() {
		JCheckBox spr = new JCheckBox("Show predicted color");
		spr.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					showPre = true;
				} else {
					showPre = false;
				}
				//System.out.println(showPre);
			}
		});
		
		return spr;
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
