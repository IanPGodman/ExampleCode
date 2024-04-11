package coding.example.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;


public class ImageService {
    private final static Logger log = LogManager.getLogger();

    private static final int MIN_SIZE = 512;
    private static final int MAX_SIZE = 1024;

    private final ResourceLoader resourceLoader;

    public ImageService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    public String detectImageType(MultipartFile file) {
        Tika tika = new Tika();
        try {
            return tika.detect(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Calculates the scale factor based on the given size and scaleUp flag.
     *
     * @param size The size of the image.
     * @param scaleUp Flag indicating whether to scale up or down.
     * @return The calculated scale factor.
     */
    private int calculateScale(int size, boolean scaleUp) {
        int scaleSize = scaleUp ? MIN_SIZE : MAX_SIZE;
        int scale = scaleUp ? scaleSize / size : size / scaleSize;
        return scaleUp ? Math.min(scale, 1) : Math.max(scale, 1);
    }

    public Optional<String> scaleAndSaveImage(MultipartFile file, String name) {
        BufferedImage srcImage;

        try {
            String imageType = detectImageType(file);

            try(InputStream inputStream = file.getInputStream()){
                srcImage = ImageIO.read(inputStream);
            }

            // Scale the image so height and width are less than 1024 and greater than 512 preserving aspect ratio.
            int width = srcImage.getWidth();
            int height = srcImage.getHeight();
            int scaledWidth = width;
            int scaledHeight = height;
            if (width > MAX_SIZE || height > MAX_SIZE) { // scale down
                int scale= Math.max(calculateScale(width, false), calculateScale(height, false));
                scaledHeight = height / scale;
                scaledWidth = width / scale;
            }
            else if (width < MIN_SIZE || height < MIN_SIZE) { // scale up
                int scale= Math.min(calculateScale(width, true), calculateScale(height, true));
                scaledWidth = width * scale;
                scaledHeight = height * scale;
            }

            BufferedImage targetImage = Scalr.resize(srcImage, Scalr.Method.BALANCED, scaledWidth, scaledHeight);

            Resource resource = resourceLoader.getResource("classpath:static/images/");
            URI uri = resource.getURI();
            log.info("URI: {}", uri);
            String imageTypeName = (imageType.equals("image/png") ? "png" : "jpg");
            String imageName = name + "." + imageTypeName;
            String uriPath = uri.getPath()+ imageName;
            log.info("Writing file to: {}", uriPath);
            File outputImageFile = new File(uriPath);
            ImageIO.write(targetImage, imageTypeName, outputImageFile);
            return Optional.of(imageName);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
