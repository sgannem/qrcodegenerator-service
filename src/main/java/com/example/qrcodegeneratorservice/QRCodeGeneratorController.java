package com.example.qrcodegeneratorservice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class QRCodeGeneratorController {


    final static Logger logger = LoggerFactory.getLogger(QRCodeGeneratorController.class);
    /**
     * //data that we want to store in the QR code
     * String str= "THE HABIT OF PERSISTENCE IS THE HABIT OF VICTORY.";
     * //path where we want to get QR Code
     * String path = "C:\\Users\\Anubhav\\Desktop\\QRDemo\\Quote.png";
     * //Encoding charset to be used
     * String charset = "UTF-8";
     * Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
     * //generates QR code with Low level(L) error correction capability
     * hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
     * //invoking the user-defined method that creates the QR code
     * generateQRcode(str, path, charset, hashMap, 200, 200);//increase or decrease height and width accodingly
     * //prints if the QR code is generated
     * System.out.println("QR Code created successfully.");
    **/
    public void generateQRcode(String data, String path, String charset, Map map, int h, int w) throws WriterException, IOException
    {
        //the BitMatrix class represents the 2D matrix of bits
        //MultiFormatWriter is a factory class that finds the appropriate Writer subclass for the
        // BarcodeFormat requested and encodes the barcode with the supplied contents.
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> qrCodeGenerator(@RequestParam(name="value", required=false, defaultValue="Hello") String value) {
        logger.info("#qrCodeGenerator is called");
        String path = "./myqrcode.png";
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        try {
            generateQRcode(value, path, charset, hashMap, 200, 200);
        } catch (Exception e) {
            logger.error("#Error occurred during QA code generation");
        }
        InputStream in = null;
        try {
            in = new FileInputStream(new File(path));
        }catch (Exception e) {
            e.printStackTrace();
        }
        byte[] b = null;
        try {
            b = IOUtils.toByteArray(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ResponseEntity<byte[]> response = new ResponseEntity<>(b, HttpStatus.ACCEPTED);
        return response;
    }

}
