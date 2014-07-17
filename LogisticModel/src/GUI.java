import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class GUI {
	/* Constants */
	static final String FRAME_LABEL = "Logistic Model";
	static final String INIT_LABEL = "Initial iterations:";
	static final String R_RANGE_LABEL = "R resolution:";
	static final String X_RANGE_LABEL = "X resolution:";
	static final String RESET_LABEL = "Reset";
	static final int X_SIZE = 640;
	static final int Y_SIZE = 480;
	static final int INIT_MIN = 0;
	static final int INIT_MAX = 10000;
	static final int INIT_INIT = 1000;
	static final int R_RES_MIN = 1000;
	static final int R_RES_MAX = 10000;
	static final int R_RES_INIT = 3000;
	static final int X_RES_MIN = 1;
	static final int X_RES_MAX = 10000;
	static final int X_RES_INIT = 1000;
	static final double X0 = 0.001;
	static final double MIN_R = 1;	// At least 1.0
	static final double MAX_R = 4.0;	// At most 4.0
	static final double MIN_X = 0.0;	// At least 0.0
	static final double MAX_X = 1.0;	// At most 1.0
	
	private double x0 = X0;
	private double minR = MIN_R;
	private double maxR = MAX_R;
	private double minX = MIN_X;
	private double maxX = MAX_X;
	
	private int initValue = INIT_INIT;
	private int rResValue = R_RES_INIT;
	private int xResValue = X_RES_INIT;
	
	/* Zooming variables */
	private boolean isZooming = false;
	private int zoomMinCanvasX;
	private int zoomMinCanvasY;
	private int zoomMaxCanvasX;
	private int zoomMaxCanvasY;
	
	private JFrame mainFrame; // The main panel.
	private JPanel statusBar;
	private JPanel controlBar;
	private Diagram diagram; // Bifurcation Diagram.
	private JLabel statusLabel; // Should read the coordinates of the mouse.
	private JLabel initLabel;
	private JLabel rResLabel;
	private JLabel xResLabel;
	private JButton resetButton;
	private JSlider initSlider;
	private JSlider rResSlider;
	private JSlider xResSlider;
	
	public GUI() {
		mainFrame = new JFrame(FRAME_LABEL);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setSize(X_SIZE,Y_SIZE);
		
		statusBar = new JPanel();
		controlBar = new JPanel();
		diagram = new Diagram();
		
		statusLabel = new JLabel("",JLabel.CENTER);
		initLabel = new JLabel(INIT_LABEL,JLabel.RIGHT);
		rResLabel = new JLabel(R_RANGE_LABEL, JLabel.RIGHT);
		xResLabel = new JLabel(X_RANGE_LABEL, JLabel.RIGHT);
		
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
		controlBar.add(rResLabel);
		controlBar.add(rResSlider);
		controlBar.add(xResLabel);
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
		diagram.addMouseListener(new MouseClickListener());
		
		mainFrame.setVisible(true);
	}
	
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand(); 
			if( command.equals( "reset" ))  { 
				x0 = X0;
				minR = MIN_R;
				maxR = MAX_R;
				minX = MIN_X;
				maxX = MAX_X;
				initValue = INIT_INIT;
				initSlider.setValue(INIT_INIT);
				rResValue = R_RES_INIT;
				rResSlider.setValue(R_RES_INIT);
				xResValue = X_RES_INIT;
				xResSlider.setValue(X_RES_INIT);
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
				int fps = (int)source.getValue();
				if (source == initSlider) initValue = fps;
				else if (source == rResSlider) rResValue = fps;
				else if (source == xResSlider) xResValue = fps;
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
				double r = minR + (maxR-minR)*e.getX()/maxCanvasX;
				if (r<minR) r = minR;
				if (r>maxR) r  = maxR;
				double x = maxX + (minX-maxX)*e.getY()/maxCanvasY;
				if (x<minX) x = minX;
				if (x>maxX) x = maxX;
				statusLabel.setText("r="+r+", x="+x);
				
				/* Draw zooming rectangle */
				if (!isZooming) {
					isZooming = true;
					zoomMinCanvasX = e.getX();
					zoomMinCanvasY = e.getY();
					zoomMaxCanvasX = e.getX();
					zoomMaxCanvasY = e.getY();
				}
				Graphics g = diagram.getGraphics();
				zoomMinCanvasX = Math.min(zoomMinCanvasX,e.getX());
				zoomMinCanvasY = Math.min(zoomMinCanvasY,e.getY());
				zoomMaxCanvasX = Math.max(zoomMaxCanvasX,e.getX());
				zoomMaxCanvasY = Math.max(zoomMaxCanvasY,e.getY());
				g.drawRect(zoomMinCanvasX, zoomMinCanvasY, zoomMaxCanvasX-zoomMinCanvasX, zoomMaxCanvasY-zoomMinCanvasY);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			isZooming = false;
			statusLabel.setText("");
		}
 
	}
	
	private class MouseClickListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isZooming = false;
			Object source = e.getSource();
			if (source == diagram) {
				Rectangle b = diagram.getBounds();
				int maxCanvasX = (int)b.getMaxX();
				int maxCanvasY = (int)b.getMaxY();
				maxX = maxX + (minX-maxX)*zoomMinCanvasY/maxCanvasY;
				minX = maxX + (minX-maxX)*zoomMaxCanvasY/maxCanvasY;
				minR = minR + (maxR-minR)*zoomMinCanvasX/maxCanvasX;
				maxR = minR + (maxR-minR)*zoomMaxCanvasX/maxCanvasX;
			}
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
				//lMap.setX(Math.random()); //Random initial value instead of constant.
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
