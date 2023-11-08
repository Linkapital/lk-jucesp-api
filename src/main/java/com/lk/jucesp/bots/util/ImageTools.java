package com.lk.jucesp.bots.util;

import lombok.AllArgsConstructor;
import lombok.Setter;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;


@AllArgsConstructor
@Setter
public class ImageTools {

    public BufferedImage redimensionImage(BufferedImage originalImage, int targetWidth, int targetHeight){
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public void saveImage(URL url) throws IOException {
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        FileOutputStream fos = new FileOutputStream("captcha.jpg");
        fos.write(response);
        fos.close();
    }

    public void resizeAndSave(String urlParam) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(urlParam));
        BufferedImage originalImage = ImageIO.read(bis);
        BufferedImage modified = redimensionImage(originalImage,288,80);
        File outputfile = new File(urlParam);
        ImageIO.write(modified, "jpg", outputfile);
    }
}
