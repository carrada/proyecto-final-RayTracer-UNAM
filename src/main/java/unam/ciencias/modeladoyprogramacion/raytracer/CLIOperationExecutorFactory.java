package unam.ciencias.modeladoyprogramacion.raytracer;

import java.util.Map;
import java.util.Optional;

import unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition.MatrixAdditionExecutor;

/**
 * Clase Factory para crear instancias de CLIOperationExecutor basadas en CLIOptions.
 *
 * @author Cristopher Carrada
 */
public class CLIOperationExecutorFactory
        implements FactoryMethod<CLIOptions, CLIOperationExecutor> {

    @Override
    public Optional<CLIOperationExecutor> createObj(CLIOptions options) {
        var executorsByOperation = Map.of(
                "matrix-addition", new MatrixAdditionExecutor(options.threads()),
                "ray-tracer", new RayTracerExecutor(options));
        return Optional.ofNullable(
                executorsByOperation.get(options.operation()));
    }
}
