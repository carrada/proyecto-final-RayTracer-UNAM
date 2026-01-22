package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Representa un vector en el espacio tridimensional (R³).
 *
 * <p>Proporciona operaciones básicas de álgebra vectorial como suma, resta, producto escalar,
 * producto punto y producto cruz.
 *
 * @author Cristopher Carrada
 */
public final class Vector3D {
  private final double x;
  private final double y;
  private final double z;

  /**
   * Construye un vector 3D.
   *
   * @param x componente x
   * @param y componente y
   * @param z componente z
   */
  public Vector3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  /**
   * Suma este vector con otro.
   *
   * @param other el otro vector
   * @return un nuevo vector resultado de la suma
   */
  public Vector3D add(Vector3D other) {
    if (other == null) {
      throw new IllegalArgumentException("Vector cannot be null");
    }
    return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
  }

  /**
   * Resta otro vector de este.
   *
   * @param other el vector a restar
   * @return un nuevo vector resultado de la resta
   */
  public Vector3D subtract(Vector3D other) {
    if (other == null) {
      throw new IllegalArgumentException("Vector cannot be null");
    }
    return new Vector3D(this.x - other.x, this.y - other.y, this.z - other.z);
  }

  /**
   * Multiplica este vector por un escalar.
   *
   * @param scalar el valor escalar
   * @return un nuevo vector resultado de la multiplicación
   */
  public Vector3D multiply(double scalar) {
    return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar);
  }

  /**
   * Calcula el producto punto (dot product) con otro vector.
   *
   * @param other el otro vector
   * @return el producto punto
   */
  public double dot(Vector3D other) {
    if (other == null) {
      throw new IllegalArgumentException("Vector cannot be null");
    }
    return this.x * other.x + this.y * other.y + this.z * other.z;
  }

  /**
   * Calcula el producto cruz (cross product) con otro vector.
   *
   * @param other el otro vector
   * @return un nuevo vector resultado del producto cruz
   */
  public Vector3D cross(Vector3D other) {
    if (other == null) {
      throw new IllegalArgumentException("Vector cannot be null");
    }
    return new Vector3D(
        this.y * other.z - this.z * other.y,
        this.z * other.x - this.x * other.z,
        this.x * other.y - this.y * other.x);
  }

  /**
   * Calcula la magnitud (longitud) del vector.
   *
   * @return la magnitud del vector
   */
  public double magnitude() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * Normaliza el vector (lo convierte en un vector unitario).
   *
   * @return un nuevo vector normalizado
   * @throws ArithmeticException si el vector tiene magnitud cero
   */
  public Vector3D normalize() {
    double mag = magnitude();
    if (mag == 0) {
      throw new ArithmeticException("Cannot normalize zero vector");
    }
    return new Vector3D(x / mag, y / mag, z / mag);
  }

  /**
   * Calcula la distancia a otro vector.
   *
   * @param other el otro vector
   * @return la distancia entre los vectores
   */
  public double distance(Vector3D other) {
    if (other == null) {
      throw new IllegalArgumentException("Vector cannot be null");
    }
    return this.subtract(other).magnitude();
  }

  /**
   * Niega el vector (invierte su dirección).
   *
   * @return un nuevo vector con componentes negadas
   */
  public Vector3D negate() {
    return new Vector3D(-x, -y, -z);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Vector3D other = (Vector3D) obj;
    return Double.compare(other.x, x) == 0
        && Double.compare(other.y, y) == 0
        && Double.compare(other.z, z) == 0;
  }

  @Override
  public int hashCode() {
    int result = Double.hashCode(x);
    result = 31 * result + Double.hashCode(y);
    result = 31 * result + Double.hashCode(z);
    return result;
  }

  @Override
  public String toString() {
    return String.format("Vector3D[%.4f, %.4f, %.4f]", x, y, z);
  }
}
