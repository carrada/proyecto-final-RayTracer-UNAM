package unam.ciencias.modeladoyprogramacion.raytracer.lights;

import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Representa una luz direccional (como el sol).
 *
 * <p>Una luz direccional emite rayos paralelos en una dirección específica desde una distancia
 * infinita. Es útil para simular luz solar o cualquier fuente de luz muy lejana.
 *
 * @author Cristopher Carrada
 */
public final class DirectionalLight extends Light {
  private final Vector3D direction;

  /**
   * Construye una luz direccional.
   *
   * @param color color de la luz
   * @param intensity intensidad de la luz
   * @param direction dirección de los rayos de luz (se normalizará automáticamente)
   */
  public DirectionalLight(Vector3D color, double intensity, Vector3D direction) {
    super(color, intensity);
    if (direction == null) {
      throw new IllegalArgumentException("Direction cannot be null");
    }
    // Normalizar y negar para obtener dirección hacia la fuente
    this.direction = direction.normalize();
  }

  /**
   * Obtiene la dirección de la luz.
   *
   * @return dirección normalizada de los rayos de luz
   */
  public Vector3D getDirection() {
    return direction;
  }

  @Override
  public Vector3D getPosition() {
    // Las luces direccionales no tienen posición específica
    return null;
  }

  @Override
  public Vector3D getDirectionFrom(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    // La dirección es constante desde cualquier punto (rayos paralelos)
    // Negamos la dirección para apuntar hacia la fuente de luz
    return direction.negate();
  }

  @Override
  public double getDistanceFrom(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    // Luz infinitamente lejana
    return Double.POSITIVE_INFINITY;
  }

  @Override
  public String toString() {
    return String.format("DirectionalLight[direction=%s, intensity=%.2f]", direction, intensity);
  }
}
