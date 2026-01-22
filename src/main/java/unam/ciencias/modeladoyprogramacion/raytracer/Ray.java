package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Representa un rayo en el espacio 3D (una semirrecta).
 *
 * <p>Un rayo se define por un punto de origen y una dirección. Matemáticamente: r(t) = origin + t
 * * direction, donde t >= 0.
 *
 * @author Cristopher Carrada
 */
public final class Ray {
  private final Vector3D origin;
  private final Vector3D direction;

  /**
   * Construye un rayo.
   *
   * @param origin punto de origen del rayo
   * @param direction dirección del rayo (debe estar normalizado)
   * @throws IllegalArgumentException si algún parámetro es null
   */
  public Ray(Vector3D origin, Vector3D direction) {
    if (origin == null) {
      throw new IllegalArgumentException("Origin cannot be null");
    }
    if (direction == null) {
      throw new IllegalArgumentException("Direction cannot be null");
    }
    this.origin = origin;
    this.direction = direction.normalize();
  }

  public Vector3D getOrigin() {
    return origin;
  }

  public Vector3D getDirection() {
    return direction;
  }

  /**
   * Calcula un punto en el rayo dado un parámetro t.
   *
   * @param t el parámetro (debe ser >= 0 para puntos válidos en la semirrecta)
   * @return el punto en el rayo: origin + t * direction
   */
  public Vector3D at(double t) {
    return origin.add(direction.multiply(t));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Ray other = (Ray) obj;
    return origin.equals(other.origin) && direction.equals(other.direction);
  }

  @Override
  public int hashCode() {
    return 31 * origin.hashCode() + direction.hashCode();
  }

  @Override
  public String toString() {
    return String.format("Ray[origin=%s, direction=%s]", origin, direction);
  }
}
