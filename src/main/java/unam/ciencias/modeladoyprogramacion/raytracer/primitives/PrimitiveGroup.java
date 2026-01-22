package unam.ciencias.modeladoyprogramacion.raytracer.primitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Grupo de primitivos que se comporta como un solo primitivo.
 *
 * <p>Implementa el patrón Composite (GoF) permitiendo tratar grupos de objetos uniformemente.
 *
 * @author Cristopher Carrada
 */
public final class PrimitiveGroup extends Primitive {
  private final List<Primitive> children;

  /**
   * Construye un grupo de primitivos.
   *
   * @param name nombre del grupo
   * @param materialId ID del material (usado si el grupo necesita material propio)
   */
  public PrimitiveGroup(String name, String materialId) {
    super(name, materialId);
    this.children = new ArrayList<>();
  }

  /**
   * Agrega un primitivo hijo al grupo.
   *
   * @param primitive primitivo a agregar
   */
  public void add(Primitive primitive) {
    if (primitive == null) {
      throw new IllegalArgumentException("Primitive cannot be null");
    }
    children.add(primitive);
  }

  /**
   * Remueve un primitivo hijo del grupo.
   *
   * @param primitive primitivo a remover
   */
  public void remove(Primitive primitive) {
    children.remove(primitive);
  }

  /**
   * Obtiene los hijos del grupo.
   *
   * @return lista de primitivos hijos
   */
  public List<Primitive> getChildren() {
    return new ArrayList<>(children);
  }

  @Override
  public Optional<Double> intersect(Ray ray) {
    // Encontrar la intersección más cercana entre todos los hijos
    return children.stream()
        .map(primitive -> primitive.intersect(ray))
        .flatMap(Optional::stream)
        .min(Double::compareTo);
  }

  @Override
  public Vector3D getNormalAt(Vector3D point) {
    // Para grupos, la normal depende del hijo más cercano
    // Esta implementación es simplificada
    throw new UnsupportedOperationException(
        "PrimitiveGroup does not support getNormalAt directly. "
            + "Use intersection with the child primitive instead.");
  }

  @Override
  public String toString() {
    return String.format("PrimitiveGroup[name=%s, children=%d]", name, children.size());
  }
}
