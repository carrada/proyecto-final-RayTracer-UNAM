package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;

class PointLightTest {

  @Test
  void constructor_createsValidPointLight() {
    PointLight light =
        new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 10, 0));
    assertEquals(1.0, light.getColor().getX(), 1e-6);
    assertEquals(1.0, light.getIntensity(), 1e-6);
    assertEquals(10.0, light.getPosition().getY(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullColor() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PointLight(null, 1.0, new Vector3D(0, 10, 0)));
  }

  @Test
  void constructor_throwsOnNegativeIntensity() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PointLight(new Vector3D(1, 1, 1), -0.5, new Vector3D(0, 10, 0)));
  }

  @Test
  void constructor_throwsOnNullPosition() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new PointLight(new Vector3D(1, 1, 1), 1.0, null));
  }

  @Test
  void getDirectionFrom_returnsNormalizedDirection() {
    PointLight light =
        new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 10, 0));
    Vector3D direction = light.getDirectionFrom(new Vector3D(0, 0, 0));
    assertEquals(1.0, direction.magnitude(), 1e-6);
    assertTrue(direction.getY() > 0);
  }

  @Test
  void getDirectionFrom_throwsOnNullPoint() {
    PointLight light =
        new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 10, 0));
    assertThrows(IllegalArgumentException.class, () -> light.getDirectionFrom(null));
  }

  @Test
  void getDistanceFrom_returnsCorrectDistance() {
    PointLight light =
        new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 10, 0));
    double distance = light.getDistanceFrom(new Vector3D(0, 0, 0));
    assertEquals(10.0, distance, 1e-6);
  }

  @Test
  void getDistanceFrom_throwsOnNullPoint() {
    PointLight light =
        new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(0, 10, 0));
    assertThrows(IllegalArgumentException.class, () -> light.getDistanceFrom(null));
  }

  @Test
  void getDistanceFrom_handlesArbitraryPoints() {
    PointLight light =
        new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(3, 4, 0));
    double distance = light.getDistanceFrom(new Vector3D(0, 0, 0));
    assertEquals(5.0, distance, 1e-6);
  }
}
