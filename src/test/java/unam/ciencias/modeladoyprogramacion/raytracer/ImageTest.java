package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class ImageTest {

  @Test
  void constructor_createsImageWithCorrectDimensions() {
    Image img = new Image(800, 600);
    assertEquals(600, img.getRows());
    assertEquals(800, img.getColumns());
  }

  @Test
  void setColorAt_andGetColorAt_work() {
    Image img = new Image(10, 10);
    Color red = new Color(255, 0, 0);
    img.setColorAt(5, 5, red);
    Color retrieved = img.getColorAt(5, 5);
    assertEquals(red.getRGB(), retrieved.getRGB());
  }

  @Test
  void build_createsImageFromBufferedImage() {
    BufferedImage buffered = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    buffered.setRGB(10, 10, Color.BLUE.getRGB());
    Image img = Image.build(buffered);
    assertEquals(100, img.getRows());
    assertEquals(100, img.getColumns());
    assertEquals(Color.BLUE.getRGB(), img.getColorAt(10, 10).getRGB());
  }

  @Test
  void buildBufferedImage_createsBufferedImageFromImage() {
    Image img = new Image(50, 50);
    // Inicializar todos los píxeles primero
    for (int r = 0; r < 50; r++) {
      for (int c = 0; c < 50; c++) {
        img.setColorAt(r, c, Color.BLACK);
      }
    }
    img.setColorAt(25, 25, Color.GREEN);
    BufferedImage buffered = Image.buildBufferedImage(img);
    assertEquals(50, buffered.getWidth());
    assertEquals(50, buffered.getHeight());
    assertEquals(Color.GREEN.getRGB(), buffered.getRGB(25, 25));
  }
}
