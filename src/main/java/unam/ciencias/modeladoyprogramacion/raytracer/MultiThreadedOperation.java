package unam.ciencias.modeladoyprogramacion.raytracer;

import java.util.List;

/**
 * Clase base para operaciones multi-hilo.
 *
 * @author Cristopher Carrada
 */
public abstract class MultiThreadedOperation {
  protected final int threads;

  public MultiThreadedOperation() {
    this.threads = 1;
  }

  public MultiThreadedOperation(int threads) {
    this.threads = threads;
  }

  protected void runAndWaitForThreads(List<Thread> threads)
    throws InterruptedException {
    threads.forEach(Thread::start);
    for (Thread t : threads) {
      t.join();
    }
  }
}
