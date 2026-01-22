package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Representa una matriz 3x3.
 *
 * <p>Proporciona operaciones básicas de álgebra matricial y transformaciones geométricas.
 *
 * @author Cristopher Carrada
 */
public final class Matrix3x3 {
  private final double[][] data;

  /**
   * Construye una matriz 3x3 a partir de un arreglo bidimensional.
   *
   * @param data arreglo 3x3 con los valores de la matriz
   * @throws IllegalArgumentException si el arreglo no es 3x3
   */
  public Matrix3x3(double[][] data) {
    if (data == null || data.length != 3) {
      throw new IllegalArgumentException("Matrix must be 3x3");
    }
    for (int i = 0; i < 3; i++) {
      if (data[i] == null || data[i].length != 3) {
        throw new IllegalArgumentException("Matrix must be 3x3");
      }
    }
    this.data = new double[3][3];
    for (int i = 0; i < 3; i++) {
      System.arraycopy(data[i], 0, this.data[i], 0, 3);
    }
  }

  /**
   * Construye una matriz identidad.
   *
   * @return matriz identidad 3x3
   */
  public static Matrix3x3 identity() {
    return new Matrix3x3(new double[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
  }

  /**
   * Obtiene el valor en la posición especificada.
   *
   * @param row fila (0-2)
   * @param col columna (0-2)
   * @return el valor en la posición
   */
  public double get(int row, int col) {
    if (row < 0 || row >= 3 || col < 0 || col >= 3) {
      throw new IndexOutOfBoundsException("Index out of bounds");
    }
    return data[row][col];
  }

  /**
   * Suma esta matriz con otra.
   *
   * @param other la otra matriz
   * @return una nueva matriz resultado de la suma
   */
  public Matrix3x3 add(Matrix3x3 other) {
    if (other == null) {
      throw new IllegalArgumentException("Matrix cannot be null");
    }
    double[][] result = new double[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        result[i][j] = this.data[i][j] + other.data[i][j];
      }
    }
    return new Matrix3x3(result);
  }

  /**
   * Multiplica esta matriz por otra.
   *
   * @param other la otra matriz
   * @return una nueva matriz resultado del producto
   */
  public Matrix3x3 multiply(Matrix3x3 other) {
    if (other == null) {
      throw new IllegalArgumentException("Matrix cannot be null");
    }
    double[][] result = new double[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        result[i][j] = 0;
        for (int k = 0; k < 3; k++) {
          result[i][j] += this.data[i][k] * other.data[k][j];
        }
      }
    }
    return new Matrix3x3(result);
  }

  /**
   * Multiplica esta matriz por un vector.
   *
   * @param vector el vector
   * @return un nuevo vector resultado del producto
   */
  public Vector3D multiply(Vector3D vector) {
    if (vector == null) {
      throw new IllegalArgumentException("Vector cannot be null");
    }
    double x =
        data[0][0] * vector.getX() + data[0][1] * vector.getY() + data[0][2] * vector.getZ();
    double y =
        data[1][0] * vector.getX() + data[1][1] * vector.getY() + data[1][2] * vector.getZ();
    double z =
        data[2][0] * vector.getX() + data[2][1] * vector.getY() + data[2][2] * vector.getZ();
    return new Vector3D(x, y, z);
  }

  /**
   * Crea una matriz de rotación alrededor del eje X.
   *
   * @param angle ángulo en radianes
   * @return matriz de rotación
   */
  public static Matrix3x3 rotationX(double angle) {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    return new Matrix3x3(new double[][] {{1, 0, 0}, {0, cos, -sin}, {0, sin, cos}});
  }

  /**
   * Crea una matriz de rotación alrededor del eje Y.
   *
   * @param angle ángulo en radianes
   * @return matriz de rotación
   */
  public static Matrix3x3 rotationY(double angle) {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    return new Matrix3x3(new double[][] {{cos, 0, sin}, {0, 1, 0}, {-sin, 0, cos}});
  }

  /**
   * Crea una matriz de rotación alrededor del eje Z.
   *
   * @param angle ángulo en radianes
   * @return matriz de rotación
   */
  public static Matrix3x3 rotationZ(double angle) {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    return new Matrix3x3(new double[][] {{cos, -sin, 0}, {sin, cos, 0}, {0, 0, 1}});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Matrix3x3 other = (Matrix3x3) obj;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (Double.compare(this.data[i][j], other.data[i][j]) != 0) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        result = 31 * result + Double.hashCode(data[i][j]);
      }
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Matrix3x3[\n");
    for (int i = 0; i < 3; i++) {
      sb.append("  [");
      for (int j = 0; j < 3; j++) {
        sb.append(String.format("%.4f", data[i][j]));
        if (j < 2) {
          sb.append(", ");
        }
      }
      sb.append("]\n");
    }
    sb.append("]");
    return sb.toString();
  }
}
