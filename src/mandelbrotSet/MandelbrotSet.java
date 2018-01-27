package mandelbrotSet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MandelbrotSet extends JPanel implements MouseListener{

	//initialize final variables
	final static int FRAME_WIDTH = 2000;
	final static int FRAME_HEIGHT = 1500;
	final static int MAX_ITERATION = 1000;
	final static double ZOOM_FACTOR = 5;
	
	final static double X_LEFT_BOUND = -2.5;
	final static double X_RIGHT_BOUND = 1.5;
	final static double Y_TOP_BOUND = 1.5;
	final static double Y_BOTTOM_BOUND = -1.5;
	
	//variables to keep track of current zoom state
	double currentXLeftBound = X_LEFT_BOUND;
	double currentXRightBound = X_RIGHT_BOUND;
	double currentYTopBound = Y_TOP_BOUND;
	double currentYBottomBound = Y_BOTTOM_BOUND;
	
	//the bufferedImage is our "canvas" for the set
	static BufferedImage image;

	JFrame frame = new JFrame();
	
	ArrayList<HashMap<String, Double>> boundsHistory = new ArrayList<HashMap<String, Double>>(5000);
	int zoomNum = 0;
	
	public MandelbrotSet(){
		image = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		addMouseListener(this);
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("left", X_LEFT_BOUND);
		map.put("right", X_RIGHT_BOUND);
		map.put("top", Y_TOP_BOUND);
		map.put("bottom", Y_BOTTOM_BOUND);
		boundsHistory.add(map);
		
	}
	
	//override the default paint method of JPanel
	@Override
	public void paintComponent(Graphics g){
		//make an array of colorRGB nums that we'll use later to color our Mandelbrot Set - found this online
		int[] colors = new int[MAX_ITERATION];
		for (int i = 0; i<MAX_ITERATION; i++) {
	            colors[i] = Color.HSBtoRGB(i/256f, 1, i/(i+8f));
	        }
		
		//do the actual calculation
		for(int row = 0; row<FRAME_HEIGHT; row++){
			for(int column = 0; column<FRAME_WIDTH; column++){
				double x0 = getScaledX(column);
				double y0 = getScaledY(row);
				double x = 0.0;
				double y = 0.0;
				int iteration = 0;
				
				while((x*x + y*y < 2*2) && iteration < MAX_ITERATION){
					double xtemp = x*x - y*y + x0;
					y = 2*x*y + y0;
					x = xtemp;
					iteration++;
				}
				
				//set Color for the pixel
				//if it escaped, according to how many iterations it took
				//otherwise, set color to black
				
				if(iteration<MAX_ITERATION){
					image.setRGB(column,row,colors[iteration-1]);
					//image.setRGB(column,row,colors[(1000/iteration)-1]);
					//image.setRGB(column, row, (int)((double)iteration/(double)MAX_ITERATION * 16000000));
					double percent = (double)iteration/(double)MAX_ITERATION;
					//image.setRGB(column, row, Color.HSBtoRGB((float)(percent*255), (float)(255-percent*255), (float)(percent*100)));
					//image.setRGB(column, row, iteration);
					//image.setRGB(column, row, 0);
					//image.setRGB(column, row, iteration*iteration);
					//image.setRGB(column, row, iteration+50);
					//image.setRGB(column,row, (int)(Math.sin((double)iteration)*iteration*iteration/Math.tan((double)iteration)));
					//image.setRGB(column, row, new Color((int)(percent*255),(int)(percent*255),(int)(percent*255)).getRGB());
					//image.setRGB(column, row, new Color((int)((percent+15)*(percent+15)),(int)((percent+15)*(percent+15)),(int)((percent+15)*(percent+15))).getRGB());
				}
				else{
					image.setRGB(column,row,0);
					//image.setRGB(column,row,Color.LIGHT_GRAY.getRGB());
					//image.setRGB(column,row,Color.WHITE.getRGB());
				}
			}
		}
		//draw the BufferedImage that we've generated
		g.drawImage(image, 0, 0, FRAME_WIDTH, FRAME_HEIGHT,null);
	}
	
	//converts the frames pixel values to x values, depending on current zoom
	public double getScaledX(int x){
		//divide the value (the location in pixels) by the frame width, multiply it by the current range to get it in the proper "range"
		//then add the left x bound to get the correct x coordinate
		double returnValue = ((double)x/(double)FRAME_WIDTH)*(currentXRightBound - currentXLeftBound) + currentXLeftBound;
		return returnValue;
	}
	
	//converts the frames pixel values to y values, depending on current zoom
	public double getScaledY(int y){
		//divide the value (the location in pixels) by the frame height, multiply it by the current range to get it in the proper "range"
		//to find the proper value, have to make it negative
		//then add the top y bound to get the correct y coordinate
		double returnValue = ((double)y/(double)FRAME_HEIGHT)*-(currentYTopBound - currentYBottomBound) + currentYTopBound;
		return returnValue;
	}
	
	

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1){
			//get location of click in pixels and scale it correctly
			double adjustedXClick = getScaledX(e.getX());
			double adjustedYClick = getScaledY(e.getY());
			
			//find current x and y ranges - we'll use this multiple times
			double currentXRange = currentXRightBound - currentXLeftBound;
			double currentYRange = currentYTopBound - currentYBottomBound;
			
			//we're doing 50% zoom, so cut range in half
			currentXRange = currentXRange/ZOOM_FACTOR;
			currentYRange = currentYRange/ZOOM_FACTOR;
			
			
			//we want the click location to be centered, so we add half the range
			//to either side of the coordinate
			currentXLeftBound = adjustedXClick - currentXRange/2;
			currentXRightBound = adjustedXClick + currentXRange/2;
			currentYTopBound = adjustedYClick + currentYRange/2;
			currentYBottomBound = adjustedYClick - currentYRange/2;
			
			zoomNum++;
			
			HashMap<String, Double> map = new HashMap<String, Double>();
			map.put("left", currentXLeftBound);
			map.put("right",currentXRightBound);
			map.put("top", currentYTopBound);
			map.put("bottom", currentYBottomBound);
			boundsHistory.add(zoomNum, map);
			//repaint the image to display zoomed image
			this.repaint();
		}
		else if (e.getButton() == MouseEvent.BUTTON3){
			if(zoomNum>0){
				zoomNum--;
				currentXLeftBound = boundsHistory.get(zoomNum).get("left");
				currentXRightBound = boundsHistory.get(zoomNum).get("right");
				currentYTopBound = boundsHistory.get(zoomNum).get("top");
				currentYBottomBound = boundsHistory.get(zoomNum).get("bottom");
				
				this.repaint();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
	    MandelbrotSet panel = new MandelbrotSet();
	    panel.repaint();
	    frame.setResizable(false);
		frame.setSize((int)((double)FRAME_WIDTH*1.1), (int)((double)FRAME_HEIGHT*1.1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setVisible(true);
	}
}
