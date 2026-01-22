package unam.ciencias.modeladoyprogramacion.raytracer.lights;

import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Clase abstracta que representa una fuente de luz en la escena.
 *
 * @author Cristopher Carrada
 */
public abstract class Light {
  protected final Vector3D color;
  protected final double intensity;

  /**
   * Construye una luz.
   *
   * @param color color de la luz (RGB)
   * @param intensity intensidad de la luz
   */
  protected Light(Vector3D color, double intensity) {
    if (color == null) {
      throw new IllegalArgumentException("Color cannot be null");
    }
    if (intensity < 0) {
      throw new IllegalArgumentException("Intensity must be non-negative");
    }
    this.color = color;
    this.intensity = intensity;
  }

  public Vector3D getColor() {
    return color;
  }

  public double getIntensity() {
    return intensity;
  }

  /**
   * Obtiene la posición de la luz (para luces puntuales).
   *
   * @return posición de la luz, o null si no aplica
   */
  public abstract Vector3D getPosition();

  /**
   * Calcula la dirección desde un punto hacia la luz.
   *
   * @param point punto desde el cual calcular la dirección
   * @return vector dirección hacia la luz (normalizado)
   */
  public abstract Vector3D getDirectionFrom(Vector3D point);

  /**
   * Calcula la distancia desde un punto hasta la luz.
   *
   * @param point punto desde el cual calcular la distancia
   * @return distancia a la luz
   */
  public abstract double getDistanceFrom(Vector3D point);
}
