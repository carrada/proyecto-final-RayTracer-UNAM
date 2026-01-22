package unam.ciencias.modeladoyprogramacion.raytracer.primitives;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unam.ciencias.modeladoyprogramacion.raytracer.Ray;
import unam.ciencias.modeladoyprogramacion.raytracer.Vector3D;

/**
 * Tests unitarios para PrimitiveGroup (Composite Pattern).
 *
 * @author Cristopher Carrada
 */
class PrimitiveGroupTest {
  private PrimitiveGroup group;
  private Sphere sphere1;
  private Sphere sphere2;

  @BeforeEach
  void setUp() {
    group = new PrimitiveGroup("group1", "material1");
    sphere1 = new Sphere("sphere1", "mat1", new Vector3D(0, 0, 0), 1.0);
    sphere2 = new Sphere("sphere2", "mat2", new Vector3D(3, 0, 0), 1.0);
  }

  @Test
  void constructor_createsEmptyGroup() {
    assertEquals("group1", group.getName());
    assertEquals("material1", group.getMaterialId());
    assertTrue(group.getChildren().isEmpty());
  }

  @Test
  void add_addsPrimitiveToGroup() {
    group.add(sphere1);

    assertEquals(1, group.getChildren().size());
    assertTrue(group.getChildren().contains(sphere1));
  }

  @Test
  void add_throwsOnNull() {
    assertThrows(IllegalArgumentException.class, () -> group.add(null));
  }

  @Test
  void remove_removesPrimitive() {
    group.add(sphere1);
    group.add(sphere2);

    group.remove(sphere1);

    assertEquals(1, group.getChildren().size());
    assertFalse(group.getChildren().contains(sphere1));
  }

  @Test
  void remove_doesNothingIfNotPresent() {
    group.add(sphere1);
    int sizeBefore = group.getChildren().size();

    group.remove(sphere2);

    assertEquals(sizeBefore, group.getChildren().size());
    assertTrue(group.getChildren().contains(sphere1));
  }

  @Test
  void getChildren_returnsDefensiveCopy() {
    group.add(sphere1);

    List<Primitive> children = group.getChildren();
    children.clear(); // Intentar modificar la copia

    // El grupo original no debe modificarse
    assertEquals(1, group.getChildren().size());
  }

  @Test
  void intersect_withEmptyGroup_returnsEmpty() {
    Ray ray = new Ray(new Vector3D(0, 0, -5), new Vector3D(0, 0, 1));

    Optional<Double> result = group.intersect(ray);

    assertTrue(result.isEmpty());
  }

  @Test
  void intersect_withOneChild_returnsChildIntersection() {
    group.add(sphere1);
    Ray ray = new Ray(new Vector3D(0, 0, -5), new Vector3D(0, 0, 1));

    Optional<Double> result = group.intersect(ray);

    assertTrue(result.isPresent());
    assertEquals(4.0, result.get(), 0.001); // Esfera en origen, radio 1, desde z=-5
  }

  @Test
  void intersect_withMultipleChildren_returnsClosestIntersection() {
    group.add(sphere1); // En origen
    group.add(sphere2); // En x=3

    Ray ray = new Ray(new Vector3D(0, 0, -5), new Vector3D(0, 0, 1));

    Optional<Double> result = group.intersect(ray);

    assertTrue(result.isPresent());
    // Debe retornar la intersección con sphere1 (más cercana)
    assertEquals(4.0, result.get(), 0.001);
  }

  @Test
  void intersect_withNoHits_returnsEmpty() {
    group.add(sphere1);
    group.add(sphere2);

    // Rayo que no golpea ninguna esfera
    Ray ray = new Ray(new Vector3D(0, 10, -5), new Vector3D(0, 0, 1));

    Optional<Double> result = group.intersect(ray);

    assertTrue(result.isEmpty());
  }

  @Test
  void getNormalAt_throwsUnsupportedOperationException() {
    Vector3D point = new Vector3D(0, 0, 0);

    assertThrows(UnsupportedOperationException.class, () -> group.getNormalAt(point));
  }

  @Test
  void add_allowsNestedGroups() {
    PrimitiveGroup nestedGroup = new PrimitiveGroup("nested", "mat1");
    nestedGroup.add(sphere1);

    group.add(nestedGroup);

    assertEquals(1, group.getChildren().size());
    assertTrue(group.getChildren().contains(nestedGroup));
  }

  @Test
  void intersect_withNestedGroups_findsIntersection() {
    PrimitiveGroup nestedGroup = new PrimitiveGroup("nested", "mat1");
    nestedGroup.add(sphere1);
    group.add(nestedGroup);

    Ray ray = new Ray(new Vector3D(0, 0, -5), new Vector3D(0, 0, 1));

    Optional<Double> result = group.intersect(ray);

    assertTrue(result.isPresent());
    assertEquals(4.0, result.get(), 0.001);
  }
}
