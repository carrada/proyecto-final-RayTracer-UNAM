package unam.ciencias.modeladoyprogramacion.raytracer;

import unam.ciencias.modeladoyprogramacion.raytracer.materials.MaterialStrategy;
import unam.ciencias.modeladoyprogramacion.raytracer.materials.PhongMaterialStrategy;

/**
 * Representa las propiedades del material de una superficie.
 *
 * <p>Define cómo la superficie interactúa con la luz (color, reflexión, propiedades especulares,
 * etc.). Usa el patrón Strategy para delegar el cálculo de dispersión.
 *
 * @author Cristopher Carrada
 */
public final class Material {
  private final String id;
  private final MaterialStrategy strategy;

  /**
   * Construye un material con estrategia personalizada.
   *
   * @param id identificador único del material
   * @param strategy estrategia de cálculo del material
   */
  public Material(String id, MaterialStrategy strategy) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Material ID cannot be null or empty");
    }
    if (strategy == null) {
      throw new IllegalArgumentException("Material strategy cannot be null");
    }
    this.id = id;
    this.strategy = strategy;
  }

  /**
   * Construye un material con Phong por defecto (retrocompatibilidad).
   *
   * @param id identificador único del material
   * @param color color del material (RGB en rango [0, 1])
   * @param diffuseCoefficient coeficiente de reflexión difusa [0, 1]
   * @param specularCoefficient coeficiente de reflexión especular [0, 1]
   * @param specularHardness dureza especular (brillo)
   * @param reflectivity reflectividad del material [0, 1]
   */
  public Material(
      String id,
      Vector3D color,
      double diffuseCoefficient,
      double specularCoefficient,
      double specularHardness,
      double reflectivity) {
    this(
        id,
        new PhongMaterialStrategy(
            id,
            color,
            diffuseCoefficient,
            specularCoefficient,
            specularHardness,
            reflectivity,
            new Vector3D(0.1, 0.1, 0.1)));
  }

  public String getId() {
    return id;
  }

  public MaterialStrategy getStrategy() {
    return strategy;
  }

  public Vector3D getColor() {
    return strategy.getColor();
  }

  public double getDiffuseCoefficient() {
    return strategy.getDiffuseCoefficient();
  }

  public double getSpecularCoefficient() {
    return strategy.getSpecularCoefficient();
  }

  public double getSpecularHardness() {
    return strategy.getSpecularHardness();
  }

  public double getReflectivity() {
    return strategy.getReflectivity();
  }

  @Override
  public String toString() {
    return String.format(
        "Material[id=%s, color=%s, diffuse=%.2f, specular=%.2f]",
        id, getColor(), getDiffuseCoefficient(), getSpecularCoefficient());
  }
}
