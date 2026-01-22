package unam.ciencias.modeladoyprogramacion.raytracer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Tests para SceneLoader.
 *
 * @author Cristopher Carrada
 */
class SceneLoaderTest {

  @Test
  void loadFromString_parsesValidScene() throws IOException {
    String json =
        """
        {
          "imageWidth": 400,
          "imageHeight": 300,
          "camera": {
            "position": [0, 0, 5],
            "direction": [0, 0, -1],
            "up": [0, 1, 0],
            "fov": 60
          },
          "materials": [
            {
              "id": "mat1",
              "color": [1, 0, 0],
              "diffuse": 0.8,
              "specular": 0.5,
              "specularHardness": 32,
              "reflectivity": 0.2
            }
          ],
          "primitives": [
            {
              "type": "sphere",
              "center": [0, 0, 0],
              "radius": 1,
              "materialId": "mat1"
            }
          ],
          "lights": [
            {
              "type": "point",
              "position": [5, 5, 5],
              "color": [1, 1, 1],
              "intensity": 1.0
            }
          ],
          "ambientLight": [0.1, 0.1, 0.1]
        }
        """;

    SceneLoader loader = new SceneLoader();
    Scene scene = loader.loadFromString(json);

    assertNotNull(scene);
    assertEquals(400, scene.getImageWidth());
    assertEquals(300, scene.getImageHeight());
    assertEquals(1, scene.getPrimitives().size());
    assertEquals(1, scene.getLights().size());
  }

  @Test
  void loadFromString_throwsOnInvalidJson() {
    SceneLoader loader = new SceneLoader();
    assertThrows(IOException.class, () -> loader.loadFromString("{invalid json"));
  }

  @Test
  void loadFromFile_throwsOnNonexistentFile() {
    SceneLoader loader = new SceneLoader();
    assertThrows(IOException.class, () -> loader.loadFromFile("nonexistent.json"));
  }

  @Test
  void loadFromFile_parsesValidFile() throws IOException {
    SceneLoader loader = new SceneLoader();
    Scene scene = loader.loadFromFile("examples/ray_tracer/simple_scene.json");
    assertNotNull(scene);
    assertTrue(scene.getPrimitives().size() > 0);
  }

  @Test
  void loadFromString_parsesPlane() throws IOException {
    String json =
        """
        {
          "camera": {
            "position": [0, 0, 5],
            "direction": [0, 0, -1],
            "up": [0, 1, 0],
            "fov": 60
          },
          "materials": [
            {
              "id": "mat1",
              "color": [0.5, 0.5, 0.5],
              "diffuse": 0.8,
              "specular": 0.2,
              "specularHardness": 16,
              "reflectivity": 0.0
            }
          ],
          "primitives": [
            {
              "type": "plane",
              "point": [0, -1, 0],
              "normal": [0, 1, 0],
              "materialId": "mat1"
            }
          ],
          "lights": []
        }
        """;

    SceneLoader loader = new SceneLoader();
    Scene scene = loader.loadFromString(json);
    assertNotNull(scene);
    assertEquals(1, scene.getPrimitives().size());
  }

  @Test
  void loadFromString_parsesBox() throws IOException {
    String json =
        """
        {
          "camera": {
            "position": [0, 0, 5],
            "direction": [0, 0, -1],
            "up": [0, 1, 0],
            "fov": 60
          },
          "materials": [
            {
              "id": "mat1",
              "color": [0.2, 0.4, 0.8],
              "diffuse": 0.7,
              "specular": 0.3,
              "specularHardness": 20,
              "reflectivity": 0.1
            }
          ],
          "primitives": [
            {
              "type": "box",
              "min": [-1, -1, -1],
              "max": [1, 1, 1],
              "materialId": "mat1"
            }
          ],
          "lights": []
        }
        """;

    SceneLoader loader = new SceneLoader();
    Scene scene = loader.loadFromString(json);
    assertNotNull(scene);
    assertEquals(1, scene.getPrimitives().size());
  }

  @Test
  void loadFromString_usesDefaultValues() throws IOException {
    String json =
        """
        {
          "camera": {
            "position": [0, 0, 5],
            "direction": [0, 0, -1],
            "up": [0, 1, 0],
            "fov": 60
          },
          "materials": [],
          "primitives": [],
          "lights": []
        }
        """;

    SceneLoader loader = new SceneLoader();
    Scene scene = loader.loadFromString(json);
    assertNotNull(scene);
    assertEquals(800, scene.getImageWidth());
    assertEquals(600, scene.getImageHeight());
  }
}
