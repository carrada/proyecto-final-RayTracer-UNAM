package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Intersection;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Scene;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Material metálico (reflexión especular pura).
 *
 * <p>Refleja la luz especularmente con posible rugosidad.
 *
 * @author Cristopher Carrada
 */
public final class MetalMaterialStrategy implements MaterialStrategy {
  // Color base del material metálico
  private final Vector3D color;
  // Coeficiente que determina la intensidad de la reflexión especular
  private final double reflectivity;
  // Rugosidad de la superficie metálica
  private final double fuzziness;

  /**
   * Construye un material metálico.
   *
   * @param color color del metal
   * @param reflectivity reflectividad [0, 1]
   * @param fuzziness rugosidad de la superficie [0, 1]
   */
  public MetalMaterialStrategy(Vector3D color, double reflectivity, double fuzziness) {
    if (color == null) {
      throw new IllegalArgumentException("Color cannot be null");
    }
    this.color = color;
    this.reflectivity = Math.max(0, Math.min(1, reflectivity));
    this.fuzziness = Math.max(0, Math.min(1, fuzziness));
  }

  /**
   * Calcula el color resultante al aplicar el modelo metálico.
   *
   * @param incident rayo incidente
   * @param intersection punto de intersección
   * @param scene escena actual
   * @param depth profundidad de recursión
   * @return color calculado
   */
  @Override
  public Vector3D scatter(Ray incident, Intersection intersection, Scene scene, int depth) {
    if (depth >= scene.getMaxBounces()) {
      return scene.getBackgroundColor();
    }

    // Punto de intersección entre el rayo y el objeto
    Vector3D point = intersection.getPoint();
    // Normal en el punto de intersección
    Vector3D normal = intersection.getNormal();

    // Reflexión especular
    Vector3D reflectDir = reflect(incident.getDirection(), normal);

    // Aplicar rugosidad (opcional)
    if (fuzziness > 0) {
      // Agregar pequeña variación aleatoria para simular rugosidad
      reflectDir = reflectDir.add(randomInUnitSphere().multiply(fuzziness)).normalize();
    }

    Ray reflectRay = new Ray(point.add(normal.multiply(1e-4)), reflectDir);
    Optional<Intersection> reflectIntersection = scene.intersect(reflectRay);

    if (reflectIntersection.isPresent()) {
      Intersection reflectHit = reflectIntersection.get();
      Optional<MaterialStrategy> reflectMaterial =
          scene.getMaterialStrategy(reflectHit.getPrimitive().getMaterialId());

      if (reflectMaterial.isPresent()) {
        Vector3D reflectColor =
            reflectMaterial.get().scatter(reflectRay, reflectHit, scene, depth + 1);
        // Modular con el color del metal
        return new Vector3D(
            reflectColor.getX() * color.getX() * reflectivity,
            reflectColor.getY() * color.getY() * reflectivity,
            reflectColor.getZ() * color.getZ() * reflectivity);
      }
    }

    return scene.getBackgroundColor();
  }

  @Override
  public Vector3D getColor() {
    return color;
  }

  @Override
  public double getDiffuseCoefficient() {
    return 0.0; // Metales no tienen componente difusa
  }

  @Override
  public double getSpecularCoefficient() {
    return 1.0; // Reflexión completamente especular (metálica)
  }

  @Override
  public double getSpecularHardness() {
    return 1000.0; // Muy duro
  }

  @Override
  public double getReflectivity() {
    return reflectivity;
  }

  /**
   * Refleja un vector incidente en una normal.
   *
   * @param incident vector incidente
   * @param normal normal en el punto de reflexión
   * @return vector reflejado
   */
  private Vector3D reflect(Vector3D incident, Vector3D normal) {
    return incident.subtract(normal.multiply(2.0 * incident.dot(normal)));
  }

  /**
   * Genera un vector aleatorio dentro de una esfera unitaria.
   *
   * @return vector aleatorio
   */
  private Vector3D randomInUnitSphere() {
    // Generación simple de vector aleatorio en esfera unitaria
    double theta = 2 * Math.PI * Math.random();
    double phi = Math.acos(2 * Math.random() - 1);
    double r = Math.cbrt(Math.random());

    double x = r * Math.sin(phi) * Math.cos(theta);
    double y = r * Math.sin(phi) * Math.sin(theta);
    double z = r * Math.cos(phi);

    return new Vector3D(x, y, z);
  }
}
