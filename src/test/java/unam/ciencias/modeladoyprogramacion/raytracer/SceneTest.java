package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Primitive;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

class SceneTest {
  private Camera camera;
  private Material material;

  @BeforeEach
  void setup() {
    camera =
        new Camera(
            new Vector3D(0, 0, 5),
            new Vector3D(0, 0, -1),
            new Vector3D(0, 1, 0),
            60.0,
            1.0);
    material = new Material("mat1", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.2);
  }

  @Test
  void builder_createsSceneWithDefaults() {
    Scene scene = new Scene.Builder().camera(camera).build();
    assertEquals(800, scene.getImageWidth());
    assertEquals(600, scene.getImageHeight());
    assertEquals(1, scene.getSamplesPerPixel());
    assertEquals(3, scene.getMaxBounces());
  }

  @Test
  void builder_throwsWhenCameraMissing() {
    assertThrows(IllegalStateException.class, () -> new Scene.Builder().build());
  }

  @Test
  void builder_setsCustomImageSize() {
    Scene scene = new Scene.Builder().camera(camera).imageSize(1024, 768).build();
    assertEquals(1024, scene.getImageWidth());
    assertEquals(768, scene.getImageHeight());
  }

  @Test
  void builder_addsPrimitives() {
    Sphere sphere = new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0);
    Scene scene = new Scene.Builder().camera(camera).addPrimitive(sphere).build();
    assertEquals(1, scene.getPrimitives().size());
  }

  @Test
  void builder_addsLights() {
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));
    Scene scene = new Scene.Builder().camera(camera).addLight(light).build();
    assertEquals(1, scene.getLights().size());
  }

  @Test
  void builder_addsMaterials() {
    Scene scene = new Scene.Builder().camera(camera).addMaterial(material).build();
    assertTrue(scene.getMaterial("mat1").isPresent());
  }

  @Test
  void getMaterial_returnsEmptyForUnknownId() {
    Scene scene = new Scene.Builder().camera(camera).build();
    assertFalse(scene.getMaterial("unknown").isPresent());
  }

  @Test
  void intersect_findsClosestPrimitive() {
    Sphere sphere1 = new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0);
    Sphere sphere2 = new Sphere("sphere2", "mat1", new Vector3D(0, 0, -5), 1.0);
    Scene scene =
        new Scene.Builder()
            .camera(camera)
            .addPrimitive(sphere1)
            .addPrimitive(sphere2)
            .build();

    Ray ray = new Ray(new Vector3D(0, 0, 5), new Vector3D(0, 0, -1));
    Optional<Intersection> hit = scene.intersect(ray);
    assertTrue(hit.isPresent());
    assertTrue(hit.get().getDistance() < 6.0);
  }

  @Test
  void intersect_returnsEmptyWhenNoHit() {
    Sphere sphere = new Sphere("sphere1", "mat1", new Vector3D(10, 10, 10), 1.0);
    Scene scene = new Scene.Builder().camera(camera).addPrimitive(sphere).build();

    Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, -1));
    Optional<Intersection> hit = scene.intersect(ray);
    assertFalse(hit.isPresent());
  }

  @Test
  void getPrimitives_returnsDefensiveCopy() {
    Scene scene = new Scene.Builder().camera(camera).build();
    List<Primitive> primitives = scene.getPrimitives();
    // List.copyOf returns immutable list, attempting to modify should throw
    assertThrows(UnsupportedOperationException.class, () -> {
      primitives.add(new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0));
    });
  }
}
