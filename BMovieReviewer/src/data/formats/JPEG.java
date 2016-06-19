package data.formats;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.filechooser.FileNameExtensionFilter;

import tools.AppLogger;
import data.Bogen;
import data.Globals;

public class JPEG extends BogenFormat {

	static final String FORMAT_EXTENSION = "jpeg";

	public static final String PROPERTY_JPEG_MIME = "jpeg.mime";
	public static final String PROPERTY_JPEG_ID = "jpeg.id";
	public static final String PROPERTY_JPEG_QUALITY = "jpeg.quality";

	public JPEG(Bogen bogen) {
		super(bogen);
	}

	public JPEG(Bogen bogen, int scaledWidth) {
		super(bogen);
		this.setScaledWidth(scaledWidth);
	}

	@Override
	public String getFormatExtension() {
		return JPEG.FORMAT_EXTENSION;
	}

	@Override
	public FileNameExtensionFilter getFileNameExtensionFilter() {
		throw new UnsupportedOperationException();
	}

	// ////////////////////////////////////////////////////////////////////////
	// INTERNALS
	// ////////////////////////////////////////////////////////////////////////

	@Override
	protected void write() throws IOException {
		BufferedImage image = bogen.getCoverImage();
        ImageWriter imageWriter = getImageWriter();
        JPEGImageWriteParam imageWriteParam = getImageWriteParam();
        IIOImage iioImage = new IIOImage(image, null, null);
        ImageOutputStream imageOut = ImageIO.createImageOutputStream(out);
        imageWriter.setOutput(imageOut);
        imageWriter.write(null, iioImage, imageWriteParam);
	}
	
	protected ImageWriter getImageWriter() {
		Globals globs = Globals.getInstance();
		String mime = globs.getProperty(PROPERTY_JPEG_ID);
        Iterator<ImageWriter> iterator = ImageIO.getImageWritersBySuffix(mime);
        return (ImageWriter) iterator.next();     
	}
	
	protected JPEGImageWriteParam getImageWriteParam() {
		float quality = getImageQuality();
		JPEGImageWriteParam imageWriteParam = new JPEGImageWriteParam(Locale
                .getDefault());
        imageWriteParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(quality);
        return imageWriteParam;
	}

	@Override
	protected void read() throws IOException {
		BufferedImage image, loadedImage;
		int scaledHeight = 0, width = 0;
		image = ImageIO.read(this.in); 
		if (image == null) {
			throw new IOException("Bild konnte nicht geladen werden");
		}

		scaledHeight = image.getHeight();
		width = image.getWidth();
		if (scaledHeight == 0 || width == 0) {
			AppLogger.info("loadImage: Bild wird nicht skaliert");
			return;
		}
		scaledHeight *= scaledWidth;
		scaledHeight /= width;

		loadedImage = new BufferedImage(scaledWidth, scaledHeight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = loadedImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
		this.bogen.setCoverImage(loadedImage);
	}

	protected float getImageQuality() {
		Globals globs = Globals.getInstance();
		float quality = 0.85f;
		try {
			quality = Float.parseFloat(globs.getProperty(PROPERTY_JPEG_QUALITY));
			if (quality > 1.0f| quality < 0) {
				AppLogger
						.severe("Bildqualitaet auf unzulaessigen Wert gestellt");
				quality = 0.85f;
			}
		} catch (NumberFormatException e) {
			AppLogger.throwing("BareFiles", "getImageQuality()", e);
		}
		return quality;
	}

	protected void setScaledWidth(int sw) {
		if (scaledWidth < 0) {
			throw new IllegalArgumentException();
		}
		this.scaledWidth = sw;
	}

	protected int scaledWidth;
}
