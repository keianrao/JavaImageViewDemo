
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;


class JavaImageViewDemo implements KeyListener {

private JFrame mainframe;
private ImageView imageView;
private JFileChooser fileChooser;
private Timer timer;

private BufferedImage image;

private static final int FPS = 30, X_SPEED = 8, Y_SPEED = 8, ZOOM_SPEED = 8;


//  \\  //  \\  //  \\  //  \\  //  \\

public void keyPressed(KeyEvent eK) {
	// We should get a menubar instead, actually..
	if (eK.getKeyCode() == KeyEvent.VK_F1) {
		if (fileChooser.showOpenDialog(mainframe)
				== JFileChooser.APPROVE_OPTION) try {
			image = ImageIO.read(fileChooser.getSelectedFile());
			
			if (image != null) {
				setCaption(fileChooser.getSelectedFile().getName());
			}
			else {
				JOptionPane.showMessageDialog(
					mainframe,
					"Our image loader didn't extract any image from the file..",
					"No image loaded!",
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
		catch (IOException eIo) {
			JOptionPane.showMessageDialog(
				mainframe,
				"Sorry, our image loader says it had a problem loading the file you selected..",
				"Could not open file!",
				JOptionPane.ERROR_MESSAGE
			);
			eIo.printStackTrace(System.err);
		}
	}
}

private void setCaption(String caption)
{
	mainframe.setTitle(caption + " - JavaImageViewDemo");
}

public void keyReleased(KeyEvent eK) { }
public void keyTyped(KeyEvent eK) { }

//  \\  //  \\  //  \\  //  \\  //  \\

private JavaImageViewDemo() {
	mainframe = new JFrame();
	
	imageView = new ImageView();
	imageView.addKeyListener(this);
	mainframe.setContentPane(imageView);
	
	mainframe.setSize(800, 600);
	mainframe.setLocationRelativeTo(null);	
	mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	fileChooser = new JFileChooser();
	
	setCaption("(No image loaded)");
	
	mainframe.setVisible(true);
	
	timer = new Timer();
	timer.schedule(new RepainterTask(), 0, 1000 / FPS);
}

//  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String... args) {
	new JavaImageViewDemo();
}

//  \\  //  \\  //  \\  //  \\  //  \\	

private class ImageView extends JPanel implements KeyListener, ComponentListener {
	int topLeftX, topLeftY;
	int width, height;
	int xOffset, yOffset;
	int xMovement, yMovement;
	double aspectRatio;
	int zoomMovement;
	boolean noImageLastRepaint = true;
	
	//  \\  //  \\  //  \\  //  \\  //  \\

	public void paintComponent(Graphics g) {	
		if (JavaImageViewDemo.this.image == null) {
			noImageLastRepaint = true;
			return;
		}
		
		if (noImageLastRepaint) {
			width = ImageView.this.getWidth();
			height = ImageView.this.getHeight();
			noImageLastRepaint = false;
		}
	
		// If zooming in, update width and height.
		if (zoomMovement != 0) {
			width -= zoomMovement * ZOOM_SPEED;
			if (width < 1) {
				width = 1;
			}

			height = (int)Math.floor(width / aspectRatio);			
			if (height > image.getHeight()) {
				height = image.getHeight();
				width = (int)Math.floor(height * aspectRatio);
			}
		}
		
		// If moving around, update topLeftX and topLeftY.
		if (xMovement != 0) {
			topLeftX += xMovement * X_SPEED;
			if (topLeftX < 0) {
				topLeftX = 0;
			}
			if ((topLeftX + width) > image.getWidth()) {
				topLeftX = image.getWidth() - width;
			}
		}		
		if (yMovement != 0) {
			topLeftY += yMovement * Y_SPEED;
			if (topLeftY < 0) {
				topLeftY = 0;
			}
			if ((topLeftY + height) > image.getHeight()) {
				topLeftY = image.getHeight() - height;
			}
		}
		
		// Okay, done adjusting. Render current frame
		g.drawImage(
			(JavaImageViewDemo.this).image,
			0, 0, getWidth(), getHeight(),
			topLeftX, topLeftY, topLeftX + width, topLeftY + height,
			this
		);
	}
	
	public void componentResized(ComponentEvent e) {
		aspectRatio = (double)getWidth() / getHeight();
		height = (int)Math.floor(width / aspectRatio);
	}

	public void keyPressed(KeyEvent eK) {
		switch (eK.getKeyCode()) {
			case KeyEvent.VK_UP:
				yMovement = -1;
				break;		
			case KeyEvent.VK_DOWN:
				yMovement = 1;
				break;
			case KeyEvent.VK_LEFT:
				xMovement = -1;
				break;
			case KeyEvent.VK_RIGHT:
				xMovement = 1;
				break;
			case KeyEvent.VK_R:
				zoomMovement = 1;
				break;
			case KeyEvent.VK_F:
				zoomMovement = -1;
				break;
		}
	}
	public void keyReleased(KeyEvent eK) {
		switch (eK.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				yMovement = 0;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				xMovement = 0;
				break;
			case KeyEvent.VK_R:
			case KeyEvent.VK_F:
				zoomMovement = 0;
				break;
		}
	}
	
	public void componentMoved(ComponentEvent eC) { }
	public void componentShown(ComponentEvent eC) { }
	public void componentHidden(ComponentEvent eC) { }
	public void keyTyped(KeyEvent eK) { }
	
	//  \\  //  \\  //  \\  //  \\  //  \\
	
	ImageView() {
		this.addComponentListener(this);
		this.addKeyListener(this);
		setFocusable(true);
	}
}

private class RepainterTask extends TimerTask {
	public void run() {
		imageView.repaint();
		java.awt.Toolkit.getDefaultToolkit().sync();
		// Unix X11-specific measure
	}
}

}
