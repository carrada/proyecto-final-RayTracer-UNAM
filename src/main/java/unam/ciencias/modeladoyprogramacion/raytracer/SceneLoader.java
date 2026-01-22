package unam.ciencias.modeladoyprogramacion.raytracer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.DirectionalLight;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.Light;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.PointLight;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.SurfaceLight;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Box;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Plane;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Primitive;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Sphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Carga escenas desde archivos JSON.
 *
 * <p>Lee archivos JSON que describen escenas 3D completas y las convierte en objetos Scene.
 *
 * @author Cristopher Carrada
 */
public final class SceneLoader {
  // Mapper para convertir JSON en objetos Java
  private final ObjectMapper mapper;

  /**
   * Constructor que inicializa el ObjectMapper.
   */
  public SceneLoader() {
    this.mapper = new ObjectMapper();
  }

  /**
   * Carga una escena desde un archivo JSON.
   *
   * @param jsonFile ruta al archivo JSON
   * @return la escena cargada
   * @throws IOException si hay error al leer el archivo
   * @throws IllegalArgumentException si el JSON es inválido
   */
  public Scene loadFromFile(String jsonFile) throws IOException {
    // Verifica que el archivo exista antes de intentar cargarlo
    File file = new File(jsonFile);
    if (!file.exists()) {
      throw new IOException("Scene file not found: " + jsonFile);
    }

    // Lee el archivo JSON y lo convierte en un árbol de nodos
    JsonNode root = mapper.readTree(file);
    return parseScene(root);
  }

  /**
   * Carga una escena desde un string JSON.
   *
   * @param jsonString contenido JSON
   * @return la escena cargada
   * @throws IOException si hay error al parsear
   */
  public Scene loadFromString(String jsonString) throws IOException {
    // Convierte el string JSON en un árbol de nodos
    JsonNode root = mapper.readTree(jsonString);
    return parseScene(root);
  }

  /**
   * Analiza un nodo raíz de JSON y construye una escena.
   *
   * @param root nodo raíz del JSON
   * @return objeto Scene construido a partir del JSON
   */
  private Scene parseScene(JsonNode root) {
    // Parámetros generales
    int imageWidth = root.path("imageWidth").asInt(800);
    int imageHeight = root.path("imageHeight").asInt(600);
    double focalDistance = root.path("focalDistance").asDouble(5.0);
    int samplesPerPixel = root.path("samplesPerPixel").asInt(1);
    int rayMaxBounces = root.path("rayMaxBounces").asInt(3);

    // Cámara
    Camera camera = parseCamera(root.path("camera"), focalDistance);

    // Materiales
    Map<String, Material> materials = new HashMap<>();
    JsonNode materialsNode = root.path("materials");
    if (materialsNode.isArray()) {
      for (JsonNode matNode : materialsNode) {
        String id = matNode.path("id").asText();
        Material material = parseMaterial(matNode);
        materials.put(id, material);
      }
    }

    // Luces
    List<Light> lights = new ArrayList<>();
    JsonNode lightsNode = root.path("lights");
    if (lightsNode.isArray()) {
      for (JsonNode lightNode : lightsNode) {
        Light light = parseLight(lightNode);
        if (light != null) {
          lights.add(light);
        }
      }
    }

    // Primitivos
    List<Primitive> primitives = new ArrayList<>();
    JsonNode primitivesNode = root.path("primitives");
    if (primitivesNode.isArray()) {
      for (JsonNode primNode : primitivesNode) {
        Primitive primitive = parsePrimitive(primNode);
        if (primitive != null) {
          primitives.add(primitive);
        }
      }
    }

    // Color de fondo (opcional, por defecto negro)
    Vector3D backgroundColor = parseVector3D(root.path("backgroundColor"), new Vector3D(0, 0, 0));

    Scene.Builder builder =
        new Scene.Builder()
            .camera(camera)
            .imageSize(imageWidth, imageHeight)
            .samplesPerPixel(samplesPerPixel)
            .maxBounces(rayMaxBounces)
            .backgroundColor(backgroundColor);

    // Agregar materiales
    for (Material material : materials.values()) {
      builder.addMaterial(material);
    }

    // Agregar luces
    for (Light light : lights) {
      builder.addLight(light);
    }

    // Agregar primitivos
    for (Primitive primitive : primitives) {
      builder.addPrimitive(primitive);
    }

    return builder.build();
  }

  /**
   * Analiza un nodo de cámara y construye un objeto Camera.
   *
   * @param cameraNode nodo de la cámara en el JSON
   * @param focalDistance distancia focal de la cámara
   * @return objeto Camera construido a partir del JSON
   */
  private Camera parseCamera(JsonNode cameraNode, double focalDistance) {
    if (cameraNode.isMissingNode()) {
      // Cámara por defecto
      return new Camera(
          new Vector3D(0, 0, -5),
          new Vector3D(0, 0, 1),
          new Vector3D(0, 1, 0),
          60.0,
          focalDistance);
    }

    Vector3D position = parseVector3D(cameraNode.path("position"), new Vector3D(0, 0, -5));
    Vector3D direction = parseVector3D(cameraNode.path("direction"), new Vector3D(0, 0, 1));
    Vector3D up = parseVector3D(cameraNode.path("normalUp"), new Vector3D(0, 1, 0));
    double fov = cameraNode.path("angleOfVision").asDouble(60.0);

    return new Camera(position, direction, up, fov, focalDistance);
  }

  /**
   * Analiza un nodo de material y construye un objeto Material.
   *
   * @param matNode nodo del material en el JSON
   * @return objeto Material construido a partir del JSON
   */
  private Material parseMaterial(JsonNode matNode) {
    String id = matNode.path("id").asText("default");
    Vector3D color = parseVector3D(matNode.path("color"), new Vector3D(1, 1, 1));
    double diffuse = matNode.path("diffuseCoefficient").asDouble(0.8);
    double specular = matNode.path("specularCoefficient").asDouble(0.5);
    double hardness = matNode.path("specularHardness").asDouble(50.0);
    double reflectivity = matNode.path("reflectivity").asDouble(0.0);
    double transparency = matNode.path("transparency").asDouble(0.0);
    double refractiveIndex = matNode.path("refractiveIndex").asDouble(1.0);

    // Si hay transparencia o índice de refracción diferente de aire, usar constructor completo
    if (transparency > 0 || refractiveIndex > 1.0) {
      return new Material(
          id,
          new unam.ciencias.modeladoyprogramacion.raytracer.materials.PhongMaterialStrategy(
              id,
              color,
              diffuse,
              specular,
              hardness,
              reflectivity,
              transparency,
              refractiveIndex,
              new Vector3D(0.1, 0.1, 0.1)));
    }

    return new Material(id, color, diffuse, specular, hardness, reflectivity);
  }

  /**
   * Analiza un nodo de luz y construye un objeto Light.
   *
   * @param lightNode nodo de la luz en el JSON
   * @return objeto Light construido a partir del JSON
   */
  private Light parseLight(JsonNode lightNode) {
    String type = lightNode.path("type").asText("point");
    Vector3D color = parseVector3D(lightNode.path("color"), new Vector3D(1, 1, 1));
    double intensity = lightNode.path("intensity").asDouble(1.0);

    if ("point".equals(type)) {
      Vector3D position = parseVector3D(lightNode.path("position"), new Vector3D(0, 10, 0));
      return new PointLight(color, intensity, position);
    }

    if ("directional".equals(type)) {
      Vector3D direction = parseVector3D(lightNode.path("direction"), new Vector3D(0, -1, 0));
      return new DirectionalLight(color, intensity, direction);
    }

    if ("surface".equals(type)) {
      Vector3D position = parseVector3D(lightNode.path("position"), new Vector3D(0, 10, 0));
      Vector3D normal = parseVector3D(lightNode.path("normal"), new Vector3D(0, -1, 0));
      double width = lightNode.path("width").asDouble(2.0);
      double height = lightNode.path("height").asDouble(2.0);
      int samples = lightNode.path("samples").asInt(16);
      return new SurfaceLight(color, intensity, position, normal, width, height, samples);
    }

    return null;
  }

  /**
   * Analiza un nodo de primitivo y construye un objeto Primitive.
   *
   * @param primNode nodo del primitivo en el JSON
   * @return objeto Primitive construido a partir del JSON
   */
  private Primitive parsePrimitive(JsonNode primNode) {
    String type = primNode.path("type").asText();
    String name = primNode.path("name").asText("unnamed");
    String materialId = primNode.path("materialId").asText("default");

    switch (type) {
      case "sphere":
        {
          Vector3D center = parseVector3D(primNode.path("position"), new Vector3D(0, 0, 0));
          double radius = primNode.path("radius").asDouble(1.0);
          return new Sphere(name, materialId, center, radius);
        }

      case "plane":
        {
          Vector3D point = parseVector3D(primNode.path("position"), new Vector3D(0, 0, 0));
          Vector3D normal = parseVector3D(primNode.path("normal"), new Vector3D(0, 1, 0));
          return new Plane(name, materialId, point, normal);
        }

      case "box":
        {
          Vector3D min = parseVector3D(primNode.path("position"), new Vector3D(0, 0, 0));
          double width = primNode.path("width").asDouble(1.0);
          double height = primNode.path("height").asDouble(1.0);
          double depth = primNode.path("depth").asDouble(1.0);

          return new Box(name, materialId, min, width, height, depth);
        }

      default:
        Logger logger = LoggerFactory.getLogger(SceneLoader.class);
        logger.warn("Unknown primitive type: {}", type);
        return null;
    }
  }

  /**
   * Analiza un nodo vectorial 3D y lo convierte en un objeto Vector3D.
   *
   * @param node nodo del vector en el JSON
   * @param defaultValue valor por defecto en caso de nodo inválido
   * @return objeto Vector3D construido a partir del nodo
   */
  private Vector3D parseVector3D(JsonNode node, Vector3D defaultValue) {
    if (node.isMissingNode() || !node.isArray() || node.size() != 3) {
      return defaultValue;
    }
    return new Vector3D(node.get(0).asDouble(), node.get(1).asDouble(), node.get(2).asDouble());
  }
}
