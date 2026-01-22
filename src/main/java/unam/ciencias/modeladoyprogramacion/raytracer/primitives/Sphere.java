package unam.ciencias.modeladoyprogramacion.raytracer.primitives;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Representa una esfera en el espacio 3D.
 *
 * <p>Definida por un centro y un radio.
 *
 * @author Cristopher Carrada
 */
public final class Sphere extends Primitive {
  private final Vector3D center;
  private final double radius;

  /**
   * Construye una esfera.
   *
   * @param name nombre de la esfera
   * @param materialId ID del material
   * @param center centro de la esfera
   * @param radius radio de la esfera
   */
  public Sphere(String name, String materialId, Vector3D center, double radius) {
    super(name, materialId);
    if (center == null) {
      throw new IllegalArgumentException("Center cannot be null");
    }
    if (radius <= 0) {
      throw new IllegalArgumentException("Radius must be positive");
    }
    this.center = center;
    this.radius = radius;
  }

  public Vector3D getCenter() {
    return center;
  }

  public double getRadius() {
    return radius;
  }

  @Override
  public Optional<Double> intersect(Ray ray) {
    // Vector del origen del rayo al centro de la esfera
    Vector3D oc = ray.getOrigin().subtract(center);

    // Coeficientes de la ecuación cuadrática: at² + bt + c = 0
    double a = ray.getDirection().dot(ray.getDirection());
    double b = 2.0 * oc.dot(ray.getDirection());
    double c = oc.dot(oc) - radius * radius;

    // Discriminante
    double discriminant = b * b - 4 * a * c;

    if (discriminant < 0) {
      return Optional.empty(); // No hay intersección
    }

    // Calcular las dos soluciones
    double sqrtDiscriminant = Math.sqrt(discriminant);
    double t1 = (-b - sqrtDiscriminant) / (2.0 * a);
    double t2 = (-b + sqrtDiscriminant) / (2.0 * a);

    // Retornar la intersección más cercana que sea positiva
    if (t1 > 1e-4) {
      return Optional.of(t1);
    } else if (t2 > 1e-4) {
      return Optional.of(t2);
    }

    return Optional.empty();
  }

  @Override
  public Vector3D getNormalAt(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    return point.subtract(center).normalize();
  }

  @Override
  public String toString() {
    return String.format("Sphere[name=%s, center=%s, radius=%.4f]", name, center, radius);
  }
}
