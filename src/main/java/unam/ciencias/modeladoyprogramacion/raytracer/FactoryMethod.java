package unam.ciencias.modeladoyprogramacion.raytracer;

import java.util.Optional;

/**
 * Interfaz del patrón Factory Method.
 *
 * @param <S> Tipo de origen
 * @param <T> Tipo destino
 * @author Cristopher Carrada
 */
public interface FactoryMethod<S,T> {
    Optional<T> createObj(S type);
}
