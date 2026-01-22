package unam.ciencias.modeladoyprogramacion.raytracer.primitives;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Representa un paralelepípedo (caja) alineado con los ejes coordenados.
 *
 * <p>Definido por una esquina mínima y dimensiones (ancho, alto, profundidad).
 *
 * @author Cristopher Carrada
 */
public final class Box extends Primitive {
  private final Vector3D min;
  private final Vector3D max;

  /**
   * Construye una caja.
   *
   * @param name nombre de la caja
   * @param materialId ID del material
   * @param min esquina mínima (x, y, z más pequeños)
   * @param width ancho (dimensión en X)
   * @param height alto (dimensión en Y)
   * @param depth profundidad (dimensión en Z)
   */
  public Box(
      String name, String materialId, Vector3D min, double width, double height, double depth) {
    super(name, materialId);
    if (min == null) {
      throw new IllegalArgumentException("Min corner cannot be null");
    }
    if (width <= 0 || height <= 0 || depth <= 0) {
      throw new IllegalArgumentException("Dimensions must be positive");
    }
    this.min = min;
    this.max = new Vector3D(min.getX() + width, min.getY() + height, min.getZ() + depth);
  }

  public Vector3D getMin() {
    return min;
  }

  public Vector3D getMax() {
    return max;
  }

  @Override
  public Optional<Double> intersect(Ray ray) {
    // Algoritmo de intersección rayo-AABB (Axis-Aligned Bounding Box)
    Vector3D invDir =
        new Vector3D(
            1.0 / ray.getDirection().getX(),
            1.0 / ray.getDirection().getY(),
            1.0 / ray.getDirection().getZ());

    double tx1 = (min.getX() - ray.getOrigin().getX()) * invDir.getX();
    double tx2 = (max.getX() - ray.getOrigin().getX()) * invDir.getX();

    double tmin = Math.min(tx1, tx2);
    double tmax = Math.max(tx1, tx2);

    double ty1 = (min.getY() - ray.getOrigin().getY()) * invDir.getY();
    double ty2 = (max.getY() - ray.getOrigin().getY()) * invDir.getY();

    tmin = Math.max(tmin, Math.min(ty1, ty2));
    tmax = Math.min(tmax, Math.max(ty1, ty2));

    double tz1 = (min.getZ() - ray.getOrigin().getZ()) * invDir.getZ();
    double tz2 = (max.getZ() - ray.getOrigin().getZ()) * invDir.getZ();

    tmin = Math.max(tmin, Math.min(tz1, tz2));
    tmax = Math.min(tmax, Math.max(tz1, tz2));

    if (tmax < tmin || tmax < 1e-4) {
      return Optional.empty();
    }

    double t = tmin > 1e-4 ? tmin : tmax;
    return t > 1e-4 ? Optional.of(t) : Optional.empty();
  }

  @Override
  public Vector3D getNormalAt(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }

    // Determinar qué cara de la caja está más cerca del punto
    double epsilon = 1e-4;
    Vector3D center =
        new Vector3D(
            (min.getX() + max.getX()) / 2,
            (min.getY() + max.getY()) / 2,
            (min.getZ() + max.getZ()) / 2);

    Vector3D d = point.subtract(center);
    double maxComponent = Math.max(Math.abs(d.getX()), Math.max(Math.abs(d.getY()), Math.abs(d.getZ())));

    if (Math.abs(Math.abs(d.getX()) - maxComponent) < epsilon) {
      return new Vector3D(Math.signum(d.getX()), 0, 0);
    } else if (Math.abs(Math.abs(d.getY()) - maxComponent) < epsilon) {
      return new Vector3D(0, Math.signum(d.getY()), 0);
    } else {
      return new Vector3D(0, 0, Math.signum(d.getZ()));
    }
  }

  @Override
  public String toString() {
    return String.format("Box[name=%s, min=%s, max=%s]", name, min, max);
  }
}
