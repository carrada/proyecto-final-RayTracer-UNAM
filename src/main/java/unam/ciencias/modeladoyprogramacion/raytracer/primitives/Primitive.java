package unam.ciencias.modeladoyprogramacion.raytracer.primitives;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Clase abstracta base para todos los objetos geométricos renderizables.
 *
 * <p>Todos los objetos renderizables deben extender esta clase e implementar los métodos de
 * intersección y cálculo de normales.
 *
 * @author Cristopher Carrada
 */
public abstract class Primitive {
  protected final String name;
  protected final String materialId;

  /**
   * Construye un primitivo.
   *
   * @param name nombre del primitivo
   * @param materialId ID del material asociado
   */
  protected Primitive(String name, String materialId) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (materialId == null || materialId.trim().isEmpty()) {
      throw new IllegalArgumentException("Material ID cannot be null or empty");
    }
    this.name = name;
    this.materialId = materialId;
  }

  public String getName() {
    return name;
  }

  public String getMaterialId() {
    return materialId;
  }

  /**
   * Calcula la intersección del rayo con este primitivo.
   *
   * @param ray el rayo a intersectar
   * @return Optional con la distancia t si hay intersección, vacío si no hay
   */
  public abstract Optional<Double> intersect(Ray ray);

  /**
   * Calcula el vector normal en un punto de la superficie.
   *
   * @param point punto en la superficie del primitivo
   * @return vector normal normalizado en ese punto
   */
  public abstract Vector3D getNormalAt(Vector3D point);
}
