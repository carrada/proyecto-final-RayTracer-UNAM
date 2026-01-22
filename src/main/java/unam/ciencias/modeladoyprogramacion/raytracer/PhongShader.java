package unam.ciencias.modeladoyprogramacion.raytracer;

import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.materials.MaterialStrategy;

/**
 * Shader que delega el cálculo de iluminación a las estrategias de materiales.
 *
 * <p>Actúa como coordinador siguiendo el patrón Strategy.
 *
 * @author Cristopher Carrada
 */
public final class PhongShader {
  private final Scene scene;

  /**
   * Construye un shader.
   *
   * @param scene la escena a renderizar
   */
  public PhongShader(Scene scene) {
    if (scene == null) {
      throw new IllegalArgumentException("Scene cannot be null");
    }
    this.scene = scene;
  }

  /**
   * Construye un shader (retrocompatibilidad).
   *
   * @param scene la escena a renderizar
   * @param ambientLight luz ambiente (ignorada, ahora en la estrategia)
   */
  @Deprecated
  public PhongShader(Scene scene, Vector3D ambientLight) {
    this(scene);
  }

  /**
   * Calcula el color en un punto de intersección usando la estrategia del material.
   *
   * @param intersection la intersección a sombrear
   * @param ray el rayo que causó la intersección
   * @param depth profundidad de recursión actual
   * @return color calculado (RGB)
   */
  public Vector3D shade(Intersection intersection, Ray ray, int depth) {
    if (intersection == null) {
      return scene.getBackgroundColor();
    }

    Optional<MaterialStrategy> materialOpt =
        scene.getMaterialStrategy(intersection.getPrimitive().getMaterialId());

    if (materialOpt.isEmpty()) {
      return new Vector3D(1, 0, 1); // Magenta si falta material
    }

    // Delegar al Strategy del material
    return materialOpt.get().scatter(ray, intersection, scene, depth);
  }
}
