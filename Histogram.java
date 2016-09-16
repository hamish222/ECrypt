import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.*;


public class Histogram extends JFrame{
	BufferedImage bimage;
	IPanel image;
	WritableRaster raster;
	int [] freqs;
	int maxfreq;
	int w = 550;
	int h = 250;
	int zoom = 0;
	//JPanel graphPanel = new JPanel();
	JPanel controlPanel = new JPanel();
	JSlider slider = new JSlider();

	public Histogram(int [] freqsData, int xLoc, int yLoc) {
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		freqs = freqsData;
		maxfreq=freqs[0];
		setTitle("Byte Histogram");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(xLoc,yLoc);
		setSize(w+35,h+40);
		bimage = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
		raster = bimage.getRaster();
		image = new IPanel(bimage);
		setLayout(new BorderLayout());
		drawHistogram();
		add(BorderLayout.CENTER,image);
		slider.setOrientation(JSlider.VERTICAL);
		slider.setValue(zoom);
		slider.addChangeListener(new SliderChange());
		controlPanel.add(slider);
		add(BorderLayout.EAST,controlPanel);
		//pack();
		setVisible(true);
	}

	public class SliderChange implements ChangeListener {
		public void stateChanged(ChangeEvent ce){
			zoom = slider.getValue();
			drawHistogram();
			validate();
//			invalidate();
//			validate();
			setVisible(false);
			setVisible(true);
		}
	}

	private void drawHistogram(){	
		int x, y;
		int i;
		double barheight;

		for (x=0; x<w; x++)
			for (y=0; y<h; y++)
				raster.setSample(x,y,0,0xff);
		for (x=20; x< w-20; x++)
			raster.setSample(x, 240, 0, 0x00);
		for (y=10; y< h-10; y++)
			raster.setSample(20, y, 0, 0xff);
		for (i=0; i<freqs.length; i++)
			if (freqs[i]>maxfreq) maxfreq = freqs[i];
		//if (maxfreq>1000) maxfreq=1000;
		for (i=0; i<freqs.length; i++){
			barheight = ((1.0 + 10.0*zoom/100.0)*freqs[i])/maxfreq;
			barheight *= (h-20);
			bar(i*2,0,(int)barheight);
		}

	}

	private void bar(int x, int y, int l){
		int dx=0, dy=0;
		int i;
		dx = 20+x;
		dy = h-y-10;
		if (l>220) l = 220;
		for (i=0; i<l; i++)
		{
			raster.setSample(dx,dy,0,0x70);
			raster.setSample(dx+1,dy,0,0x70);  // Remove this line to make the bars half as wide.
			dy--;
		}
	}

	private class IPanel extends JPanel{
		private BufferedImage bufimage;

		public IPanel(BufferedImage img){
			bufimage = img;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(bufimage,0,0,null);
		}
	}


}
