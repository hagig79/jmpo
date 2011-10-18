package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class MpoWriter {
	public static void write(MpoFile mpoFile, OutputStream stream) {

	}

	static byte[] convertByteArray(BufferedImage image) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageOutputStream ios = null;
		ios = ImageIO.createImageOutputStream(bos);
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = writers.next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(1.0f);
		writer.setOutput(ios);
		writer.write(image);
		return bos.toByteArray();
	}
}
