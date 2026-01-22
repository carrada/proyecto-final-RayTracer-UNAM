package unam.ciencias.modeladoyprogramacion.raytracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.Light;
import unam.ciencias.modeladoyprogramacion.raytracer.materials.MaterialStrategy;
import unam.ciencias.modeladoyprogramacion.raytracer.primitives.Primitive;

/**
 * Representa una escena 3D completa.
 *
 * <p>Contiene todos los objetos geométricos, materiales, luces y configuración de la cámara.
 *
 * @author Cristopher Carrada
 */
public final class Scene {
  private final Camera camera;
  private final List<Primitive> primitives;
  private final List<Light> lights;
  private final Map<String, Material> materials;
  private final int imageWidth;
  private final int imageHeight;
  private final int samplesPerPixel;
  private final int maxBounces;
  private final Vector3D backgroundColor;

  /**
   * Constructor privado. Usar Builder para crear instancias.
   */
  private Scene(
      Camera camera,
      List<Primitive> primitives,
      List<Light> lights,
      Map<String, Material> materials,
      int imageWidth,
      int imageHeight,
      int samplesPerPixel,
      int maxBounces,
      Vector3D backgroundColor) {
    this.camera = camera;
    this.primitives = new ArrayList<>(primitives);
    this.lights = new ArrayList<>(lights);
    this.materials = new HashMap<>(materials);
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.samplesPerPixel = samplesPerPixel;
    this.maxBounces = maxBounces;
    this.backgroundColor = backgroundColor;
  }

  public Camera getCamera() {
    return camera;
  }

  public List<Primitive> getPrimitives() {
    return List.copyOf(primitives);
  }

  public List<Light> getLights() {
    return List.copyOf(lights);
  }

  public Optional<Material> getMaterial(String id) {
    return Optional.ofNullable(materials.get(id));
  }

  /**
   * Obtiene la estrategia de material por ID.
   *
   * @param id ID del material
   * @return Optional con la estrategia del material
   */
  public Optional<MaterialStrategy> getMaterialStrategy(String id) {
    return getMaterial(id).map(Material::getStrategy);
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public int getSamplesPerPixel() {
    return samplesPerPixel;
  }

  public int getMaxBounces() {
    return maxBounces;
  }

  public Vector3D getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Encuentra la intersección más cercana con los primitivos de la escena.
   *
   * @param ray el rayo a intersectar
   * @return Optional con la intersección más cercana, vacío si no hay
   */
  public Optional<Intersection> intersect(Ray ray) {
    Intersection closest = null;
    double minDistance = Double.POSITIVE_INFINITY;

    for (Primitive primitive : primitives) {
      Optional<Double> t = primitive.intersect(ray);
      if (t.isPresent() && t.get() < minDistance) {
        minDistance = t.get();
        Vector3D point = ray.at(t.get());
        Vector3D normal = primitive.getNormalAt(point);
        closest = new Intersection(minDistance, point, normal, primitive);
      }
    }

    return Optional.ofNullable(closest);
  }

  /** Builder para construir escenas. */
  public static class Builder {
    private Camera camera;
    private final List<Primitive> primitives = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    private final Map<String, Material> materials = new HashMap<>();
    private int imageWidth = 800;
    private int imageHeight = 600;
    private int samplesPerPixel = 1;
    private int maxBounces = 3;
    private Vector3D backgroundColor = new Vector3D(0.2, 0.2, 0.2);

    public Builder camera(Camera camera) {
      this.camera = camera;
      return this;
    }

    public Builder addPrimitive(Primitive primitive) {
      this.primitives.add(primitive);
      return this;
    }

    public Builder addLight(Light light) {
      this.lights.add(light);
      return this;
    }

    public Builder addMaterial(Material material) {
      this.materials.put(material.getId(), material);
      return this;
    }

    public Builder imageSize(int width, int height) {
      this.imageWidth = width;
      this.imageHeight = height;
      return this;
    }

    public Builder samplesPerPixel(int samples) {
      this.samplesPerPixel = samples;
      return this;
    }

    public Builder maxBounces(int bounces) {
      this.maxBounces = bounces;
      return this;
    }

    public Builder backgroundColor(Vector3D color) {
      this.backgroundColor = color;
      return this;
    }

    public Scene build() {
      if (camera == null) {
        throw new IllegalStateException("Camera is required");
      }
      return new Scene(
          camera,
          primitives,
          lights,
          materials,
          imageWidth,
          imageHeight,
          samplesPerPixel,
          maxBounces,
          backgroundColor);
    }
  }
}
