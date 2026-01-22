package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

class IntersectionTest {

  @Test
  void constructor_createsValidIntersection() {
    Sphere sphere = new Sphere("s1", "m1", new Vector3D(0, 0, 0), 1.0);
    Intersection hit = new Intersection(5.0, new Vector3D(1, 0, 0), new Vector3D(1, 0, 0), sphere);
    assertEquals(5.0, hit.getDistance(), 1e-6);
    assertNotNull(hit.getPoint());
    assertNotNull(hit.getNormal());
    assertNotNull(hit.getPrimitive());
  }

  @Test
  void constructor_normalizesNormal() {
    Sphere sphere = new Sphere("s1", "m1", new Vector3D(0, 0, 0), 1.0);
    Intersection hit = new Intersection(5.0, new Vector3D(1, 0, 0), new Vector3D(3, 0, 0), sphere);
    assertEquals(1.0, hit.getNormal().magnitude(), 1e-6);
  }

  @Test
  void constructor_throwsOnNegativeDistance() {
    Sphere sphere = new Sphere("s1", "m1", new Vector3D(0, 0, 0), 1.0);
    assertThrows(IllegalArgumentException.class,
        () -> new Intersection(-1.0, new Vector3D(1, 0, 0), new Vector3D(1, 0, 0), sphere));
  }

  @Test
  void constructor_throwsOnNullPoint() {
    Sphere sphere = new Sphere("s1", "m1", new Vector3D(0, 0, 0), 1.0);
    assertThrows(IllegalArgumentException.class,
        () -> new Intersection(5.0, null, new Vector3D(1, 0, 0), sphere));
  }

  @Test
  void constructor_throwsOnNullNormal() {
    Sphere sphere = new Sphere("s1", "m1", new Vector3D(0, 0, 0), 1.0);
    assertThrows(IllegalArgumentException.class,
        () -> new Intersection(5.0, new Vector3D(1, 0, 0), null, sphere));
  }

  @Test
  void constructor_throwsOnNullPrimitive() {
    assertThrows(IllegalArgumentException.class,
        () -> new Intersection(5.0, new Vector3D(1, 0, 0), new Vector3D(1, 0, 0), null));
  }

  @Test
  void equals_returnsFalseForNull() {
    Sphere sphere = new Sphere("s1", "m1", new Vector3D(0, 0, 0), 1.0);
    Intersection hit = new Intersection(5.0, new Vector3D(1, 0, 0), new Vector3D(1, 0, 0), sphere);
    assertNotEquals(hit, null);
  }

  @Test
  void equals_returnsFalseForDifferentClass() {
    Sphere sphere = new Sphere("s1", "m1", new Vector3D(0, 0, 0), 1.0);
    Intersection hit = new Intersection(5.0, new Vector3D(1, 0, 0), new Vector3D(1, 0, 0), sphere);
    assertNotEquals(hit, "not an intersection");
  }
}

