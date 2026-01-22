package unam.ciencias.modeladoyprogramacion.raytracer.primitives;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Representa un plano infinito en el espacio 3D.
 *
 * <p>Definido por un punto en el plano y un vector normal.
 *
 * @author Cristopher Carrada
 */
public final class Plane extends Primitive {
  private final Vector3D point;
  private final Vector3D normal;

  /**
   * Construye un plano.
   *
   * @param name nombre del plano
   * @param materialId ID del material
   * @param point un punto en el plano
   * @param normal vector normal al plano
   */
  public Plane(String name, String materialId, Vector3D point, Vector3D normal) {
    super(name, materialId);
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    if (normal == null) {
      throw new IllegalArgumentException("Normal cannot be null");
    }
    this.point = point;
    this.normal = normal.normalize();
  }

  public Vector3D getPoint() {
    return point;
  }

  public Vector3D getNormal() {
    return normal;
  }

  @Override
  public Optional<Double> intersect(Ray ray) {
    // n · d (producto punto de normal y dirección del rayo)
    double denominator = normal.dot(ray.getDirection());

    // Si el denominador es ~0, el rayo es paralelo al plano
    if (Math.abs(denominator) < 1e-6) {
      return Optional.empty();
    }

    // n · (p - o) / (n · d)
    // donde p es un punto del plano, o es el origen del rayo
    double t = normal.dot(point.subtract(ray.getOrigin())) / denominator;

    // Solo intersecciones positivas (delante del rayo)
    if (t > 1e-4) {
      return Optional.of(t);
    }

    return Optional.empty();
  }

  @Override
  public Vector3D getNormalAt(Vector3D point) {
    // El plano tiene normal constante en todos sus puntos
    return normal;
  }

  @Override
  public String toString() {
    return String.format("Plane[name=%s, point=%s, normal=%s]", name, point, normal);
  }
}
