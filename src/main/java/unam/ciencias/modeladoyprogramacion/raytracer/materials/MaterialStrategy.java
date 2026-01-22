package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import unam.ciencias.modeladoyprogramacion.raytracer.Intersection;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Scene;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Interfaz Strategy para diferentes algoritmos de cálculo de materiales.
 *
 * <p>Permite intercambiar algoritmos de dispersión de luz en tiempo de ejecución siguiendo el
 * patrón Strategy (GoF).
 *
 * @author Cristopher Carrada
 */
public interface MaterialStrategy {
  /**
   * Calcula la dispersión de la luz al interactuar con este material.
   *
   * @param incident rayo incidente
   * @param intersection información de la intersección
   * @param scene escena completa
   * @param depth profundidad de recursión actual
   * @return color resultante después de la dispersión
   */
  Vector3D scatter(Ray incident, Intersection intersection, Scene scene, int depth);

  /**
   * Obtiene el color base del material.
   *
   * @return color del material
   */
  Vector3D getColor();

  /**
   * Obtiene el coeficiente de reflexión difusa.
   *
   * @return coeficiente difuso [0, 1]
   */
  double getDiffuseCoefficient();

  /**
   * Obtiene el coeficiente de reflexión especular.
   *
   * @return coeficiente especular [0, 1]
   */
  double getSpecularCoefficient();

  /**
   * Obtiene la dureza especular (brillo).
   *
   * @return dureza especular
   */
  double getSpecularHardness();

  /**
   * Obtiene la reflectividad del material.
   *
   * @return reflectividad [0, 1]
   */
  double getReflectivity();

  /**
   * Obtiene el índice de refracción del material.
   *
   * <p>Valores comunes: aire=1.0, agua=1.33, vidrio=1.5, diamante=2.42
   *
   * @return índice de refracción (≥ 1.0)
   */
  default double getRefractiveIndex() {
    return 1.0; // Por defecto, no refractivo (aire)
  }

  /**
   * Obtiene la transparencia del material.
   *
   * @return transparencia [0, 1], donde 0=opaco y 1=completamente transparente
   */
  default double getTransparency() {
    return 0.0; // Por defecto, opaco
  }
}
