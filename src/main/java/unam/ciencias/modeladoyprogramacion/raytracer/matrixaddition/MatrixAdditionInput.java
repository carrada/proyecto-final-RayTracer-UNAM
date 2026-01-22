package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;

/**
 * Objeto de entrada para la operación de suma de matrices.
 *
 * <p>Utiliza Java 21 record para inmutabilidad automática y métodos generados
 * (equals, hashCode, toString).
 *
 * @param matrixA primera matriz a sumar
 * @param matrixB segunda matriz a sumar
 * @author Cristopher Carrada
 */
public record MatrixAdditionInput(Matrix<Integer> matrixA, Matrix<Integer> matrixB) {}
