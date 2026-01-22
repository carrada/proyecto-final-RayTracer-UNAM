package unam.ciencias.modeladoyprogramacion.raytracer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import unam.ciencias.modeladoyprogramacion.raytracer.observers.RenderProgressListener;

/**
 * Motor de Ray Tracing.
 *
 * <p>Renderiza una escena disparando rayos desde la cámara y calculando intersecciones.
 *
 * @author Cristopher Carrada
 */
public final class RayTracer extends MultiThreadedOperation {
  private static final int PROGRESS_UPDATE_INTERVAL = 1000;
  
  private final Scene scene;
  private final PhongShader shader;
  private final List<RenderProgressListener> progressListeners;

  /**
   * Construye un ray tracer.
   *
   * @param scene la escena a renderizar
   */
  public RayTracer(Scene scene) {
    this(scene, 1);
  }

  /**
   * Construye un ray tracer con multithreading.
   *
   * @param scene la escena a renderizar
   * @param threads número de hilos a usar
   */
  public RayTracer(Scene scene, int threads) {
    super(threads);
    if (scene == null) {
      throw new IllegalArgumentException("Scene cannot be null");
    }
    this.scene = scene;
    this.shader = new PhongShader(scene);
    this.progressListeners = new ArrayList<>();
  }

  /**
   * Agrega un listener de progreso (patrón Observer).
   *
   * @param listener el listener a agregar
   */
  public void addProgressListener(RenderProgressListener listener) {
    if (listener != null) {
      progressListeners.add(listener);
    }
  }

  /**
   * Remueve un listener de progreso.
   *
   * @param listener el listener a remover
   */
  public void removeProgressListener(RenderProgressListener listener) {
    progressListeners.remove(listener);
  }

  /**
   * Notifica a todos los listeners de una actualización de progreso.
   */
  private void notifyProgressUpdate(int pixelsRendered, int totalPixels) {
    for (RenderProgressListener listener : progressListeners) {
      listener.onProgressUpdate(pixelsRendered, totalPixels);
    }
  }

  /**
   * Notifica a todos los listeners que el renderizado comenzó.
   */
  private void notifyRenderStart(int totalPixels) {
    for (RenderProgressListener listener : progressListeners) {
      listener.onRenderStart(totalPixels);
    }
  }

  /**
   * Notifica a todos los listeners que el renderizado terminó.
   */
  private void notifyRenderComplete() {
    for (RenderProgressListener listener : progressListeners) {
      listener.onRenderComplete();
    }
  }

  /**
   * Renderiza la escena completa.
   *
   * @return imagen renderizada
   */
  public Image render() {
    int width = scene.getImageWidth();
    int height = scene.getImageHeight();
    int totalPixels = width * height;

    // Notificar inicio
    notifyRenderStart(totalPixels);

    // Usar AtomicIntegerArray para eliminar locks
    AtomicIntegerArray pixelData = new AtomicIntegerArray(totalPixels);
    AtomicInteger pixelsCompleted = new AtomicInteger(0);

    Camera camera = scene.getCamera();
    double aspectRatio = (double) width / height;
    double fovRadians = Math.toRadians(camera.getFov());
    double viewportHeight = 2.0 * Math.tan(fovRadians / 2.0);
    double viewportWidth = viewportHeight * aspectRatio;

    // Vectores de la base de la cámara
    Vector3D w = camera.getDirection().negate(); // Apunta hacia atrás
    Vector3D u = camera.getUp().cross(w).normalize(); // Derecha
    Vector3D v = w.cross(u); // Arriba real

    // Esquina superior izquierda del viewport
    Vector3D horizontal = u.multiply(viewportWidth);
    Vector3D vertical = v.multiply(viewportHeight);
    Vector3D lowerLeft =
        camera
            .getPosition()
            .subtract(horizontal.multiply(0.5))
            .subtract(vertical.multiply(0.5))
            .subtract(w);

    // Dividir el trabajo en bloques de filas para cada thread
    List<Thread> threadList = new ArrayList<>();
    int rowsPerThread = (int) Math.ceil((double) height / threads);

    for (int t = 0; t < threads; t++) {
      final int startRow = t * rowsPerThread;
      final int endRow = Math.min(startRow + rowsPerThread, height);

      Thread thread =
          new Thread(
              () -> {
                for (int row = startRow; row < endRow; row++) {
                  for (int col = 0; col < width; col++) {
                    // Calcular color del píxel (función pura)
                    Vector3D color =
                        calculatePixelColor(row, col, width, height, lowerLeft, horizontal, vertical, camera);

                    // Escribir resultado (efecto secundario)
                    int rgb = ColorHelper.vector3DToRGB(color);
                    pixelData.set(row * width + col, rgb);

                    // Notificar progreso (efecto secundario)
                    notifyProgress(pixelsCompleted, totalPixels);
                  }
                }
              });
      threadList.add(thread);
    }

    // Ejecutar y esperar a que terminen todos los threads
    try {
      runAndWaitForThreads(threadList);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Rendering interrupted", e);
    }

    // Construir imagen desde AtomicIntegerArray
    Image image = new Image(width, height);
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        int rgb = pixelData.get(row * width + col);
        image.setValue(row, col, rgb);
      }
    }

    // Notificar finalización
    notifyRenderComplete();

    return image;
  }

  /**
   * Traza un rayo y calcula su color.
   *
   * @param ray el rayo a trazar
   * @param depth profundidad de recursión
   * @return color del rayo
   */
  private Vector3D traceRay(Ray ray, int depth) {
    if (depth >= scene.getMaxBounces()) {
      return scene.getBackgroundColor();
    }

    Optional<Intersection> intersection = scene.intersect(ray);
    if (intersection.isEmpty()) {
      return scene.getBackgroundColor();
    }

    return shader.shade(intersection.get(), ray, depth);
  }

  /**
   * Calcula el color de un píxel mediante muestreo Monte Carlo.
   *
   * <p>Esta función es PURA: no modifica estado y siempre produce el mismo resultado para los
   * mismos parámetros (salvo por el componente aleatorio del antialiasing).
   *
   * @param row fila del píxel
   * @param col columna del píxel
   * @param width ancho de la imagen
   * @param height altura de la imagen
   * @param lowerLeft esquina inferior izquierda del viewport
   * @param horizontal vector horizontal del viewport
   * @param vertical vector vertical del viewport
   * @param camera cámara de la escena
   * @return color final promediado de las muestras
   */
  private Vector3D calculatePixelColor(
      int row,
      int col,
      int width,
      int height,
      Vector3D lowerLeft,
      Vector3D horizontal,
      Vector3D vertical,
      Camera camera) {
    Vector3D color = new Vector3D(0, 0, 0);

    // Múltiples muestras por píxel (antialiasing básico)
    for (int s = 0; s < scene.getSamplesPerPixel(); s++) {
      double uOffset = (col + (s > 0 ? Math.random() : 0.5)) / width;
      double vOffset = (row + (s > 0 ? Math.random() : 0.5)) / height;

      Vector3D pixelCenter =
          lowerLeft.add(horizontal.multiply(uOffset)).add(vertical.multiply(vOffset));

      Ray ray = new Ray(camera.getPosition(), pixelCenter.subtract(camera.getPosition()));
      color = color.add(traceRay(ray, 0));
    }

    // Promediar las muestras
    return color.multiply(1.0 / scene.getSamplesPerPixel());
  }

  /**
   * Notifica el progreso del renderizado de forma thread-safe.
   *
   * <p>EFECTO SECUNDARIO: Incrementa contador atómico y notifica a observers.
   *
   * @param pixelsCompleted contador atómico de píxeles completados
   * @param totalPixels total de píxeles a renderizar
   */
  private void notifyProgress(AtomicInteger pixelsCompleted, int totalPixels) {
    int completed = pixelsCompleted.incrementAndGet();
    if (completed % PROGRESS_UPDATE_INTERVAL == 0 || completed == totalPixels) {
      notifyProgressUpdate(completed, totalPixels);
    }
  }
}
