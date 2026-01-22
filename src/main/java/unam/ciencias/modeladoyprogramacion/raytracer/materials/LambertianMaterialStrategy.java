package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import unam.ciencias.modeladoyprogramacion.raytracer.Intersection;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Scene;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;
import unam.ciencias.modeladoyprogramacion.raytracer.lights.Light;

/**
 * Material Lambertiano (difuso puro).
 *
 * <p>Refleja la luz uniformemente en todas las direcciones, sin componente especular.
 *
 * @author Cristopher Carrada
 */
public final class LambertianMaterialStrategy implements MaterialStrategy {
  // Color base del material
  private final Vector3D color;
  // Coeficiente que determina la intensidad de la reflexión difusa
  private final double diffuseCoefficient;
  // Luz ambiente aplicada al material
  private final Vector3D ambientLight;

  /**
   * Construye un material Lambertiano.
   *
   * @param color color del material
   * @param diffuseCoefficient coeficiente difuso [0, 1]
   * @param ambientLight luz ambiente
   */
  public LambertianMaterialStrategy(
      Vector3D color, double diffuseCoefficient, Vector3D ambientLight) {
    if (color == null) {
      throw new IllegalArgumentException("Color cannot be null");
    }
    this.color = color;
    this.diffuseCoefficient = Math.max(0, Math.min(1, diffuseCoefficient));
    this.ambientLight = ambientLight != null ? ambientLight : new Vector3D(0.1, 0.1, 0.1);
  }

  /**
   * Calcula el color resultante al aplicar el modelo Lambertiano.
   *
   * @param incident rayo incidente
   * @param intersection punto de intersección
   * @param scene escena actual
   * @param depth profundidad de recursión
   * @return color calculado
   */
  @Override
  public Vector3D scatter(Ray incident, Intersection intersection, Scene scene, int depth) {
    // Punto de intersección entre el rayo y el objeto
    Vector3D point = intersection.getPoint();
    // Normal en el punto de intersección
    Vector3D normal = intersection.getNormal();

    // Componente ambiente
    Vector3D ambient =
        new Vector3D(
            ambientLight.getX() * color.getX(),
            ambientLight.getY() * color.getY(),
            ambientLight.getZ() * color.getZ());

    Vector3D resultColor = ambient;

    // Solo componente difuso
    for (Light light : scene.getLights()) {
      Vector3D lightDir = light.getDirectionFrom(point);
      double lightDistance = light.getDistanceFrom(point);

      // Verificar sombras
      Ray shadowRay = new Ray(point.add(lightDir.multiply(1e-4)), lightDir);
      if (scene.intersect(shadowRay).isPresent()
          && scene.intersect(shadowRay).get().getDistance() < lightDistance) {
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

      resultColor = resultColor.add(diffuse);
    }

    return clamp(resultColor);
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
    return 0.0; // Sin especular
  }

  @Override
  public double getSpecularHardness() {
    return 0.0; // Sin especular
  }

  @Override
  public double getReflectivity() {
    return 0.0; // Sin reflexión
  }

  private Vector3D clamp(Vector3D color) {
    return new Vector3D(
        Math.max(0, Math.min(1, color.getX())),
        Math.max(0, Math.min(1, color.getY())),
        Math.max(0, Math.min(1, color.getZ())));
  }
}
