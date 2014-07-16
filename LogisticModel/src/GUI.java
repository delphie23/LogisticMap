import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class GUI {
	static final String FRAME_LABEL = "Logistic Model";
	static final String INIT_LABEL = "Initial iterations:";
	static final String R_RANGE_LABEL = "R resolution:";
	static final String X_RANGE_LABEL = "X resolution:";
	static final String RESET_LABEL = "Reset";
	static final int INIT_MIN = 0;
	static final int INIT_MAX = 10000;
	static final int INIT_INIT = 1000;
	static final int R_RES_MIN = 1000;
	static final int R_RES_MAX = 10000;
	static final int R_RES_INIT = 3000;
	static final int X_RES_MIN = 1;
	static final int X_RES_MAX = 10000;
	static final int X_RES_INIT = 1000;
	
	
	/*TODO: Remake this so the user will control those values.*/
	private double x0 = 0.001;
	private double minR = 3.55;	// At least 0.0
	private double maxR = 4.0;	// At most 4.0
	private double minX = 0.0;	// At least 0.0
	private double maxX = 1.0;	// At most 1.0
	
	private int initValue = INIT_INIT;
	private int rResValue = R_RES_INIT;
	private int xResValue = X_RES_INIT;
	
	private JFrame mainFrame; // The main panel.
	private JPanel statusBar;
	private JPanel controlBar;
	private Diagram diagram; // Bifurcation Diagram. REPLACE
	private JLabel statusLabel; // Should read the coordinates of the mouse.
	private JLabel initLabel;
	private JLabel rRangeLabel;
	private JLabel xRangeLabel;
	private JButton resetButton;
	private JSlider initSlider;
	private JSlider rResSlider;
	private JSlider xResSlider;
	  
	
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
		rRangeLabel = new JLabel(R_RANGE_LABEL, JLabel.RIGHT);
		xRangeLabel = new JLabel(X_RANGE_LABEL, JLabel.RIGHT);
		
		resetButton = new JButton(RESET_LABEL);
		
		initSlider = new JSlider(JSlider.HORIZONTAL, INIT_MIN, INIT_MAX, INIT_INIT);
		initSlider.setMajorTickSpacing(5000);
		initSlider.setMinorTickSpacing(1000);
		initSlider.setPaintTicks(true);
		initSlider.setPaintLabels(true);
		rResSlider = new JSlider(JSlider.HORIZONTAL, R_RES_MIN, R_RES_MAX, R_RES_INIT);
		rResSlider.setMajorTickSpacing(5000);
		rResSlider.setMinorTickSpacing(1000);
		rResSlider.setPaintTicks(true);
		rResSlider.setPaintLabels(true);
		xResSlider = new JSlider(JSlider.HORIZONTAL, X_RES_MIN, X_RES_MAX, X_RES_INIT);
		xResSlider.setMajorTickSpacing(5000);
		xResSlider.setMinorTickSpacing(1000);
		xResSlider.setPaintTicks(true);
		xResSlider.setPaintLabels(true);
		
		mainFrame.add(statusBar,BorderLayout.SOUTH);
		mainFrame.add(controlBar, BorderLayout.NORTH);
		mainFrame.add(diagram, BorderLayout.CENTER);
		
		statusBar.add(statusLabel);
		
		controlBar.setLayout(new GridLayout(1,7));
		controlBar.add(initLabel);
		controlBar.add(initSlider);
		controlBar.add(rRangeLabel);
		controlBar.add(rResSlider);
		controlBar.add(xRangeLabel);
		controlBar.add(xResSlider);
		controlBar.add(resetButton);
		
		initSlider.addChangeListener(new SliderListener());
		initSlider.setName(INIT_LABEL);
		rResSlider.addChangeListener(new SliderListener());
		rResSlider.setName(R_RANGE_LABEL);
		xResSlider.addChangeListener(new SliderListener());
		xResSlider.setName(X_RANGE_LABEL);
		resetButton.addActionListener(new ButtonListener());
		resetButton.setActionCommand("reset");
		diagram.addMouseMotionListener(new MotionListener());
		
		mainFrame.setVisible(true);
	}
	
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand(); 
			if( command.equals( "reset" ))  {
	            initValue = initSlider.getValue();
	            xResValue = xResSlider.getValue();
	            rResValue = rResSlider.getValue();
	            statusLabel.setText("reset.");
	         }
		}
	}
	
	private class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			if (source.getValueIsAdjusting()) {
				int fps = (int)source.getValue();
				statusLabel.setText(""+source.getName()+" "+fps);
			}
			else {
				statusLabel.setText("");
			}
		}
	}
	
	private class MotionListener implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			Object source = e.getSource();
			if (source == diagram) {
				Rectangle b = diagram.getBounds();
				int maxCanvasX = (int)b.getMaxX();
				int maxCanvasY = (int)b.getMaxY();
				//double r = minR + e.getX()*maxR/maxCanvasX;
				//double r = e.getX()/(double)maxCanvasX;
				double r = minR + (maxR-minR)*e.getX()/maxCanvasX;
				if (r<minR) r = minR;
				if (r>maxR) r  = maxR;
				double x = maxX + (minX-maxX)*e.getY()/maxCanvasY;
				if (x<minX) x = minX;
				if (x>maxX) x = maxX;
				statusLabel.setText("r="+r+", x="+x);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}
 
	}
	
	private class Diagram extends Canvas {
		private static final long serialVersionUID = -6097526300504399996L;

		@Override
		public void paint(Graphics g) {
			Rectangle b = g.getClipBounds();
			int maxCanvasX = (int)b.getMaxX();
			int maxCanvasY = (int)b.getMaxY();
			LogisticMap lMap = new LogisticMap(0,0);
			
			for (int i=1; i<=rResValue; i++) {
				double r = minR + (maxR-minR)*i/rResValue;
				int rCoord = maxCanvasX*i/rResValue;
				lMap.setR(r);
				lMap.setX(x0);
				//lMap.setX(Math.random());
				lMap.iterate(initValue);
				for (int j=1; j<=xResValue; j++) {
					double x = lMap.next();
					int xCoord = (int)(maxCanvasY*(x-maxX)/(minX-maxX));
					g.drawLine(rCoord,xCoord,rCoord,xCoord);	
				}
			}
		}
	}
}
