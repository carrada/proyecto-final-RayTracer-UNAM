package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;

public interface MatrixAddition<T> {
    Matrix<T> add(Matrix<T> matrixA, Matrix<T> matrixB);
}
