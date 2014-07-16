import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class GUI {
	static final String FRAME_LABEL = "Logistic Model";
	static final String INIT_LABEL = "Initial iterations:";
	static final String X_RANGE_LABEL = "X resolution:";
	static final String R_RANGE_LABEL = "R resolution:";
	static final String RESET_LABEL = "Reset";
	static final int INIT_MIN = 0;
	static final int INIT_MAX = 10000;
	static final int INIT_INIT = 1000;
	static final int X_RES_MIN = 1;
	static final int X_RES_MAX = 10000;
	static final int X_RES_INIT = 1000;
	static final int R_RES_MIN = 1000;
	static final int R_RES_MAX = 10000;
	static final int R_RES_INIT = 3000;
	
	/*TODO: Remake this so the user will control those values.*/
	static final double X0 = 0.001;
	static final double MIN_X = 0.0;	// At least 0.0
	static final double MAX_X = 1.0;	// At most 1.0
	static final double MIN_R = 3.55;	// At least 0.0
	static final double MAX_R = 4.0;	// At most 4.0
	
	private int initValue = INIT_INIT;
	private int xResValue = X_RES_INIT;
	private int rResValue = R_RES_INIT;
	
	private JFrame mainFrame; // The main panel.
	private JPanel statusBar;
	private JPanel controlBar;
	private Diagram diagram; // Bifurcation Diagram. REPLACE
	private JLabel statusLabel; // Should read the coordinates of the mouse.
	private JLabel initLabel;
	private JLabel xRangeLabel;
	private JLabel rRangeLabel;
	private JButton resetButton;
	private JSlider initSlider;
	private JSlider xResSlider;
	private JSlider rResSlider;  
	
	public static void main(String[] args){
		new GUI();
	}
	
	public GUI() {
		mainFrame = new JFrame(FRAME_LABEL);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setSize(640,480); // TODO: move this to the constants sections.
		
		statusBar = new JPanel();
		controlBar = new JPanel();
		diagram = new Diagram(); // CAREFUL!!!
		
		statusLabel = new JLabel("",JLabel.CENTER);
		initLabel = new JLabel(INIT_LABEL,JLabel.RIGHT);
		xRangeLabel = new JLabel(X_RANGE_LABEL, JLabel.RIGHT);
		rRangeLabel = new JLabel(R_RANGE_LABEL, JLabel.RIGHT);
		
		resetButton = new JButton(RESET_LABEL);
		
		initSlider = new JSlider(JSlider.HORIZONTAL, INIT_MIN, INIT_MAX, INIT_INIT);
		initSlider.setMajorTickSpacing(5000);
		initSlider.setMinorTickSpacing(1000);
		initSlider.setPaintTicks(true);
		initSlider.setPaintLabels(true);
		xResSlider = new JSlider(JSlider.HORIZONTAL, X_RES_MIN, X_RES_MAX, X_RES_INIT);
		xResSlider.setMajorTickSpacing(5000);
		xResSlider.setMinorTickSpacing(1000);
		xResSlider.setPaintTicks(true);
		xResSlider.setPaintLabels(true);
		rResSlider = new JSlider(JSlider.HORIZONTAL, R_RES_MIN, R_RES_MAX, R_RES_INIT);
		rResSlider.setMajorTickSpacing(5000);
		rResSlider.setMinorTickSpacing(1000);
		rResSlider.setPaintTicks(true);
		rResSlider.setPaintLabels(true);
		
		mainFrame.add(statusBar,BorderLayout.SOUTH);
		mainFrame.add(controlBar, BorderLayout.NORTH);
		mainFrame.add(diagram, BorderLayout.CENTER);
		
		statusBar.add(statusLabel);
		
		controlBar.setLayout(new GridLayout(1,7));
		controlBar.add(initLabel);
		controlBar.add(initSlider);
		controlBar.add(xRangeLabel);
		controlBar.add(xResSlider);
		controlBar.add(rRangeLabel);
		controlBar.add(rResSlider);
		controlBar.add(resetButton);
		
		initSlider.addChangeListener(new SliderListener());
		initSlider.setName(INIT_LABEL);
		xResSlider.addChangeListener(new SliderListener());
		xResSlider.setName(X_RANGE_LABEL);
		rResSlider.addChangeListener(new SliderListener());
		rResSlider.setName(R_RANGE_LABEL);
		resetButton.addActionListener(new ButtonListener());
		resetButton.setActionCommand("reset");
		
		mainFrame.setVisible(true);
	}
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand(); 
			if( command.equals( "reset" ))  {
	            initValue = initSlider.getValue();
	            xResValue = xResSlider.getValue();
	            rResValue = rResSlider.getValue();
	            statusLabel.setText("reset.");
	         }
		}
	}
	
	private class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			if (source.getValueIsAdjusting()) {
				int fps = (int)source.getValue();
				statusLabel.setText(""+source.getName()+" "+fps);
			}
			else {
				/*//DESTROY!!!
				int fps = (int)source.getValue();
				if (source ==  initSlider) {
					initValue = fps; 
				}
				else if (source == xResSlider) {
					xResValue = fps;
				}
				else if (source == rResSlider) {
					rResValue = fps;
				}
				*/
				statusLabel.setText("");
			}
		}
	}
	
	private class Diagram extends Canvas {
		private static final long serialVersionUID = -6097526300504399996L;

		public void paint(Graphics g) {
			Rectangle b = g.getClipBounds();
			int maxX = (int)b.getMaxX();
			int maxY = (int)b.getMaxY();
			LogisticMap lMap = new LogisticMap(0,0);
			
			for (int i=1; i<=rResValue; i++) {
				double r = MIN_R + (MAX_R-MIN_R)*i/rResValue;
				int rCoord = maxX*i/rResValue;
				lMap.setR(r);
				lMap.setX(X0);
				//lMap.setX(Math.random());
				lMap.iterate(initValue);
				for (int j=1; j<=xResValue; j++) {
					double x = lMap.next();
					int xCoord = (int)(maxY*(x-MAX_X)/(MIN_X-MAX_X));
					g.drawLine(rCoord,xCoord,rCoord,xCoord);	
				}
			}
		}
	}
}
