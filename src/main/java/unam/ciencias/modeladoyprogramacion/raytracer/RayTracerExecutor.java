package unam.ciencias.modeladoyprogramacion.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executor para ray tracing desde la línea de comandos.
 *
 * <p>Carga una escena desde JSON, renderiza la imagen y la guarda en PNG.
 *
 * @author Cristopher Carrada
 */
public final class RayTracerExecutor implements CLIOperationExecutor {
  // Logger para registrar información y errores durante la ejecución
  private static final Logger logger = LoggerFactory.getLogger(RayTracerExecutor.class);

  /**
   * Constructor que inicializa el executor con las opciones de CLI.
   *
   * @param options Opciones proporcionadas desde la línea de comandos.
   */
  public RayTracerExecutor(CLIOptions options) {
    if (options == null) {
      throw new IllegalArgumentException("Options cannot be null");
    }
  }

  /**
   * Método principal que ejecuta el ray tracing.
   *
   * @param options Opciones de entrada y salida, así como configuración de hilos.
   */
  @Override
  public void execute(CLIOptions options) {
    // Archivo de entrada que contiene la escena en formato JSON
    String inputFile = options.input();
    // Archivo de salida donde se guardará la imagen renderizada
    String outputFile = options.output();

    // Validación de que los archivos de entrada y salida no sean nulos o vacíos
    if (inputFile == null || inputFile.isEmpty()) {
      throw new IllegalArgumentException("Input file is required for ray-tracer operation");
    }

    if (outputFile == null || outputFile.isEmpty()) {
      throw new IllegalArgumentException("Output file is required for ray-tracer operation");
    }

    try {
      // Carga de la escena desde el archivo JSON
      logger.info("Loading scene from: {}", inputFile);
      SceneLoader loader = new SceneLoader();
      Scene scene = loader.loadFromFile(inputFile);

      // Renderizado de la imagen con el número de hilos especificado
      logger.info(
          "Rendering image ({}x{}) with {} threads...",
          scene.getImageWidth(),
          scene.getImageHeight(),
          options.threads());

      RayTracer rayTracer = new RayTracer(scene, options.threads());
      Image image = rayTracer.render();

      // Guardado de la imagen renderizada en el archivo de salida
      logger.info("Saving image to: {}", outputFile);
      BufferedImage bufferedImage = Image.buildBufferedImage(image);

      File outputImageFile = new File(outputFile);
      File parentDir = outputImageFile.getParentFile();
      if (parentDir != null) {
        parentDir.mkdirs();
      }
      ImageIO.write(bufferedImage, "PNG", outputImageFile);

      logger.info("Rendering complete!");

    } catch (IOException e) {
      // Manejo de errores durante el proceso de renderizado o guardado
      logger.error("Error during ray tracing: {}", e.getMessage(), e);
      System.exit(1);
    }
  }
}
