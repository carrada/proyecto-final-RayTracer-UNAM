package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Plane;

class PlaneTest {

  @Test
  void constructor_createsValidPlane() {
    Plane plane = new Plane("test", "mat1", new Vector3D(0, 0, 0), new Vector3D(0, 1, 0));
    assertEquals(1.0, plane.getNormal().magnitude(), 1e-6);
  }

  @Test
  void constructor_normalizesNormalVector() {
    Plane plane = new Plane("test", "mat1", new Vector3D(0, 0, 0), new Vector3D(0, 5, 0));
    Vector3D normal = plane.getNormal();
    assertEquals(1.0, normal.magnitude(), 1e-6);
    assertEquals(0.0, normal.getX(), 1e-6);
    assertEquals(1.0, normal.getY(), 1e-6);
    assertEquals(0.0, normal.getZ(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullPoint() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Plane("test", "mat1", null, new Vector3D(0, 1, 0)));
  }

  @Test
  void constructor_throwsOnNullNormal() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Plane("test", "mat1", new Vector3D(0, 0, 0), null));
  }

  @Test
  void intersect_returnsDistanceWhenRayHits() {
    Plane plane = new Plane("test", "mat1", new Vector3D(0, 0, -10), new Vector3D(0, 0, 1));
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, -1));
    Optional<Double> t = plane.intersect(ray);
    assertTrue(t.isPresent());
    assertEquals(10.0, t.get(), 1e-6);
  }

  @Test
  void intersect_returnsEmptyWhenRayParallel() {
    Plane plane = new Plane("test", "mat1", new Vector3D(0, 0, 0), new Vector3D(0, 1, 0));
    Ray ray = new Ray(new Vector3D(0, 5, 0), new Vector3D(1, 0, 0));
    assertFalse(plane.intersect(ray).isPresent());
  }

  @Test
  void intersect_returnsEmptyWhenIntersectionBehindRay() {
    Plane plane = new Plane("test", "mat1", new Vector3D(0, 0, -10), new Vector3D(0, 0, 1));
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, 1));
    assertFalse(plane.intersect(ray).isPresent());
  }

  @Test
  void intersect_worksForAngledRay() {
    Plane plane = new Plane("test", "mat1", new Vector3D(0, 5, 0), new Vector3D(0, 1, 0));
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 1, 0));
    Optional<Double> t = plane.intersect(ray);
    assertTrue(t.isPresent());
    assertEquals(5.0, t.get(), 1e-6);
  }

  @Test
  void getNormalAt_returnsConstantNormal() {
    Plane plane = new Plane("test", "mat1", new Vector3D(0, 0, 0), new Vector3D(0, 1, 0));
    Vector3D n1 = plane.getNormalAt(new Vector3D(0, 0, 0));
    Vector3D n2 = plane.getNormalAt(new Vector3D(100, 50, -30));
    assertEquals(n1.getX(), n2.getX(), 1e-6);
    assertEquals(n1.getY(), n2.getY(), 1e-6);
    assertEquals(n1.getZ(), n2.getZ(), 1e-6);
    assertEquals(1.0, n1.magnitude(), 1e-6);
  }
}
