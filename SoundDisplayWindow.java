import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

// Possibly helpful websites: 
//		http://stackoverflow.com/questions/938304/how-to-get-audio-data-from-a-mp3

// Issues:
// 		1. Parse the data for display once instead of multiple times (histogram, time plot, spectrogram...)
//		2. Sound doesn't play when ECrypt is run from a jar file - assuming it was double-clicked to start.  
//			(Using java -jar ECrypt.jar works just fine.)

public class SoundDisplayWindow extends JFrame{
	SoundDisplayWindow me = this;
	public ViewButtonListener buttonListener = new ViewButtonListener();
	JMenuBar menuBar = new JMenuBar(  );
	static String filePath="";

	AudioFormat baseFormat;
	AudioInputStream in = null;

	byte[] clipBytes;
	int numBytesRead = 0;
	int numFramesRead = 0;
	int totalFramesRead = 0;
	int bytesPerFrame;
	int clipSize = 0;

	Clip clip;

	BufferedImage bimage;
	IPanel image;
	WritableRaster raster;
	int w = 580;
	int h = 260;
	int maxwidth = 2500;
	int minwidth = 400; 

	JButton playButton = new JButton("Play");
	JLabel  timeLabel = new JLabel("    Length:");
	JLabel  timeUnits = new JLabel("sec");
	JLabel  samplerateLabel = new JLabel("    Sample rate:");
	JTextField timeField = new JTextField(5);
	JTextField sampleRate = new JTextField(5);
	JPanel controls = new JPanel();
	JScrollPane waveForm;


	// Constructor
	public SoundDisplayWindow(String title, int yLoc){
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		//ClassLoader.getResourceAsStream(); 
		setSize(w,h+80);
		setTitle(title);
		setLayout(new BorderLayout());
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem clearText = new JMenuItem("Clear");
		JMenuItem openFile = new JMenuItem("Open");
		JMenuItem saveFile = new JMenuItem("Save");
		fileMenu.add(clearText);
		fileMenu.addSeparator(  );	
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		clearText.addActionListener(new ClearListener());
		openFile.addActionListener(new OpenFileListener());
		saveFile.addActionListener(new SaveFileListener());
		menuBar.add(fileMenu);
		menuBar.add(makeAnalysisMenu());
		setJMenuBar(menuBar);

		controls.add(playButton); 
		playButton.addActionListener(new PlayButtonListener());
		controls.add(samplerateLabel);
		controls.add(sampleRate);
		controls.add(timeLabel);
		controls.add(timeField);
		controls.add(timeUnits);
		add(BorderLayout.SOUTH,controls);

		// Create the wave display area
		bimage = new BufferedImage(maxwidth,h,BufferedImage.TYPE_BYTE_GRAY);
		raster = bimage.getRaster();
		clearTimePlot();
		image = new IPanel(bimage);
		//waveForm = new JScrollPane(image);
		add(BorderLayout.CENTER,image);
		MyMouseMotionListener doScrollRectToVisible = new MyMouseMotionListener();	
		image.addMouseMotionListener(doScrollRectToVisible);

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocation(200,yLoc);
		setVisible(false);
	}


	public JMenu makeAnalysisMenu(){
		JMenu analyzeMenu = new JMenu("Analysis");
		JMenuItem monographFreq = new JMenuItem("Byte Frequencies");
		JMenuItem IoC = new JMenuItem("Index of Coincidence");
		//	analyzeMenu.addSeparator();
		analyzeMenu.add(monographFreq); monographFreq.addActionListener(new MonographActionListener());
		analyzeMenu.add(IoC); IoC.addActionListener(new IoCActionListener());
		return analyzeMenu;
	}

	@Override
	protected void finalize() throws Throwable {
		if (clip != null)
			clip.close();
	}

	public class MonographActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int [] freqs = new int[256];
			int i;
			for (i=0; i<256; i++) freqs[i]=0;
			
			int bytesPerSample = bytesPerFrame/baseFormat.getChannels();
			int shift = 0;
			if (bytesPerFrame>1 && !baseFormat.isBigEndian()) shift = 1;
			i = 0;
			while (i<(clipSize-shift)/bytesPerFrame){
				if (bytesPerSample==1)
					freqs[clipBytes[i*bytesPerFrame+shift] & 0xff]++;
				else
					freqs[MainWindow.RealMod((clipBytes[i*bytesPerFrame+shift] & 0xff) + 128,256)]++;
				i++;
			}
//			for (i=0; i<clipSize; i++){
//				freqs[clipBytes[i]&0xff]++;
//			}
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + w-8;
			if (xLoc>1000) xLoc = 0;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new Histogram(freqs,xLoc,yLoc);

		}
	}

	private class MyMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			Rectangle r = new Rectangle(e.getX(), e.getY(), 1,1);
			((JPanel)e.getSource()).scrollRectToVisible(r);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

	}

	public class IoCActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int[] data = new int[clipSize];
			int i;
			for (i=0; i<clipSize; i++) data[i] = clipBytes[i]&0xff;
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + w-8;
			if (xLoc>1000) xLoc = 0;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new IoC(getTitle(),data, xLoc, yLoc);
		}
	}

	private class ClearListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			clearTimePlot();
			clipSize=0;
			timeField.setText("");
			sampleRate.setText("");
		}
	}

	private class PlayButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try {
				if (clip != null)
					clip.stop();
				clip = AudioSystem.getClip();
				clip.open(baseFormat, clipBytes, 0, clipSize);
				clip.start();
			} catch (LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	
	}
	private class OpenFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e)
		{	
			// This code almost handles .au files, but the time plot is incorrect.  This code now only allows .wav files in audio mode.
			byte[] audioBytes;
			int audioSize;

			numBytesRead = 0;
			numFramesRead = 0;
			totalFramesRead = 0;

			int i;

			final JFileChooser fc = new JFileChooser(filePath);
			//final FileFilter filter = new FileNameExtensionFilter("Sound Files", "wav", "au", "aiff");
			final FileFilter filterwav = new FileNameExtensionFilter("WAV (*.wav)", "wav");

			fc.addChoosableFileFilter(filterwav);
			//			fc.addChoosableFileFilter(filter);
			//			int returnVal = fc.showOpenDialog(getParent());
			int returnVal = fc.showOpenDialog(me);
			if (returnVal==JFileChooser.CANCEL_OPTION) return;
			File file = fc.getSelectedFile();
			String filename, ext;
			try{
				filename = file.getAbsolutePath();
			} catch(NullPointerException e1){
				filename="";
			};
			if (filename.length()>3)
				ext = filename.substring(filename.length()-3,filename.length());
			else
				ext="";
			
			if (!ext.equals("wav")){
				returnVal = JFileChooser.ERROR_OPTION;
			}

			switch (returnVal){
			case JFileChooser.APPROVE_OPTION:
				filePath = file.getPath();
				break;
			case JFileChooser.ERROR_OPTION:
				JOptionPane.showMessageDialog(null, filename + " does not appear to be a .wav file.","Error",JOptionPane.ERROR_MESSAGE);
				return;
			default: //returnVal != JFileChooser.CANCEL_OPTION)
				JOptionPane.showMessageDialog(null, "Error locating the selected file " + file.getPath()+".","Error",JOptionPane.ERROR_MESSAGE);
				return;
			};

			//AudioInputStream in = null;
			//filename = "/flanders.wav";
			//System.out.println("InputStream filename: "+filename);
			
			//ClassLoader cl = Resource.class.getClassLoader();
			//InputStream ais=null;
			//ais = getClass().getClassLoader().getResourceAsStream(filename);
			//if (ais==null) System.out.println("It's null alright!!");
			
			
			//FileInputStream fis=null;
			//try {
			//	fis = new FileInputStream(file);
			//} catch (FileNotFoundException e4) {
			//	e4.printStackTrace();
			//}
			//if (fis==null) System.out.println("fis is null too"); else System.out.println("fis isn't null");
			
			try {
				in = AudioSystem.getAudioInputStream(file);
			} catch (FileNotFoundException e3){
				JOptionPane.showMessageDialog(null, filename + " does not exist.","Error",JOptionPane.ERROR_MESSAGE);
				return;
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (EOFException e2) {
				e2.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (NullPointerException e3){
				System.out.println("'in' is null");
			}
			baseFormat = in.getFormat();
			bytesPerFrame = baseFormat.getFrameSize();
			sampleRate.setText(String.valueOf((int)baseFormat.getSampleRate()));
			if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) bytesPerFrame = 1;
			audioBytes = new byte[1024*bytesPerFrame];
			audioSize = (int) (in.getFrameLength() * bytesPerFrame);
			clipBytes = new byte[audioSize];
			int j=0;
			try {
				while ((numBytesRead = in.read(audioBytes)) != -1) {
					numFramesRead = numBytesRead / bytesPerFrame;
					totalFramesRead += numFramesRead;
					for (i=0; i<numBytesRead; i++){
						if (j<clipBytes.length) clipBytes[j++]=audioBytes[i];
					}
				}
				clipSize = j;
				updateWindow();
				/*
				timeField.setText(String.valueOf( ((totalFramesRead*100)/(int)baseFormat.getFrameRate())/100.0));
				w = clipSize/(128*bytesPerFrame);
				if (w<minwidth) w = minwidth;
				setSize(w,h+80);

				System.out.println("# of channels: "+baseFormat.getChannels());
				System.out.println("frame rate: "+baseFormat.getFrameRate());
				System.out.println("frame size: "+baseFormat.getFrameSize());
				System.out.println("sample rate: "+baseFormat.getSampleRate());
				System.out.println("big endian?: "+baseFormat.isBigEndian());
				System.out.println("\n");

				drawTimePlot();
				drawSpectrogram();	
				 */
			} catch (IOException e1) {
				e1.printStackTrace();

			} 
			}
		}

		private void updateWindow(){
			//System.out.println("totalFramesRead = "+totalFramesRead+"\n");
			timeField.setText(String.valueOf( ((totalFramesRead*100)/(int)baseFormat.getFrameRate())/100.0));
			sampleRate.setText(String.valueOf((int)baseFormat.getSampleRate()));
			w = clipSize/(128*bytesPerFrame);
			if (w<minwidth) w = minwidth;
			setSize(w,h+80);
			drawTimePlot();
			drawSpectrogram();	
		}

		private class SaveFileListener implements ActionListener{
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser fc = new JFileChooser(filePath);
				final FileFilter filterwav = new FileNameExtensionFilter("WAV Files (*.wav)", "wav");

				fc.addChoosableFileFilter(filterwav);
				int returnVal = fc.showDialog(me,"Save");
				File file = fc.getSelectedFile();
				String filename, ext;
				try{
					filename = file.getPath();
				} catch(NullPointerException e1){
					filename="";
				};

				if (filename.length()>3)
					ext = filename.substring(filename.length()-4,filename.length());
				else
					ext="";
				
				if (!ext.equals(".wav")){
					filename += ".wav";
					file = new File(filename);
				}

				ByteArrayInputStream b_in = new ByteArrayInputStream(clipBytes);
				AudioInputStream ais = new AudioInputStream(b_in,baseFormat,clipSize);
				try {	
					AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);  // This can be changed to other formats like AU and still work.
				} catch (NullPointerException e5) {
					//System.out.println("NullPointerException");
				} catch (IOException e3) {
					e3.printStackTrace();
				} catch (IllegalArgumentException e4) {
				//	System.out.println("IllegalArgumentException");
				};
			}
		}

		public class ViewButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				setVisible(true);
			}
		}

		private void clearTimePlot(){	
			int x, y;
			for (x=0; x<maxwidth; x++)
				for (y=0; y<h; y++)
					raster.setSample(x,y,0,0xff);
		}

		private void drawTimePlot(){	
			int x, y;
			int i;
			int compressionFactor = 128;  // How many bytes appear at each x value.
			int shift=1;

			clearTimePlot();
			if (bytesPerFrame==1){
				for (i=0; i<clipSize; i++){
					x = i/compressionFactor;
					y = (int)clipBytes[i]&0xff;
					if (x>=maxwidth){
						JOptionPane.showMessageDialog(null, "The display has been limited.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					raster.setSample(x,(h/4-64)+(255-y)/2,0,0x60);
				}  }
			else{
				int bytesPerSample = bytesPerFrame/baseFormat.getChannels();
				if (baseFormat.isBigEndian()) shift=0;
				for (i=0; i<clipSize; i+=bytesPerFrame)
				{
					x = i/compressionFactor/bytesPerFrame;
					if (x>=maxwidth) break;
					if (bytesPerSample>1) {
						y = (int)clipBytes[i+shift];
						raster.setSample(x,h/4-1-y/2,0,0x60);
					}
					else {
						y = (int)clipBytes[i+shift]&0x0ff;
						raster.setSample(x,(h/4-64)+(255-y)/2,0,0x60);
					}

					/*
			// The previous block of commented code is the original.  The next block attempts to handle bigger frames.
			if (baseFormat.isBigEndian()) shift = 0;
			for (i=0; i<clipSize/bytesPerFrame; i++){
				x = i/compressionFactor;
				y = (int)clipBytes[bytesPerFrame*i+shift];
				if (x>=maxwidth){
					JOptionPane.showMessageDialog(null, "The display has been limited.","Warning",JOptionPane.WARNING_MESSAGE);
					break;
				}
				raster.setSample(x,h/4-1-y/2,0,0xa0);
					 */	
				}
			}
			setVisible(false);
			setVisible(true);
		}

		private void drawSpectrogram(){
			//  CONSULT Project2_KEY.nb in my private MA 321 folder for more details.
			int fftSize = 256; 	// Make sure this is a power of 2.
			//int overlap = fftSize/2; 	// This version includes an overlap to try to (appx) double the width of the spectrogram.
		//	int maxFreq = 5000;	// Most human speech is below 5 kHz.
		//	int sr = baseFormat.getChannels(); 	// Get the sample rate from the data.
			double[] relFreqs = new double[fftSize];
			int[][] grayLevels = new int[2*clipSize/(fftSize*bytesPerFrame) - 1][fftSize/2];   // Only need half of the transform because of symmetry.
			int block, sample;

			for (block=0; block<2*clipSize/(fftSize*bytesPerFrame) - 1; block++){
				relFreqs = FourierTransform(block*fftSize*bytesPerFrame/2,fftSize);
				for (sample=0; sample<fftSize/2; sample++){
					grayLevels[block][sample] = (int) Math.floor(255*relFreqs[sample]);
				}
			}
			int x, y;
			for (block=0; block<2*clipSize/(fftSize*bytesPerFrame) - 1; block++){
				x = block;
				if (x>=maxwidth){
					JOptionPane.showMessageDialog(null, "The display has been limited.","Warning",JOptionPane.WARNING_MESSAGE);
					break;
				}
				for (sample=0; sample<fftSize/2; sample++){
					y = h - sample - 1;
					raster.setSample(x,y,0,grayLevels[block][sample]);
				}
			}
		}

		public double[] FourierTransform(int start, int fftSize) {
			// This returns the scaled magnitudes (between 0 and 1) of the FFT of an array of integers. 
			// The data clipBytes[start]...clipByts[start+fftSize-1].
			Complex [] temp = new Complex[fftSize];
			double [] abstemp = new double[fftSize];
			double abstempmax;
			int i;
			int shift=0;

			if (bytesPerFrame>1 && !baseFormat.isBigEndian()) shift = 1;
			for (i=0; i<fftSize; i++){
				temp[i] = new Complex(1.0*(clipBytes[start+i*bytesPerFrame+shift] & 0xff),0.0);
			}
			temp = fft(temp);
			for (i=0; i<fftSize; i++){
				abstemp[i] = temp[i].abs();
			}
			abstempmax = 0;
			for (i=1; i<fftSize; i++){   // Skip the first few frequencies (e.g. i=0...9) because they are typically very large compared to the rest.
				if (abstemp[i]>abstempmax) abstempmax = abstemp[i];
			}
			if (abstempmax==0) abstempmax = 1.0;
			for (i=0; i<fftSize; i++){
				if (abstemp[i]>abstempmax) abstemp[i] = 1.0;
				else abstemp[i] = abstemp[i]/abstempmax;
			}
			return abstemp;
		}

		private class IPanel extends JPanel{
			private BufferedImage bufimage;

			public IPanel(BufferedImage img){
				bufimage = img;
				setAutoscrolls(true);
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bufimage,0,0,null);
			}
		}

		public AudioFormat getFormat(){
			return baseFormat;
		}

		public void setFormat(AudioFormat format){
			if (format==null) return;
			if (clipBytes==null) return;
			baseFormat = format;
			bytesPerFrame = baseFormat.getFrameSize();
			totalFramesRead = clipBytes.length/bytesPerFrame;
			updateWindow();
		}

		public byte[] getData()
		{
			int i;
			byte[] data = new byte[clipBytes.length];
			for (i=0; i<clipBytes.length; i++) data[i] = clipBytes[i];
			return data;
		}

		public void setData(byte[] data)
		{
			int i;
			if (data==null) return;
			clipBytes = new byte[data.length];
			for (i=0; i<data.length; i++)
				clipBytes[i] = data[i];
			clipSize = data.length;
		}

		// Code from http://introcs.cs.princeton.edu/java/97data/FFT.java.html.
		// compute the FFT of x[], assuming its length is a power of 2
		// This is equivalent to Mathematica's Fourier with FourierParameters->{1,-1}
		public Complex[] fft(Complex[] x) {
			int N = x.length;

			if (N == 1) return new Complex[] { x[0] };  // base case     
			if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }  // radix 2 Cooley-Tukey FFT

			// fft of even terms
			Complex[] even = new Complex[N/2];
			for (int k = 0; k < N/2; k++) even[k] = x[2*k];
			Complex[] q = fft(even);

			// fft of odd terms
			Complex[] odd  = even;  // reuse the array
			for (int k = 0; k < N/2; k++) odd[k] = x[2*k + 1];
			Complex[] r = fft(odd);

			// combine
			Complex[] y = new Complex[N];
			for (int k = 0; k < N/2; k++) {
				double kth = -2 * k * Math.PI / N;
				Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
				y[k]       = q[k].plus(wk.times(r[k]));
				y[k + N/2] = q[k].minus(wk.times(r[k]));
			}
			return y;
		}

		// Code from http://introcs.cs.princeton.edu/java/32class/Complex.java.html.
		public class Complex {
			private final double re;   // the real part
			private final double im;   // the imaginary part

			public Complex(double real, double imag) {
				re = real;
				im = imag;
			}

			public String toString() {
				if (im == 0) return re + "";
				if (re == 0) return im + "i";
				if (im <  0) return re + " - " + (-im) + "i";
				return re + " + " + im + "i";
			}

			public double abs()   { return Math.hypot(re, im); }  // Math.sqrt(re*re + im*im)
			public double phase() { return Math.atan2(im, re); }  // between -pi and pi

			public Complex plus(Complex b) {
				Complex a = this;             // invoking object
				double real = a.re + b.re;
				double imag = a.im + b.im;
				return new Complex(real, imag);
			}

			public Complex minus(Complex b) {
				Complex a = this;
				double real = a.re - b.re;
				double imag = a.im - b.im;
				return new Complex(real, imag);
			}

			public Complex times(Complex b) {
				Complex a = this;
				double real = a.re * b.re - a.im * b.im;
				double imag = a.re * b.im + a.im * b.re;
				return new Complex(real, imag);
			}

			public Complex times(double alpha) {return new Complex(alpha * re, alpha * im);}
			public Complex conjugate() {  return new Complex(re, -im); }

			public Complex reciprocal() {
				double scale = re*re + im*im;
				return new Complex(re / scale, -im / scale);
			}

			public double re() { return re; }
			public double im() { return im; }

			public Complex divides(Complex b) {
				Complex a = this;
				return a.times(b.reciprocal());
			}

			public Complex exp() {return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));}
			public Complex sin() {return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));}
			public Complex cos() {return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));}
			public Complex tan() {return sin().divides(cos());}

			public Complex plus(Complex a, Complex b) {
				double real = a.re + b.re;
				double imag = a.im + b.im;
				Complex sum = new Complex(real, imag);
				return sum;
			}
		}

	}

