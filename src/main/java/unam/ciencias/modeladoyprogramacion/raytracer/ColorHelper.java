package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Clase auxiliar para conversiones y ajustes de color.
 *
 * @author Cristopher Carrada
 */
public final class ColorHelper {
  public static final int MIN_COLOR = 0;
  public static final int MAX_COLOR = 255;

  public static int adjustColor(float color) {
    return adjustColor(Math.round(color));
  }

  public static int adjustColor(int color) {
    return getInRange(color, MIN_COLOR, MAX_COLOR);
  }

  public static int getInRange(int value, int minValue, int maxValue) {
    value = Math.max(value, minValue);
    value = Math.min(maxValue, value);
    return value;
  }

  /**
   * Convierte un Vector3D (color en rango [0,1]) a RGB entero.
   *
   * @param color vector con componentes RGB en [0, 1]
   * @return color RGB como entero (formato 0xRRGGBB)
   */
  public static int vector3DToRGB(Vector3D color) {
    int r = adjustColor((int) (color.getX() * 255));
    int g = adjustColor((int) (color.getY() * 255));
    int b = adjustColor((int) (color.getZ() * 255));
    return (r << 16) | (g << 8) | b;
  }
}
