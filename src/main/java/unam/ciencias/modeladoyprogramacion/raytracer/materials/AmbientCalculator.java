package unam.ciencias.modeladoyprogramacion.raytracer.materials;

import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Calculadora de componente de iluminación ambiente (Phong Shading Model).
 *
 * <p>Esta clase encapsula el cálculo del componente ambiente del modelo de iluminación de Phong,
 * siguiendo el principio de Responsabilidad Única (SRP). El componente ambiente simula la luz
 * difusa omnidireccional que ilumina todos los objetos uniformemente.
 *
 * <p><b>Fórmula:</b> {@code ambient = ambientLight ⊙ materialColor}
 *
 * <p>Donde ⊙ denota la multiplicación componente a componente (Hadamard product).
 *
 * <p><b>Ejemplo de uso:</b>
 * <pre>{@code
 * AmbientCalculator calculator = new AmbientCalculator();
 * Vector3D ambient = new Vector3D(0.1, 0.1, 0.1);  // Luz ambiente tenue
 * Vector3D color = new Vector3D(1.0, 0.0, 0.0);    // Material rojo
 * Vector3D result = calculator.calculate(ambient, color);
 * // result = (0.1, 0.0, 0.0) → rojo oscuro
 * }</pre>
 *
 * @author Cristopher Carrada
 * @see PhongMaterialStrategy
 */
public final class AmbientCalculator {

  /**
   * Calcula el componente de iluminación ambiente.
   *
   * <p>La iluminación ambiente es constante para toda la escena y no depende de la posición o
   * dirección de la luz. Representa la luz difusa que rebota múltiples veces en el ambiente.
   *
   * @param ambientLight intensidad de luz ambiente (RGB en rango [0, 1])
   * @param materialColor color del material (RGB en rango [0, 1])
   * @return color resultante del componente ambiente (producto componente a componente)
   * @throws IllegalArgumentException si algún parámetro es null
   */
  public Vector3D calculate(Vector3D ambientLight, Vector3D materialColor) {
    if (ambientLight == null || materialColor == null) {
      throw new IllegalArgumentException("Ambient light and material color cannot be null");
    }

    return new Vector3D(
        ambientLight.getX() * materialColor.getX(),
        ambientLight.getY() * materialColor.getY(),
        ambientLight.getZ() * materialColor.getZ());
  }
}
