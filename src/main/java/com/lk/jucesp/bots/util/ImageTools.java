package com.lk.jucesp.bots.util;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

@AllArgsConstructor
@Slf4j
@Setter
public class ImageTools {

    public BufferedImage redimensionImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }

    public void saveImage(URL url) {
        try (InputStream in = new BufferedInputStream(url.openStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             FileOutputStream fos = new FileOutputStream("captcha.jpg")) {

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }

            fos.write(out.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void resizeAndSave(String urlParam) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(urlParam))) {
            BufferedImage originalImage = ImageIO.read(bis);
            BufferedImage modified = redimensionImage(originalImage, 288, 80);
            File outputfile = new File(urlParam);
            ImageIO.write(modified, "jpg", outputfile);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
