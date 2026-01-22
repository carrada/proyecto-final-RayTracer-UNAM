package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

class SphereTest {

  @Test
  void constructor_createsValidSphere() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, 0), 5.0);
    assertEquals(0.0, sphere.getCenter().getX(), 1e-6);
    assertEquals(0.0, sphere.getCenter().getY(), 1e-6);
    assertEquals(0.0, sphere.getCenter().getZ(), 1e-6);
    assertEquals(5.0, sphere.getRadius(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullCenter() {
    assertThrows(IllegalArgumentException.class, () -> new Sphere("test", "mat1", null, 5.0));
  }

  @Test
  void constructor_throwsOnZeroRadius() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Sphere("test", "mat1", new Vector3D(0, 0, 0), 0.0));
  }

  @Test
  void constructor_throwsOnNegativeRadius() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Sphere("test", "mat1", new Vector3D(0, 0, 0), -1.0));
  }

  @Test
  void intersect_returnsDistanceWhenRayHits() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, -10), 2.0);
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, -1));
    Optional<Double> t = sphere.intersect(ray);
    assertTrue(t.isPresent());
    assertEquals(8.0, t.get(), 1e-6);
  }

  @Test
  void intersect_returnsEmptyWhenRayMisses() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, -10), 2.0);
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    assertFalse(sphere.intersect(ray).isPresent());
  }

  @Test
  void intersect_returnsEmptyWhenSphereBehindRay() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, 10), 2.0);
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, -1));
    assertFalse(sphere.intersect(ray).isPresent());
  }

  @Test
  void intersect_handlesRayOriginInsideSphere() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, 0), 5.0);
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, 1));
    Optional<Double> t = sphere.intersect(ray);
    assertTrue(t.isPresent());
    assertEquals(5.0, t.get(), 1e-6);
  }

  @Test
  void getNormalAt_returnsNormalizedVectorPointingOutward() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, 0), 5.0);
    Vector3D normal = sphere.getNormalAt(new Vector3D(5, 0, 0));
    assertEquals(1.0, normal.getX(), 1e-6);
    assertEquals(0.0, normal.getY(), 1e-6);
    assertEquals(0.0, normal.getZ(), 1e-6);
    assertEquals(1.0, normal.magnitude(), 1e-6);
  }

  @Test
  void getNormalAt_worksForArbitraryPoint() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, 0), 10.0);
    Vector3D normal = sphere.getNormalAt(new Vector3D(3, 4, 0));
    assertEquals(1.0, normal.magnitude(), 1e-6);
    assertTrue(normal.getX() > 0);
    assertTrue(normal.getY() > 0);
  }

  @Test
  void getNormalAt_throwsOnNullPoint() {
    Sphere sphere = new Sphere("test", "mat1", new Vector3D(0, 0, 0), 5.0);
    assertThrows(IllegalArgumentException.class, () -> sphere.getNormalAt(null));
  }
}
