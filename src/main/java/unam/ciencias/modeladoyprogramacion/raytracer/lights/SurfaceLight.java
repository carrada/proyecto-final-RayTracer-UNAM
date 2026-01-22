package unam.ciencias.modeladoyprogramacion.raytracer.lights;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Representa una luz de área (superficie emisora de luz).
 *
 * <p>Una luz de superficie emite luz desde un área rectangular, creando sombras suaves mediante
 * muestreo Monte Carlo. A diferencia de las luces puntuales que producen sombras duras con bordes
 * definidos, las luces de área generan penumbras realistas.
 *
 * @author Cristopher Carrada
 */
public final class SurfaceLight extends Light {
  private final Vector3D position;
  private final Vector3D normal;
  private final Vector3D uAxis;
  private final Vector3D vAxis;
  private final double width;
  private final double height;
  private final int samples;
  private final Random random;

  /**
   * Construye una luz de superficie rectangular.
   *
   * @param color color de la luz
   * @param intensity intensidad de la luz
   * @param position posición central de la superficie
   * @param normal vector normal a la superficie (define la dirección de emisión)
   * @param width ancho de la superficie rectangular
   * @param height altura de la superficie rectangular
   * @param samples número de puntos de muestreo para sombras suaves (mínimo 1)
   */
  public SurfaceLight(
      Vector3D color,
      double intensity,
      Vector3D position,
      Vector3D normal,
      double width,
      double height,
      int samples) {
    super(color, intensity);
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (normal == null) {
      throw new IllegalArgumentException("Normal cannot be null");
    }
    if (width <= 0) {
      throw new IllegalArgumentException("Width must be positive");
    }
    if (height <= 0) {
      throw new IllegalArgumentException("Height must be positive");
    }
    if (samples < 1) {
      throw new IllegalArgumentException("Samples must be at least 1");
    }

    this.position = position;
    this.normal = normal.normalize();
    this.width = width;
    this.height = height;
    this.samples = samples;
    this.random = new Random(42); // Seed fijo para resultados reproducibles

    // Construir sistema de coordenadas ortogonal para la superficie
    // Elegir un vector arbitrario que no sea paralelo a la normal
    Vector3D arbitrary =
        Math.abs(this.normal.getY()) < 0.9
            ? new Vector3D(0, 1, 0)
            : new Vector3D(1, 0, 0);

    this.uAxis = this.normal.cross(arbitrary).normalize();
    this.vAxis = this.normal.cross(this.uAxis).normalize();
  }

  @Override
  public Vector3D getPosition() {
    return position;
  }

  /**
   * Obtiene el vector normal de la superficie.
   *
   * @return normal normalizado de la superficie
   */
  public Vector3D getNormal() {
    return normal;
  }

  /**
   * Obtiene el ancho de la superficie.
   *
   * @return ancho en unidades del mundo
   */
  public double getWidth() {
    return width;
  }

  /**
   * Obtiene la altura de la superficie.
   *
   * @return altura en unidades del mundo
   */
  public double getHeight() {
    return height;
  }

  /**
   * Obtiene el número de muestras para sombras suaves.
   *
   * @return número de puntos de muestreo
   */
  public int getSamples() {
    return samples;
  }

  /**
   * Genera puntos de muestreo distribuidos sobre la superficie rectangular.
   *
   * <p>Usa muestreo estratificado (jittered grid sampling) para mejor distribución que muestreo
   * puramente aleatorio.
   *
   * @return lista de puntos sobre la superficie
   */
  public List<Vector3D> getSamplePoints() {
    List<Vector3D> points = new ArrayList<>(samples);

    // Usar grid estratificado para mejor cobertura
    int sqrtSamples = (int) Math.ceil(Math.sqrt(samples));

    for (int i = 0; i < samples; i++) {
      // Coordenadas de grid
      int row = i / sqrtSamples;
      int col = i % sqrtSamples;

      // Offset aleatorio dentro de la celda del grid (jittering)
      double uOffset = (col + random.nextDouble()) / sqrtSamples;
      double vOffset = (row + random.nextDouble()) / sqrtSamples;

      // Mapear de [0,1] a [-width/2, width/2] y [-height/2, height/2]
      double u = (uOffset - 0.5) * width;
      double v = (vOffset - 0.5) * height;

      // Calcular posición en espacio 3D
      Vector3D offset = uAxis.multiply(u).add(vAxis.multiply(v));
      Vector3D samplePoint = position.add(offset);
      points.add(samplePoint);
    }

    return points;
  }

  @Override
  public Vector3D getDirectionFrom(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    // Retorna dirección hacia el centro de la superficie
    return position.subtract(point).normalize();
  }

  @Override
  public double getDistanceFrom(Vector3D point) {
    if (point == null) {
      throw new IllegalArgumentException("Point cannot be null");
    }
    // Retorna distancia al centro de la superficie
    return position.distance(point);
  }

  /**
   * Calcula la dirección desde un punto hacia un punto de muestra específico.
   *
   * @param point punto desde el cual calcular
   * @param samplePoint punto de muestra en la superficie
   * @return dirección normalizada hacia el punto de muestra
   */
  public Vector3D getDirectionFromToSample(Vector3D point, Vector3D samplePoint) {
    if (point == null || samplePoint == null) {
      throw new IllegalArgumentException("Points cannot be null");
    }
    return samplePoint.subtract(point).normalize();
  }

  /**
   * Calcula la distancia desde un punto hasta un punto de muestra específico.
   *
   * @param point punto desde el cual calcular
   * @param samplePoint punto de muestra en la superficie
   * @return distancia al punto de muestra
   */
  public double getDistanceFromToSample(Vector3D point, Vector3D samplePoint) {
    if (point == null || samplePoint == null) {
      throw new IllegalArgumentException("Points cannot be null");
    }
    return samplePoint.distance(point);
  }

  @Override
  public String toString() {
    return String.format(
        "SurfaceLight[position=%s, normal=%s, size=%.2fx%.2f, samples=%d, intensity=%.2f]",
        position, normal, width, height, samples, intensity);
  }
}
