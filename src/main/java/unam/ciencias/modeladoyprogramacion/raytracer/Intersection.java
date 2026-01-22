package unam.ciencias.modeladoyprogramacion.raytracer;

import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Primitive;

/**
 * Representa una intersección entre un rayo y una primitiva.
 *
 * <p>Contiene información sobre el punto de intersección, la distancia desde el origen del rayo, y
 * la primitiva intersectada.
 *
 * @author Cristopher Carrada
 */
public final class Intersection {
  private final double distance;
  private final Vector3D point;
  private final Vector3D normal;
  private final Primitive primitive;

  /**
   * Construye una intersección.
   *
   * @param distance distancia desde el origen del rayo hasta el punto de intersección
   * @param point punto de intersección en el espacio 3D
   * @param normal vector normal en el punto de intersección (normalizado)
   * @param primitive la primitiva que fue intersectada
   * @throws IllegalArgumentException si algún parámetro es null o distance es negativa
   */
  public Intersection(double distance, Vector3D point, Vector3D normal, Primitive primitive) {
    if (distance < 0 || !Double.isFinite(distance)) {
      throw new IllegalArgumentException("Distance must be non-negative and finite");
    }
    if (point == null) {
      throw new IllegalArgumentException("Intersection point cannot be null");
    }
    if (normal == null) {
      throw new IllegalArgumentException("Normal vector cannot be null");
    }
    if (primitive == null) {
      throw new IllegalArgumentException("Primitive cannot be null");
    }
    this.distance = distance;
    this.point = point;
    this.normal = normal.normalize();
    this.primitive = primitive;
  }

  public double getDistance() {
    return distance;
  }

  public Vector3D getPoint() {
    return point;
  }

  public Vector3D getNormal() {
    return normal;
  }

  public Primitive getPrimitive() {
    return primitive;
  }

  @Override
  public String toString() {
    return String.format(
        "Intersection[distance=%.4f, point=%s, normal=%s]", distance, point, normal);
  }
}
