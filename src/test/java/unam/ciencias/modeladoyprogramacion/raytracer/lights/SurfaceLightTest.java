package unam.ciencias.modeladoyprogramacion.raytracer.lights;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Tests para la clase SurfaceLight.
 *
 * @author Cristopher Carrada
 */
class SurfaceLightTest {

  @Test
  void constructor_createsValidSurfaceLight() {
    Vector3D color = new Vector3D(1.0, 0.9, 0.8);
    Vector3D position = new Vector3D(0, 5, 0);
    Vector3D normal = new Vector3D(0, -1, 0);

    SurfaceLight light = new SurfaceLight(color, 1.0, position, normal, 2.0, 2.0, 16);

    assertNotNull(light);
    assertEquals(color, light.getColor());
    assertEquals(1.0, light.getIntensity());
    assertEquals(position, light.getPosition());
    assertEquals(2.0, light.getWidth());
    assertEquals(2.0, light.getHeight());
    assertEquals(16, light.getSamples());
  }

  @Test
  void constructor_normalizesNormal() {
    Vector3D unnormalized = new Vector3D(0, -2, 0);
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 0), unnormalized, 2.0, 2.0, 4);

    Vector3D normal = light.getNormal();
    assertEquals(1.0, normal.magnitude(), 1e-10);
  }

  @Test
  void constructor_throwsOnNullColor() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SurfaceLight(
                null, 1.0, new Vector3D(0, 5, 0), new Vector3D(0, -1, 0), 2.0, 2.0, 4));
  }

  @Test
  void constructor_throwsOnNullPosition() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SurfaceLight(
                new Vector3D(1, 1, 1), 1.0, null, new Vector3D(0, -1, 0), 2.0, 2.0, 4));
  }

  @Test
  void constructor_throwsOnNullNormal() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SurfaceLight(
                new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 0), null, 2.0, 2.0, 4));
  }

  @Test
  void constructor_throwsOnNegativeIntensity() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SurfaceLight(
                new Vector3D(1, 1, 1), -0.5, new Vector3D(0, 5, 0), new Vector3D(0, -1, 0), 2.0, 2.0, 4));
  }

  @Test
  void constructor_throwsOnZeroWidth() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SurfaceLight(
                new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 0), new Vector3D(0, -1, 0), 0.0, 2.0, 4));
  }

  @Test
  void constructor_throwsOnNegativeHeight() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SurfaceLight(
                new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 0), new Vector3D(0, -1, 0), 2.0, -1.0, 4));
  }

  @Test
  void constructor_throwsOnZeroSamples() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SurfaceLight(
                new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 5, 0), new Vector3D(0, -1, 0), 2.0, 2.0, 0));
  }

  @Test
  void getDirectionFrom_pointsTowardCenter() {
    Vector3D position = new Vector3D(0, 10, 0);
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1), 1.0, position, new Vector3D(0, -1, 0), 2.0, 2.0, 4);

    Vector3D point = new Vector3D(5, 0, 0);
    Vector3D direction = light.getDirectionFrom(point);

    // Dirección debe estar normalizada
    assertEquals(1.0, direction.magnitude(), 1e-10);

    // Dirección debe apuntar hacia la posición
    Vector3D expected = position.subtract(point).normalize();
    assertEquals(expected.getX(), direction.getX(), 1e-10);
    assertEquals(expected.getY(), direction.getY(), 1e-10);
    assertEquals(expected.getZ(), direction.getZ(), 1e-10);
  }

  @Test
  void getDirectionFrom_throwsOnNullPoint() {
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.0,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            2.0,
            2.0,
            4);

    assertThrows(IllegalArgumentException.class, () -> light.getDirectionFrom(null));
  }

  @Test
  void getDistanceFrom_returnsCorrectDistance() {
    Vector3D position = new Vector3D(0, 10, 0);
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1), 1.0, position, new Vector3D(0, -1, 0), 2.0, 2.0, 4);

    Vector3D point = new Vector3D(3, 6, 4);
    double distance = light.getDistanceFrom(point);

    double expected = position.distance(point);
    assertEquals(expected, distance, 1e-10);
  }

  @Test
  void getDistanceFrom_throwsOnNullPoint() {
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.0,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            2.0,
            2.0,
            4);

    assertThrows(IllegalArgumentException.class, () -> light.getDistanceFrom(null));
  }

  @Test
  void getSamplePoints_returnsCorrectNumberOfSamples() {
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.0,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            4.0,
            4.0,
            16);

    List<Vector3D> samples = light.getSamplePoints();
    assertEquals(16, samples.size());
  }

  @Test
  void getSamplePoints_distributesSamplesWithinBounds() {
    Vector3D position = new Vector3D(0, 10, 0);
    double width = 4.0;
    double height = 4.0;

    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1), 1.0, position, new Vector3D(0, -1, 0), width, height, 25);

    List<Vector3D> samples = light.getSamplePoints();

    // Verificar que todos los puntos están dentro de los límites razonables
    for (Vector3D sample : samples) {
      Vector3D offset = sample.subtract(position);
      double distance = offset.magnitude();

      // La distancia máxima desde el centro es la diagonal del rectángulo / 2
      double maxDistance = Math.sqrt(width * width + height * height) / 2;
      assertTrue(distance <= maxDistance + 0.1, "Sample point outside bounds: " + sample);
    }
  }

  @Test
  void getSamplePoints_isConsistent() {
    // Con seed fijo, las muestras deben ser reproducibles
    SurfaceLight light1 =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.0,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            2.0,
            2.0,
            9);

    SurfaceLight light2 =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.0,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            2.0,
            2.0,
            9);

    List<Vector3D> samples1 = light1.getSamplePoints();
    List<Vector3D> samples2 = light2.getSamplePoints();

    assertEquals(samples1.size(), samples2.size());
    for (int i = 0; i < samples1.size(); i++) {
      assertEquals(samples1.get(i).getX(), samples2.get(i).getX(), 1e-10);
      assertEquals(samples1.get(i).getY(), samples2.get(i).getY(), 1e-10);
      assertEquals(samples1.get(i).getZ(), samples2.get(i).getZ(), 1e-10);
    }
  }

  @Test
  void getDirectionFromToSample_calculatesCorrectDirection() {
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.0,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            2.0,
            2.0,
            4);

    Vector3D point = new Vector3D(5, 0, 0);
    Vector3D samplePoint = new Vector3D(1, 10, 1);

    Vector3D direction = light.getDirectionFromToSample(point, samplePoint);

    assertEquals(1.0, direction.magnitude(), 1e-10);

    Vector3D expected = samplePoint.subtract(point).normalize();
    assertEquals(expected.getX(), direction.getX(), 1e-10);
    assertEquals(expected.getY(), direction.getY(), 1e-10);
    assertEquals(expected.getZ(), direction.getZ(), 1e-10);
  }

  @Test
  void getDistanceFromToSample_calculatesCorrectDistance() {
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.0,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            2.0,
            2.0,
            4);

    Vector3D point = new Vector3D(0, 0, 0);
    Vector3D samplePoint = new Vector3D(3, 4, 0);

    double distance = light.getDistanceFromToSample(point, samplePoint);
    assertEquals(5.0, distance, 1e-10);
  }

  @Test
  void toString_returnsReadableString() {
    SurfaceLight light =
        new SurfaceLight(
            new Vector3D(1, 1, 1),
            1.5,
            new Vector3D(0, 10, 0),
            new Vector3D(0, -1, 0),
            3.0,
            2.0,
            16);

    String str = light.toString();
    assertTrue(str.contains("SurfaceLight"));
    assertTrue(str.contains("3.00x2.00"));
    assertTrue(str.contains("samples=16"));
  }
}
