import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
		setPane();
	}
	
	private void setPane() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addPane(colorPanel());
		addPane(forcePredict());
		addPane(distPredict());
		
		addPane(new JPanel());
		
		addPane(threshold());
		addPane(shrink());
		addPane(mass());
	}
	
	private void addPane(JPanel p) {
		//Border out = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0));
		Border in = BorderFactory.createEmptyBorder(5, 0, 5, 0);
		p.setBorder(in);
		this.add(p);
	}
	
	private JPanel threshold() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		p.add(new JLabel("Set threshold:"));
		
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
		
		p.add(slider);
				
		return p;
	}
	
	private JPanel shrink() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		p.add(new JLabel("Set shrink speed:"));

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
		
		p.add(slider);
				
		return p;
	}
	
	private JPanel mass() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		p.add(new JLabel("Set inertia:"));

		int avg = (int)Math.log(mass);
		int min = (int)Math.log(mass / 1000);
		int max = (int)Math.log(mass * 10);
				
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
		
		p.add(slider);
				
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
		
		for (Clas c : data.clas) {
			JLabel label = new JLabel("Class: " + c.name);
			label.setForeground(c.color());
			p.add(label);
		}
		return p;
	}
}
