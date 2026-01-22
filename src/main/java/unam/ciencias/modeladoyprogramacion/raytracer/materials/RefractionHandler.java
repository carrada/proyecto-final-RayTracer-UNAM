package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Intersection;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Scene;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Manejador de refracciones y transparencia con ecuaciones de Fresnel (Phong Extended Model).
 *
 * <p>Esta clase encapsula la física compleja de la refracción de luz a través de materiales
 * transparentes, incluyendo:
 *
 * <ul>
 *   <li><b>Ley de Snell:</b> Cambio de dirección de la luz al pasar entre medios
 *   <li><b>Aproximación de Schlick:</b> Estimación eficiente de las ecuaciones de Fresnel
 *   <li><b>Reflexión interna total:</b> Condición cuando la luz no puede salir del material
 * </ul>
 *
 * <p><b>Ley de Snell:</b> {@code n₁ sin(θ₁) = n₂ sin(θ₂)}
 *
 * <p>Donde:
 * <ul>
 *   <li>n₁ = índice de refracción del medio origen (aire = 1.0)
 *   <li>n₂ = índice de refracción del medio destino (vidrio ≈ 1.5, agua ≈ 1.33)
 *   <li>θ₁ = ángulo de incidencia
 *   <li>θ₂ = ángulo de refracción
 * </ul>
 *
 * <p><b>Aproximación de Schlick (Fresnel):</b>
 * {@code R(θ) = R₀ + (1 - R₀)(1 - cos θ)⁵}
 *
 * <p>Donde {@code R₀ = ((n₁ - n₂) / (n₁ + n₂))²}
 *
 * <p>Esta aproximación calcula qué porcentaje de luz se refleja vs. refracta, dependiendo del
 * ángulo de incidencia (efecto observado en ventanas que reflejan más cuando las ves de lado).
 *
 * <p><b>Ejemplo de uso:</b>
 * <pre>{@code
 * RefractionHandler handler = new RefractionHandler();
 * Ray incident = new Ray(origin, direction);
 * Intersection hit = scene.intersect(incident).get();
 *
 * Vector3D refracted = handler.handleRefraction(
 *     incident, hit, scene, depth,
 *     0.95,  // 95% transparente (vidrio)
 *     1.5    // índice de refracción del vidrio
 * );
 * }</pre>
 *
 * @author Cristopher Carrada
 * @see PhongMaterialStrategy
 * @see ReflectionHandler
 */
public final class RefractionHandler {

  /**
   * Calcula la contribución de refracción al color final mediante ray tracing recursivo.
   *
   * <p>Este método implementa física completa de refracción:
   *
   * <ol>
   *   <li>Detecta si el rayo entra o sale del material (usando producto punto con normal)
   *   <li>Calcula el ratio de índices de refracción (η = n₁/n₂)
   *   <li>Aplica Ley de Snell para obtener dirección refractada
   *   <li>Detecta reflexión interna total (sin² θ₂ > 1)
   *   <li>Calcula probabilidad de reflexión vs. refracción (Fresnel/Schlick)
   *   <li>Combina reflexión y refracción según probabilidad
   * </ol>
   *
   * <p><b>Optimización:</b> Retorna negro si transparencia es cero, evitando cálculos costosos.
   *
   * @param incident rayo incidente que golpeó la superficie transparente
   * @param intersection punto de intersección con normal de superficie
   * @param scene escena completa para trazar rayos refractados/reflejados
   * @param depth profundidad de recursión actual (previene recursión infinita)
   * @param transparency transparencia del material [0, 1] (0=opaco, 1=completamente transparente)
   * @param refractiveIndex índice de refracción del material (aire=1.0, agua=1.33, vidrio=1.5,
   *     diamante=2.42)
   * @return color de refracción (RGB), o negro si opaco o excede profundidad máxima
   */
  public Vector3D handleRefraction(
      Ray incident,
      Intersection intersection,
      Scene scene,
      int depth,
      double transparency,
      double refractiveIndex) {
    if (transparency <= 0 || depth >= scene.getMaxBounces()) {
      return new Vector3D(0, 0, 0);
    }

    Vector3D point = intersection.getPoint();
    Vector3D normal = intersection.getNormal();
    Vector3D incidentDir = incident.getDirection();

    // Determinar si entramos o salimos del material
    double cosTheta = -normal.dot(incidentDir);
    boolean entering = cosTheta > 0;

    // Índices de refracción (n1 = origen, n2 = destino)
    double n1 = entering ? 1.0 : refractiveIndex;
    double n2 = entering ? refractiveIndex : 1.0;
    double eta = n1 / n2;

    // Ajustar normal si salimos del material
    Vector3D effectiveNormal = entering ? normal : normal.negate();
    double cosThetaI = Math.abs(cosTheta);

    // Calcular dirección refractada con Ley de Snell
    Optional<Vector3D> refractDir = refract(incidentDir, effectiveNormal, eta);

    if (refractDir.isEmpty()) {
      // Reflexión interna total - no hay refracción
      return new Vector3D(0, 0, 0);
    }

    // Calcular coeficiente de Fresnel
    double fresnel = schlickApproximation(cosThetaI, n1, n2);

    // Trazar rayo refractado
    Vector3D refractOffset = entering ? effectiveNormal.negate() : effectiveNormal;
    Ray refractRay = new Ray(point.add(refractOffset.multiply(1e-4)), refractDir.get());
    Optional<Intersection> refractIntersection = scene.intersect(refractRay);

    Vector3D refractColor = scene.getBackgroundColor();
    if (refractIntersection.isPresent()) {
      Intersection refractHit = refractIntersection.get();
      Optional<MaterialStrategy> refractMaterial =
          scene.getMaterialStrategy(refractHit.getPrimitive().getMaterialId());

      if (refractMaterial.isPresent()) {
        refractColor = refractMaterial.get().scatter(refractRay, refractHit, scene, depth + 1);
      }
    }

    // Aplicar peso de Fresnel (menos reflexión = más refracción)
    double refractWeight = transparency * (1.0 - fresnel);
    return refractColor.multiply(refractWeight);
  }

  /**
   * Calcula la dirección refractada usando la Ley de Snell.
   *
   * @param incident vector incidente (normalizado)
   * @param normal vector normal (normalizado)
   * @param eta razón de índices de refracción (n1/n2)
   * @return Optional con dirección refractada, o vacío si hay reflexión interna total
   */
  private Optional<Vector3D> refract(Vector3D incident, Vector3D normal, double eta) {
    double cosTheta = -normal.dot(incident);
    double sinThetaTSquared = eta * eta * (1.0 - cosTheta * cosTheta);

    // Reflexión interna total si sin²θ_t > 1
    if (sinThetaTSquared > 1.0) {
      return Optional.empty();
    }

    double cosThetaT = Math.sqrt(1.0 - sinThetaTSquared);
    Vector3D perpendicular = incident.add(normal.multiply(cosTheta)).multiply(eta);
    Vector3D parallel = normal.multiply(-cosThetaT);

    return Optional.of(perpendicular.add(parallel).normalize());
  }

  /**
   * Aproximación de Schlick para ecuaciones de Fresnel.
   *
   * @param cosTheta coseno del ángulo de incidencia
   * @param n1 índice de refracción del medio de origen
   * @param n2 índice de refracción del medio de destino
   * @return coeficiente de reflexión de Fresnel [0, 1]
   */
  private double schlickApproximation(double cosTheta, double n1, double n2) {
    double r0 = Math.pow((n1 - n2) / (n1 + n2), 2);
    return r0 + (1 - r0) * Math.pow(1 - cosTheta, 5);
  }
}
