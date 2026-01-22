package unam.ciencias.modeladoyprogramacion.raytracer.observers;

/**
 * Interfaz Observer para observar el progreso del renderizado.
 *
 * <p>Implementa el patrón Observer (GoF) para desacoplar la lógica de renderizado de la
 * presentación del progreso.
 *
 * @author Cristopher Carrada
 */
public interface RenderProgressListener {
  /**
   * Notifica actualización del progreso de renderizado.
   *
   * @param pixelsRendered número de píxeles ya renderizados
   * @param totalPixels número total de píxeles
   */
  void onProgressUpdate(int pixelsRendered, int totalPixels);

  /**
   * Notifica que se completó un tile de renderizado.
   *
   * @param tileId identificador del tile completado
   */
  void onTileCompleted(int tileId);

  /**
   * Notifica que el renderizado comenzó.
   *
   * @param totalPixels número total de píxeles a renderizar
   */
  void onRenderStart(int totalPixels);

  /**
   * Notifica que el renderizado terminó.
   */
  void onRenderComplete();
}
