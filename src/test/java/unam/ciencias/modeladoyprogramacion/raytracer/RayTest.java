package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RayTest {

  @Test
  void constructor_createsValidRay() {
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    assertEquals(0.0, ray.getOrigin().getX(), 1e-6);
    assertEquals(1.0, ray.getDirection().magnitude(), 1e-6);
  }

  @Test
  void constructor_normalizesDirection() {
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(3, 0, 0));
    assertEquals(1.0, ray.getDirection().magnitude(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullOrigin() {
    assertThrows(IllegalArgumentException.class, 
        () -> new Ray(null, new Vector3D(1, 0, 0)));
  }

  @Test
  void constructor_throwsOnNullDirection() {
    assertThrows(IllegalArgumentException.class, 
        () -> new Ray(new Vector3D(0, 0, 0), null));
  }

  @Test
  void at_calculatesPointAlongRay() {
    Ray ray = new Ray(new Vector3D(1, 2, 3), new Vector3D(1, 0, 0));
    Vector3D point = ray.at(5.0);
    assertEquals(6.0, point.getX(), 1e-6);
    assertEquals(2.0, point.getY(), 1e-6);
    assertEquals(3.0, point.getZ(), 1e-6);
  }

  @Test
  void equals_returnsTrueForIdenticalRays() {
    Ray ray1 = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    Ray ray2 = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    assertEquals(ray1, ray2);
  }

  @Test
  void equals_returnsFalseForDifferentRays() {
    Ray ray1 = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    Ray ray2 = new Ray(new Vector3D(1, 1, 1), new Vector3D(1, 0, 0));
    assertNotEquals(ray1, ray2);
  }

  @Test
  void hashCode_isConsistent() {
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    int hash1 = ray.hashCode();
    int hash2 = ray.hashCode();
    assertEquals(hash1, hash2);
  }

  @Test
  void equals_returnsFalseForNull() {
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    assertNotEquals(ray, null);
  }

  @Test
  void equals_returnsFalseForDifferentClass() {
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    assertNotEquals(ray, "not a ray");
  }

  @Test
  void toString_returnsFormattedString() {
    Ray ray = new Ray(new Vector3D(1, 2, 3), new Vector3D(1, 0, 0));
    String result = ray.toString();
    assertNotNull(result);
    assertTrue(result.contains("Ray") || result.contains("origin") || result.contains("direction"));
  }
}
