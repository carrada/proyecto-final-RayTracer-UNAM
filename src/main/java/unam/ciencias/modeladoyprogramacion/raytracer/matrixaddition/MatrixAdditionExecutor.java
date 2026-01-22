package unam.ciencias.modeladoyprogramacion.raytracer.matrixaddition;

import unam.ciencias.modeladoyprogramacion.raytracer.CLIOperationExecutor;
import unam.ciencias.modeladoyprogramacion.raytracer.CLIOptions;
import unam.ciencias.modeladoyprogramacion.raytracer.Matrix;
import unam.ciencias.modeladoyprogramacion.raytracer.shared.CLIInputReader;
import unam.ciencias.modeladoyprogramacion.raytracer.shared.CLIOutputFormatter;

/**
 * Ejecutor para realizar la operación de suma de matrices en un contexto CLI.
 * Lee las matrices de entrada, realiza la suma usando un enfoque multihilo,
 * y muestra el resultado.
 */
public final class MatrixAdditionExecutor implements CLIOperationExecutor {
    private CLIInputReader<MatrixAdditionInput> inputReader;
    private CLIOutputFormatter<Matrix<Integer>> outputFormatter;
    private MatrixAddition<Integer> matrixAdder;

    public MatrixAdditionExecutor(int numberOfThreads) {
        // NOTA: las dependencias deberían inyectarse usando un framework DI
        // o al menos manualmente para mejor testeabilidad, pero por simplicidad
        // las instanciamos directamente aquí. Si escribimos código así
        // no podemos hacer pruebas unitarias correctamente ya que las dependencias están hardcodeadas
        // y no podemos mockearlas.
        // Dado que esta clase no tiene lógica, la dejaremos así por ahora.
        this.inputReader = new MatrixAdditionInputReaderImpl();
        this.outputFormatter = new MatrixAdditionOutputFormatterImpl<>();
        this.matrixAdder = new MultiThreadedMatrixAddition<>(
                numberOfThreads, (a, b) -> a + b);
    }
    
    @Override
    public void execute(CLIOptions options) {
        // NOTA: No hay lógica que probar aquí, ya que toda la lógica se delega
        // a las dependencias, este método solo orquesta las llamadas.

        // Leer entrada desde stdin
        var matrixAdditionInput = inputReader.read(System.in);

        // Realizar suma de matrices
        var result = matrixAdder.add(matrixAdditionInput.matrixA(),
                                     matrixAdditionInput.matrixB());

        // Formatear e imprimir salida a stdout
        System.out.print(outputFormatter.format(result));
    }
}
