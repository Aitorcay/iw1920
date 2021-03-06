package es.ucm.fdi.iw.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.constants.ConstantsPdfFile;
import es.ucm.fdi.iw.model.User;

/**
 * Generates a QR code with a link to the profile of a student
 * @author aitorcay
 */

public class QrGenerator {
	
	private static final Logger log = LogManager.getLogger(QrGenerator.class);
	
	
	/**
	 * Genera el código QR de acceso para un usuario
	 * 
	 * @param id		id del usuario
	 * @param username	nombre de usario
	 * @throws UnknownHostException
	 */
	public static void generateQrCode(User user) throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();		
		String url = "http://" + inetAddress.getHostAddress() + ":" +ConstantsPdfFile.PORT + "/token/" + user.getToken();
		int size = ConstantsPdfFile.QR_IMG_SIZE;
		String fileType = ConstantsPdfFile.PNG;
		
		File directory = new File(ConstantsPdfFile.QR_DIR);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	    
	    String QrPath = ConstantsPdfFile.QR_DIR + ConstantsPdfFile.QR_IMG + user.getUsername() + "." + ConstantsPdfFile.PNG;
		File myFile = new File(QrPath);
		
		try {
			
			Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			
			// Now with zxing version 3.2.1 you could change border size (white border size to just 1)
			hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
 
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size,
					size, hintMap);
			int CrunchifyWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
					BufferedImage.TYPE_INT_RGB);
			image.createGraphics();
 
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
			graphics.setColor(Color.BLACK);
 
			for (int i = 0; i < CrunchifyWidth; i++) {
				for (int j = 0; j < CrunchifyWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}			
			
			ImageIO.write(image, fileType, myFile);
						
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("\n\nYou have successfully created QR Code for user {}", user.getId());
	}
}
