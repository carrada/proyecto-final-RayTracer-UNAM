package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Intersection;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Scene;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Manejador de reflexiones especulares recursivas (Phong Shading Model).
 *
 * <p>Esta clase encapsula la lógica de cálculo de rayos reflejados y su contribución al color
 * final, implementando reflexiones tipo espejo (perfect mirror reflections). Sigue el principio de
 * Responsabilidad Única (SRP) al separar esta responsabilidad del cálculo principal de
 * iluminación.
 *
 * <p><b>Física:</b> Utiliza la ley de reflexión: el ángulo de incidencia es igual al ángulo de
 * reflexión con respecto a la normal de la superficie.
 *
 * <p><b>Fórmula del vector reflejado:</b>
 * {@code R = I - 2(I·N)N}
 *
 * <p>Donde:
 * <ul>
 *   <li>R = vector reflejado
 *   <li>I = vector incidente (normalizado)
 *   <li>N = normal de la superficie
 *   <li>· = producto punto
 * </ul>
 *
 * <p><b>Ejemplo de uso:</b>
 * <pre>{@code
 * ReflectionHandler handler = new ReflectionHandler();
 * Ray incident = new Ray(origin, direction);
 * Intersection hit = scene.intersect(incident).get();
 *
 * Vector3D reflection = handler.handleReflection(
 *     incident, hit, scene, depth, 0.9  // 90% reflectivo (espejo)
 * );
 * }</pre>
 *
 * @author Cristopher Carrada
 * @see PhongMaterialStrategy
 * @see RefractionHandler
 */
public final class ReflectionHandler {

  /**
   * Calcula la contribución de reflexión especular al color final mediante ray tracing recursivo.
   *
   * <p>Este método traza un rayo reflejado desde el punto de intersección y calcula el color que
   * ve ese rayo. La recursión se detiene cuando se alcanza la profundidad máxima o cuando la
   * reflectividad es cero.
   *
   * <p><b>Optimización:</b> Retorna negro inmediatamente si no hay reflexión significativa,
   * evitando cálculos innecesarios.
   *
   * @param incident rayo incidente que golpeó la superficie
   * @param intersection punto de intersección con normal de superficie
   * @param scene escena completa para trazar rayo reflejado recursivamente
   * @param depth profundidad de recursión actual (previene recursión infinita)
   * @param reflectivity coeficiente de reflectividad del material [0, 1] (0=mate, 1=espejo
   *     perfecto)
   * @return color de reflexión (RGB), o negro si no hay reflexión o se excedió profundidad máxima
   */
  public Vector3D handleReflection(
      Ray incident,
      Intersection intersection,
      Scene scene,
      int depth,
      double reflectivity) {
    if (reflectivity <= 0 || depth >= scene.getMaxBounces()) {
      return new Vector3D(0, 0, 0);
    }

    Vector3D point = intersection.getPoint();
    Vector3D normal = intersection.getNormal();

    // Calcular dirección reflejada
    Vector3D reflectDir = reflect(incident.getDirection(), normal);

    // Offset para evitar self-intersection
    Ray reflectRay = new Ray(point.add(normal.multiply(1e-4)), reflectDir);
    Optional<Intersection> reflectIntersection = scene.intersect(reflectRay);

    if (reflectIntersection.isEmpty()) {
      return new Vector3D(0, 0, 0);
    }

    Intersection reflectHit = reflectIntersection.get();
    Optional<MaterialStrategy> reflectMaterial =
        scene.getMaterialStrategy(reflectHit.getPrimitive().getMaterialId());

    if (reflectMaterial.isEmpty()) {
      return new Vector3D(0, 0, 0);
    }

    return reflectMaterial.get().scatter(reflectRay, reflectHit, scene, depth + 1);
  }

  /**
   * Refleja un vector respecto a una normal.
   *
   * @param incident vector incidente
   * @param normal vector normal (debe estar normalizado)
   * @return vector reflejado
   */
  private Vector3D reflect(Vector3D incident, Vector3D normal) {
    double dotProduct = incident.dot(normal);
    return incident.subtract(normal.multiply(2 * dotProduct));
  }
}
