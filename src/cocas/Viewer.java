package cocas;

/* 
 * Viewer.java
 */

import java.awt.*;
import java.awt.event.*;

public class Viewer extends Panel {
	private Image image;
        public int width;
        public int height;
	public Viewer(String fileName, int w, int h) {
		width = w;
                height = h;
                
                Toolkit toolkit = Toolkit.getDefaultToolkit();
		image = toolkit.getImage(fileName);
                
		MediaTracker mediaTracker = new MediaTracker(this);
		mediaTracker.addImage(image, 0,width, height);
		try
		{
			mediaTracker.waitForID(0);
		}
		catch (InterruptedException ie)
		{
			System.err.println(ie);
			System.exit(1);
		}
                
		//addWindowListener(new WindowAdapter() {
      		//public void windowClosing(WindowEvent e) {
        	//	System.exit(0);
      		//}
		//});
		//setSize(image.getWidth(null), image.getHeight(null));
		
                setSize(width,height);
                //setTitle(fileName);
		show();
	}

	public void paint(Graphics graphics) {
		graphics.drawImage(image, 0, 0,width, height,null);
                
	}

	
}