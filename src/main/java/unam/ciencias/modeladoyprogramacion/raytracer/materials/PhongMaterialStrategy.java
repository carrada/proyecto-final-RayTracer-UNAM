package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import java.util.List;
import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Intersection;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Scene;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.Light;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.SurfaceLight;

/**
 * Implementación del modelo de iluminación de Phong como Strategy.
 *
 * <p>REFACTORIZADO: Ahora delega responsabilidades a clases colaboradoras especializadas (SRP).
 * Combina componentes ambiente, difuso y especular para producir el color final.
 *
 * @author Cristopher Carrada
 */
public final class PhongMaterialStrategy implements MaterialStrategy {
  // Identificador único del material
  private final String id;
  // Color base del material
  private final Vector3D color;
  // Coeficiente de reflexión difusa
  private final double diffuseCoefficient;
  // Coeficiente de reflexión especular
  private final double specularCoefficient;
  // Dureza especular (brillo)
  private final double specularHardness;
  // Reflectividad del material
  private final double reflectivity;
  // Transparencia del material
  private final double transparency;
  // Índice de refracción del material
  private final double refractiveIndex;
  // Luz ambiente aplicada al material
  private final Vector3D ambientLight;

  // Colaboradores (Principio de Responsabilidad Única)
  private final AmbientCalculator ambientCalculator;
  private final ReflectionHandler reflectionHandler;
  private final RefractionHandler refractionHandler;

  /**
   * Construye una estrategia de material Phong.
   *
   * @param id identificador único del material
   * @param color color del material (RGB en rango [0, 1])
   * @param diffuseCoefficient coeficiente de reflexión difusa [0, 1]
   * @param specularCoefficient coeficiente de reflexión especular [0, 1]
   * @param specularHardness dureza especular (brillo)
   * @param reflectivity reflectividad del material [0, 1]
   * @param ambientLight luz ambiente
   */
  public PhongMaterialStrategy(
      String id,
      Vector3D color,
      double diffuseCoefficient,
      double specularCoefficient,
      double specularHardness,
      double reflectivity,
      Vector3D ambientLight) {
    this(
        id,
        color,
        diffuseCoefficient,
        specularCoefficient,
        specularHardness,
        reflectivity,
        0.0,
        1.0,
        ambientLight);
  }

  /**
   * Construye una estrategia de material Phong con refracción.
   *
   * @param id identificador único del material
   * @param color color del material (RGB en rango [0, 1])
   * @param diffuseCoefficient coeficiente de reflexión difusa [0, 1]
   * @param specularCoefficient coeficiente de reflexión especular [0, 1]
   * @param specularHardness dureza especular (brillo)
   * @param reflectivity reflectividad del material [0, 1]
   * @param transparency transparencia del material [0, 1]
   * @param refractiveIndex índice de refracción (≥ 1.0)
   * @param ambientLight luz ambiente
   */
  public PhongMaterialStrategy(
      String id,
      Vector3D color,
      double diffuseCoefficient,
      double specularCoefficient,
      double specularHardness,
      double reflectivity,
      double transparency,
      double refractiveIndex,
      Vector3D ambientLight) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Material ID cannot be null or empty");
    }
    if (color == null) {
      throw new IllegalArgumentException("Color cannot be null");
    }
    this.id = id;
    this.color = color;
    this.diffuseCoefficient = Math.max(0, Math.min(1, diffuseCoefficient));
    this.specularCoefficient = Math.max(0, Math.min(1, specularCoefficient));
    this.specularHardness = Math.max(1, specularHardness);
    this.reflectivity = Math.max(0, Math.min(1, reflectivity));
    this.transparency = Math.max(0, Math.min(1, transparency));
    this.refractiveIndex = Math.max(1.0, refractiveIndex);
    this.ambientLight = ambientLight != null ? ambientLight : new Vector3D(0.1, 0.1, 0.1);

    // Inicializar colaboradores
    this.ambientCalculator = new AmbientCalculator();
    this.reflectionHandler = new ReflectionHandler();
    this.refractionHandler = new RefractionHandler();
  }

  @Override
  public Vector3D scatter(Ray incident, Intersection intersection, Scene scene, int depth) {
    Vector3D point = intersection.getPoint();
    Vector3D normal = intersection.getNormal();
    Vector3D viewDir = incident.getDirection().negate();

    // 1. Componente ambiente (delegado a AmbientCalculator)
    Vector3D resultColor = ambientCalculator.calculate(ambientLight, color);

    // 2. Contribución de cada luz (difuso + especular)
    resultColor = resultColor.add(calculateLightingContribution(point, normal, viewDir, scene));

    // 3. Reflexión (delegado a ReflectionHandler)
    if (reflectivity > 0) {
      Vector3D reflectionColor =
          reflectionHandler.handleReflection(incident, intersection, scene, depth, reflectivity);
      resultColor = blendColors(resultColor, reflectionColor, reflectivity);
    }

    // 4. Refracción (delegado a RefractionHandler)
    if (transparency > 0) {
      Vector3D refractionColor =
          refractionHandler.handleRefraction(
              incident, intersection, scene, depth, transparency, refractiveIndex);
      // RefractionHandler ya aplica el peso, solo sumar
      resultColor = resultColor.add(refractionColor);
    }

    return clamp(resultColor);
  }

  /**
   * Calcula la contribución de todas las luces de la escena (difuso + especular).
   *
   * @param point punto de intersección
   * @param normal normal en el punto
   * @param viewDir dirección de vista
   * @param scene escena completa
   * @return color de iluminación directa
   */
  private Vector3D calculateLightingContribution(
      Vector3D point, Vector3D normal, Vector3D viewDir, Scene scene) {
    Vector3D lighting = new Vector3D(0, 0, 0);

    for (Light light : scene.getLights()) {
      if (light instanceof SurfaceLight) {
        lighting =
            lighting.add(
                computeSurfaceLightContribution(
                    (SurfaceLight) light, point, normal, viewDir, scene));
        continue;
      }

      // Luces puntuales y direccionales (un solo rayo)
      Vector3D lightDir = light.getDirectionFrom(point);
      double lightDistance = light.getDistanceFrom(point);

      if (isInShadow(point, lightDir, lightDistance, scene)) {
        continue;
      }
      // Componente difuso (Lambert)
      double diffuseIntensity = Math.max(0, normal.dot(lightDir));
      Vector3D diffuse =
          new Vector3D(
              diffuseCoefficient
                  * diffuseIntensity
                  * color.getX()
                  * light.getColor().getX()
                  * light.getIntensity(),
              diffuseCoefficient
                  * diffuseIntensity
                  * color.getY()
                  * light.getColor().getY()
                  * light.getIntensity(),
              diffuseCoefficient
                  * diffuseIntensity
                  * color.getZ()
                  * light.getColor().getZ()
                  * light.getIntensity());

      // Componente especular (Phong)
      Vector3D reflectDir = reflect(lightDir.negate(), normal);
      double specularIntensity =
          Math.pow(Math.max(0, viewDir.dot(reflectDir)), specularHardness);
      Vector3D specular =
          new Vector3D(
              specularCoefficient * specularIntensity * light.getColor().getX() * light.getIntensity(),
              specularCoefficient * specularIntensity * light.getColor().getY() * light.getIntensity(),
              specularCoefficient * specularIntensity * light.getColor().getZ() * light.getIntensity());

      lighting = lighting.add(diffuse).add(specular);
    }

    return lighting;
  }

  /**
   * Mezcla dos colores usando un factor de mezcla.
   *
   * @param base color base
   * @param blend color a mezclar
   * @param factor factor de mezcla [0, 1]
   * @return color resultante
   */
  private Vector3D blendColors(Vector3D base, Vector3D blend, double factor) {
    return new Vector3D(
        base.getX() * (1 - factor) + blend.getX() * factor,
        base.getY() * (1 - factor) + blend.getY() * factor,
        base.getZ() * (1 - factor) + blend.getZ() * factor);
  }

  @Override
  public Vector3D getColor() {
    return color;
  }

  @Override
  public double getDiffuseCoefficient() {
    return diffuseCoefficient;
  }

  @Override
  public double getSpecularCoefficient() {
    return specularCoefficient;
  }

  @Override
  public double getSpecularHardness() {
    return specularHardness;
  }

  @Override
  public double getReflectivity() {
    return reflectivity;
  }

  @Override
  public double getTransparency() {
    return transparency;
  }

  @Override
  public double getRefractiveIndex() {
    return refractiveIndex;
  }

  public String getId() {
    return id;
  }

  /**
   * Calcula la contribución de una luz de superficie usando muestreo Monte Carlo.
   *
   * @param surfaceLight la luz de superficie
   * @param point punto de intersección
   * @param normal normal en el punto
   * @param viewDir dirección de vista
   * @param scene escena para verificar sombras
   * @return color contribuido por la luz de superficie
   */
  private Vector3D computeSurfaceLightContribution(
      SurfaceLight surfaceLight, Vector3D point, Vector3D normal, Vector3D viewDir, Scene scene) {
    List<Vector3D> samplePoints = surfaceLight.getSamplePoints();
    int visibleSamples = 0;
    Vector3D totalDiffuse = new Vector3D(0, 0, 0);
    Vector3D totalSpecular = new Vector3D(0, 0, 0);

    // Muestrear múltiples puntos en la superficie de la luz
    for (Vector3D samplePoint : samplePoints) {
      Vector3D lightDir = surfaceLight.getDirectionFromToSample(point, samplePoint);
      double lightDistance = surfaceLight.getDistanceFromToSample(point, samplePoint);

      // Verificar si este punto está en sombra
      if (isInShadow(point, lightDir, lightDistance, scene)) {
        continue;
      }

      visibleSamples++;

      // Componente difuso
      double diffuseIntensity = Math.max(0, normal.dot(lightDir));
      Vector3D diffuse =
          new Vector3D(
              diffuseCoefficient * diffuseIntensity * color.getX() * surfaceLight.getColor().getX(),
              diffuseCoefficient * diffuseIntensity * color.getY() * surfaceLight.getColor().getY(),
              diffuseCoefficient * diffuseIntensity * color.getZ() * surfaceLight.getColor().getZ());

      // Componente especular
      Vector3D reflectDir = reflect(lightDir.negate(), normal);
      double specularIntensity = Math.pow(Math.max(0, viewDir.dot(reflectDir)), specularHardness);
      Vector3D specular =
          new Vector3D(
              specularCoefficient * specularIntensity * surfaceLight.getColor().getX(),
              specularCoefficient * specularIntensity * surfaceLight.getColor().getY(),
              specularCoefficient * specularIntensity * surfaceLight.getColor().getZ());

      totalDiffuse = totalDiffuse.add(diffuse);
      totalSpecular = totalSpecular.add(specular);
    }

    // Promediar las contribuciones y aplicar intensidad
    if (visibleSamples == 0) {
      return new Vector3D(0, 0, 0);
    }

    double samples = samplePoints.size();
    double intensity = surfaceLight.getIntensity();
    return new Vector3D(
        (totalDiffuse.getX() + totalSpecular.getX()) * intensity / samples,
        (totalDiffuse.getY() + totalSpecular.getY()) * intensity / samples,
        (totalDiffuse.getZ() + totalSpecular.getZ()) * intensity / samples);
  }

  private boolean isInShadow(
      Vector3D point, Vector3D lightDir, double lightDistance, Scene scene) {
    Ray shadowRay = new Ray(point.add(lightDir.multiply(1e-4)), lightDir);
    Optional<Intersection> shadowIntersection = scene.intersect(shadowRay);

    return shadowIntersection.isPresent()
        && shadowIntersection.get().getDistance() < lightDistance;
  }

  private Vector3D reflect(Vector3D incident, Vector3D normal) {
    return incident.subtract(normal.multiply(2.0 * incident.dot(normal)));
  }

  private Vector3D clamp(Vector3D color) {
    return new Vector3D(
        Math.max(0, Math.min(1, color.getX())),
        Math.max(0, Math.min(1, color.getY())),
        Math.max(0, Math.min(1, color.getZ())));
  }

  @Override
  public String toString() {
    return String.format(
        "PhongMaterial[id=%s, color=%s, diffuse=%.2f, specular=%.2f]",
        id, color, diffuseCoefficient, specularCoefficient);
  }
}
