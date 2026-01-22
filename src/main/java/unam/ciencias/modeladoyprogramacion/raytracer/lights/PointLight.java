package unam.ciencias.modeladoyprogramacion.raytracer.lights;

import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Representa una luz puntual en el espacio.
 *
 * <p>Una luz puntual emite luz desde un punto específico en todas direcciones.
 *
 * @author Cristopher Carrada
 */
public final class PointLight extends Light {
  private final Vector3D position;

  /**
   * Construye una luz puntual.
   *
   * @param color color de la luz
   * @param intensity intensidad
   * @param position posición de la luz en el espacio
   */
  public PointLight(Vector3D color, double intensity, Vector3D position) {
    super(color, intensity);
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    this.position = position;
  }

  @Override
  public Vector3D getPosition() {
    return position;
  }

  @Override
  public Vector3D getDirectionFrom(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    return position.subtract(point).normalize();
  }

  @Override
  public double getDistanceFrom(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    return position.distance(point);
  }

  @Override
  public String toString() {
    return String.format("PointLight[position=%s, intensity=%.2f]", position, intensity);
  }
}
