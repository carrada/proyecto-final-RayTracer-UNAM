package unam.ciencias.modeladoyprogramacion.raytracer.observers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener de progreso que muestra el progreso en consola usando SLF4J.
 *
 * @author Cristopher Carrada
 */
public final class ConsoleProgressListener implements RenderProgressListener {
  private static final Logger logger = LoggerFactory.getLogger(ConsoleProgressListener.class);
  private int lastPercentage = -1;

  @Override
  public void onProgressUpdate(int pixelsRendered, int totalPixels) {
    int percentage = (int) ((pixelsRendered * 100.0) / totalPixels);
    if (percentage != lastPercentage && percentage % 10 == 0) {
      logger.info("Rendering progress: {}%", percentage);
      lastPercentage = percentage;
    }
  }

  @Override
  public void onTileCompleted(int tileId) {
    // Opcional: mostrar tiles completados
  }

  @Override
  public void onRenderStart(int totalPixels) {
    logger.info("Starting render: {} pixels", totalPixels);
  }

  @Override
  public void onRenderComplete() {
    logger.info("Rendering complete: 100%");
  }
}
