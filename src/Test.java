import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import jp.skr.soundwing.mpo.MPOFile;
import jp.skr.soundwing.mpo.MPOLoader;

public class Test {
	public static void main(String[] args) {
		String path = "testdata/HNI_0043.MPO.jpg";
		File file = new File(path);
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			final MPOFile image = MPOLoader.read(is);
			int width = 640;
			int height = 480;
			int[] pixels1 = new int[640 * 480];
			image.getLeftImage().getBufferedImage()
					.getRGB(0, 0, 640, 480, pixels1, 0, 640);
			int[] pixels2 = new int[640 * 480];
			image.getRightImage().getBufferedImage()
					.getRGB(0, 0, 640, 480, pixels2, 0, 640);
			//
			// int[] out = calcZ(pixels1, pixels2, 640);
			// final BufferedImage image3 = new BufferedImage(width, height,
			// BufferedImage.TYPE_INT_ARGB);
			// image3.setRGB(0, 0, width, height,out, 0, width);
			int windowX = 200;
			int windowY = 100;
			int windowWidth = 50;
			int windowHeight = 50;
			drawRect(image.getLeftImage().getBufferedImage(), windowX, windowY,
					windowWidth, windowHeight, Color.red);
			int d = calcD(pixels2, pixels1, 640, windowX, windowY, windowWidth,
					windowHeight);
			System.out.println("d = " + d);
			drawRect(image.getRightImage().getBufferedImage(), windowX - d,
					windowY, windowWidth, windowHeight, Color.red);
			JFrame frame = new JFrame() {
				@Override
				public void paint(Graphics g) {
					g.drawImage(image.getLeftImage().getBufferedImage(), 0, 0,
							this);
					// g.drawImage(image3, 0, 0, this);
					g.drawImage(image.getRightImage().getBufferedImage(), 640,
							0, this);
				}
			};
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(640 * 2, 480);
			frame.setVisible(true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param pixel1
	 * @param pixel2
	 * @param width
	 * @return
	 */
	public static int[] calcZ(int[] pixel1, int[] pixel2, int width) {
		int[] img_out = (int[]) pixel1.clone();
		int height = pixel1.length / width;

		int red1, green1, blue1, red2, green2, blue2, mono;
		for (int i = 0; i < pixel1.length; i++) {
			red1 = (pixel1[i] >>> 16) & 0xff;
			green1 = (pixel1[i] >>> 8) & 0xff;
			blue1 = pixel1[i] & 0xff;
			red2 = (pixel2[i] >>> 16) & 0xff;
			green2 = (pixel2[i] >>> 8) & 0xff;
			blue2 = pixel2[i] & 0xff;

			img_out[i] = (0xff << 24) | ((Math.abs(red1 - red2) & 0xff) << 16)
					| ((Math.abs(green1 - green2) & 0xff) << 8)
					| (Math.abs(blue1 - blue2) & 0xff);
		}
		return img_out;
	}

	/**
	 * @param pixel1
	 * @param pixel2
	 * @param width
	 * @param windowX
	 * @param windowY
	 * @param windowWidth
	 * @param windowHeight
	 * @return
	 */
	public static int calcD(int[] pixel1, int[] pixel2, int width, int windowX,
			int windowY, int windowWidth, int windowHeight) {
		int height = pixel1.length / width;
		int score = Integer.MAX_VALUE;
		int d = windowWidth;
		int red1, green1, blue1, red2, green2, blue2;
		int p1, p2;
		for (int i = windowX + 100; i >= 0; i--) {
			int s = 0;
			for (int j = 0; j < windowHeight; j++) {
				for (int k = 0; k < windowWidth; k++) {
					p1 = i + k + (windowY + j) * width;
					red1 = (pixel1[p1] >>> 16) & 0xff;
					green1 = (pixel1[p1] >>> 8) & 0xff;
					blue1 = pixel1[p1] & 0xff;
					p2 = windowX + k + (windowY + j) * width;
					red2 = (pixel2[p2] >>> 16) & 0xff;
					green2 = (pixel2[p2] >>> 8) & 0xff;
					blue2 = pixel2[p2] & 0xff;

					s += Math.abs(red1 - red2) + Math.abs(green1 - green2)
							+ Math.abs(blue1 - blue2);
				}
			}
			if (s < score) {
				score = s;
				d = i;
			}
		}

		return windowX - d;
	}

	/**
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	static void drawRect(BufferedImage image, int x, int y, int width,
			int height, Color color) {
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.drawRect(x, y, width, height);
	}
}
