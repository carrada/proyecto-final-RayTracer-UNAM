package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Box;

class BoxTest {

  @Test
  void constructor_createsValidBox() {
    Box box = new Box("test", "mat1", new Vector3D(-1, -1, -1), 2.0, 2.0, 2.0);
    assertEquals(-1.0, box.getMin().getX(), 1e-6);
    assertEquals(-1.0, box.getMin().getY(), 1e-6);
    assertEquals(-1.0, box.getMin().getZ(), 1e-6);
    assertEquals(1.0, box.getMax().getX(), 1e-6);
    assertEquals(1.0, box.getMax().getY(), 1e-6);
    assertEquals(1.0, box.getMax().getZ(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullMin() {
    assertThrows(IllegalArgumentException.class, () -> new Box("test", "mat1", null, 2.0, 2.0, 2.0));
  }

  @Test
  void constructor_throwsOnZeroWidth() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Box("test", "mat1", new Vector3D(0, 0, 0), 0.0, 2.0, 2.0));
  }

  @Test
  void constructor_throwsOnNegativeHeight() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Box("test", "mat1", new Vector3D(0, 0, 0), 2.0, -1.0, 2.0));
  }

  @Test
  void constructor_throwsOnZeroDepth() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Box("test", "mat1", new Vector3D(0, 0, 0), 2.0, 2.0, 0.0));
  }

  @Test
  void intersect_returnsDistanceWhenRayHits() {
    Box box = new Box("test", "mat1", new Vector3D(-1, -1, -11), 2.0, 2.0, 2.0);
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, -1));
    Optional<Double> t = box.intersect(ray);
    assertTrue(t.isPresent());
    assertTrue(t.get() > 0);
  }

  @Test
  void intersect_returnsEmptyWhenRayMisses() {
    Box box = new Box("test", "mat1", new Vector3D(-1, -1, -11), 2.0, 2.0, 2.0);
    Ray ray = new Ray(new Vector3D(10, 10, 0), new Vector3D(0, 0, -1));
    assertFalse(box.intersect(ray).isPresent());
  }

  @Test
  void intersect_returnsEmptyWhenBoxBehindRay() {
    Box box = new Box("test", "mat1", new Vector3D(-1, -1, 10), 2.0, 2.0, 2.0);
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, -1));
    assertFalse(box.intersect(ray).isPresent());
  }

  @Test
  void intersect_handlesRayOriginInsideBox() {
    Box box = new Box("test", "mat1", new Vector3D(-5, -5, -5), 10.0, 10.0, 10.0);
    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
    assertTrue(box.intersect(ray).isPresent());
  }

  @Test
  void intersect_worksForAxisAlignedRays() {
    Box box = new Box("test", "mat1", new Vector3D(0, 0, 0), 5.0, 5.0, 5.0);
    Ray rayX = new Ray(new Vector3D(-10, 2.5, 2.5), new Vector3D(1, 0, 0));
    assertTrue(box.intersect(rayX).isPresent());
    Ray rayY = new Ray(new Vector3D(2.5, -10, 2.5), new Vector3D(0, 1, 0));
    assertTrue(box.intersect(rayY).isPresent());
    Ray rayZ = new Ray(new Vector3D(2.5, 2.5, -10), new Vector3D(0, 0, 1));
    assertTrue(box.intersect(rayZ).isPresent());
  }

  @Test
  void intersect_worksForDiagonalRay() {
    Box box = new Box("test", "mat1", new Vector3D(-1, -1, -1), 2.0, 2.0, 2.0);
    Vector3D direction = new Vector3D(1, 1, 1).normalize();
    Ray ray = new Ray(new Vector3D(-5, -5, -5), direction);
    assertTrue(box.intersect(ray).isPresent());
  }

  @Test
  void getNormalAt_returnsCorrectNormalForFace() {
    Box box = new Box("test", "mat1", new Vector3D(-1, -1, -1), 2.0, 2.0, 2.0);
    Vector3D normalFront = box.getNormalAt(new Vector3D(0, 0, 1));
    assertEquals(1.0, Math.abs(normalFront.getZ()), 1e-6);
    Vector3D normalRight = box.getNormalAt(new Vector3D(1, 0, 0));
    assertEquals(1.0, Math.abs(normalRight.getX()), 1e-6);
  }

  @Test
  void getNormalAt_returnsNormalizedVector() {
    Box box = new Box("test", "mat1", new Vector3D(0, 0, 0), 10.0, 10.0, 10.0);
    Vector3D normal = box.getNormalAt(new Vector3D(10, 5, 5));
    assertEquals(1.0, normal.magnitude(), 1e-6);
  }

  @Test
  void getNormalAt_throwsOnNullPoint() {
    Box box = new Box("test", "mat1", new Vector3D(0, 0, 0), 2.0, 2.0, 2.0);
    assertThrows(IllegalArgumentException.class, () -> box.getNormalAt(null));
  }
}
