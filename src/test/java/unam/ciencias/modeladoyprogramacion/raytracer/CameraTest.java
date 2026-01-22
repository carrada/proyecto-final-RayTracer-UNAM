package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CameraTest {

  @Test
  void constructor_createsValidCamera() {
    Camera cam =
        new Camera(
            new Vector3D(0, 0, 0),
            new Vector3D(0, 0, -1),
            new Vector3D(0, 1, 0),
            60.0,
            1.0);
    assertEquals(0.0, cam.getPosition().getZ(), 1e-6);
    assertEquals(60.0, cam.getFov(), 1e-6);
    assertEquals(1.0, cam.getFocalDistance(), 1e-6);
  }

  @Test
  void constructor_normalizesDirection() {
    Camera cam =
        new Camera(
            new Vector3D(0, 0, 0),
            new Vector3D(0, 0, -5),
            new Vector3D(0, 1, 0),
            60.0,
            1.0);
    assertEquals(1.0, cam.getDirection().magnitude(), 1e-6);
  }

  @Test
  void constructor_normalizesUp() {
    Camera cam =
        new Camera(
            new Vector3D(0, 0, 0),
            new Vector3D(0, 0, -1),
            new Vector3D(0, 3, 0),
            60.0,
            1.0);
    assertEquals(1.0, cam.getUp().magnitude(), 1e-6);
  }

  @Test
  void constructor_throwsOnNullPosition() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Camera(
                null, new Vector3D(0, 0, -1), new Vector3D(0, 1, 0), 60.0, 1.0));
  }

  @Test
  void constructor_throwsOnNullDirection() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Camera(
                new Vector3D(0, 0, 0), null, new Vector3D(0, 1, 0), 60.0, 1.0));
  }

  @Test
  void constructor_throwsOnNullUp() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Camera(
                new Vector3D(0, 0, 0), new Vector3D(0, 0, -1), null, 60.0, 1.0));
  }

  @Test
  void constructor_throwsOnInvalidFov() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Camera(
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, -1),
                new Vector3D(0, 1, 0),
                0.0,
                1.0));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Camera(
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, -1),
                new Vector3D(0, 1, 0),
                180.0,
                1.0));
  }

  @Test
  void constructor_throwsOnNegativeFocalDistance() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Camera(
                new Vector3D(0, 0, 0),
                new Vector3D(0, 0, -1),
                new Vector3D(0, 1, 0),
                60.0,
                -1.0));
  }

  @Test
  void getRight_returnsPerpendicularVector() {
    Camera cam =
        new Camera(
            new Vector3D(0, 0, 0),
            new Vector3D(0, 0, -1),
            new Vector3D(0, 1, 0),
            60.0,
            1.0);
    Vector3D right = cam.getRight();
    assertEquals(1.0, right.magnitude(), 1e-6);
    assertEquals(0.0, right.dot(cam.getDirection()), 1e-6);
    assertEquals(0.0, right.dot(cam.getUp()), 1e-6);
  }
}
