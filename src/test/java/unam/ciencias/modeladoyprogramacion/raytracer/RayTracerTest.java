package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;
import unam.ciencias.modeladoyprogramacion.raytracer.observers.RenderProgressListener;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;

@ExtendWith(MockitoExtension.class)
class RayTracerTest {
  private Scene scene;

  @Mock private RenderProgressListener mockListener;

  @BeforeEach
  void setup() {
    Camera camera =
        new Camera(
            new Vector3D(0, 0, 5),
            new Vector3D(0, 0, -1),
            new Vector3D(0, 1, 0),
            60.0,
            1.0);
    Material material = new Material("mat1", new Vector3D(1, 0, 0), 0.8, 0.5, 32.0, 0.0);
    Sphere sphere = new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0);
    PointLight light = new PointLight(new Vector3D(1, 1, 1), 1.0, new Vector3D(5, 5, 5));

    scene =
        new Scene.Builder()
            .camera(camera)
            .addMaterial(material)
            .addPrimitive(sphere)
            .addLight(light)
            .imageSize(100, 100)
            .samplesPerPixel(1)
            .build();
  }

  @Test
  void constructor_throwsOnNullScene() {
    assertThrows(IllegalArgumentException.class, () -> new RayTracer(null));
  }

  @Test
  void constructor_acceptsValidScene() {
    RayTracer tracer = new RayTracer(scene);
    assertNotNull(tracer);
  }

  @Test
  void constructor_acceptsThreadCount() {
    RayTracer tracer = new RayTracer(scene, 4);
    assertNotNull(tracer);
  }

  @Test
  void render_producesImageWithCorrectDimensions() {
    RayTracer tracer = new RayTracer(scene);
    Image result = tracer.render();
    assertEquals(100, result.getRows());
    assertEquals(100, result.getColumns());
  }

  // ==================== TESTS CON MOCKITO ====================

  @Test
  void addProgressListener_withValidListener_addsSuccessfully() {
    RayTracer tracer = new RayTracer(scene);
    tracer.addProgressListener(mockListener);

    // El listener debe recibir notificaciones al renderizar
    tracer.render();

    verify(mockListener, times(1)).onRenderStart(anyInt());
    verify(mockListener, times(1)).onRenderComplete();
  }

  @Test
  void addProgressListener_withNullListener_doesNotThrow() {
    RayTracer tracer = new RayTracer(scene);

    // No debe lanzar excepción
    assertDoesNotThrow(() -> tracer.addProgressListener(null));
  }

  @Test
  void render_notifiesProgressListener_onStart() {
    RayTracer tracer = new RayTracer(scene);
    tracer.addProgressListener(mockListener);

    tracer.render();

    verify(mockListener, times(1)).onRenderStart(10000); // 100x100 = 10,000 píxeles
  }

  @Test
  void render_notifiesProgressListener_onComplete() {
    RayTracer tracer = new RayTracer(scene);
    tracer.addProgressListener(mockListener);

    tracer.render();

    verify(mockListener, times(1)).onRenderComplete();
  }

  @Test
  void render_notifiesProgressListener_duringRendering() {
    RayTracer tracer = new RayTracer(scene);
    tracer.addProgressListener(mockListener);

    tracer.render();

    // Debe notificar progreso al menos una vez
    verify(mockListener, atLeastOnce()).onProgressUpdate(anyInt(), eq(10000));
  }

  @Test
  void removeProgressListener_removesListener() {
    RayTracer tracer = new RayTracer(scene);
    tracer.addProgressListener(mockListener);
    tracer.removeProgressListener(mockListener);

    tracer.render();

    // El listener NO debe recibir notificaciones
    verify(mockListener, never()).onRenderStart(anyInt());
    verify(mockListener, never()).onRenderComplete();
  }

  @Test
  void render_withMultipleThreads_producesCorrectImage() {
    RayTracer tracer = new RayTracer(scene, 4);
    Image result = tracer.render();

    assertEquals(100, result.getRows());
    assertEquals(100, result.getColumns());
  }

  @Test
  void render_withMultipleListeners_notifiesAll() {
    RenderProgressListener listener1 = mock(RenderProgressListener.class);
    RenderProgressListener listener2 = mock(RenderProgressListener.class);

    RayTracer tracer = new RayTracer(scene);
    tracer.addProgressListener(listener1);
    tracer.addProgressListener(listener2);

    tracer.render();

    verify(listener1, times(1)).onRenderStart(anyInt());
    verify(listener1, times(1)).onRenderComplete();
    verify(listener2, times(1)).onRenderStart(anyInt());
    verify(listener2, times(1)).onRenderComplete();
  }
}
