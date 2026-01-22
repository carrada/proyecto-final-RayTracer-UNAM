package unam.ciencias.modeladoyprogramacion.raytracer.observers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for ConsoleProgressListener. */
class ConsoleProgressListenerTest {
  private ConsoleProgressListener listener;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @BeforeEach
  void setUp() {
    listener = new ConsoleProgressListener();
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void onRenderStart_printsStartMessage() {
    listener.onRenderStart(1000);
    String output = outputStream.toString();
    assertTrue(output.contains("Starting render: 1000 pixels"));
  }

  @Test
  void onRenderComplete_printsFinalMessage() {
    listener.onRenderComplete();
    String output = outputStream.toString();
    assertTrue(output.contains("Rendering complete: 100%"));
  }

  @Test
  void onProgressUpdate_printsAtTenPercentIntervals() {
    listener.onProgressUpdate(0, 1000);
    listener.onProgressUpdate(50, 1000); // 5% - no debería imprimir
    listener.onProgressUpdate(100, 1000); // 10% - debería imprimir
    listener.onProgressUpdate(150, 1000); // 15% - no debería imprimir
    listener.onProgressUpdate(200, 1000); // 20% - debería imprimir

    String output = outputStream.toString();
    assertTrue(output.contains("Rendering progress: 10%"));
    assertTrue(output.contains("Rendering progress: 20%"));
    assertFalse(output.contains("Rendering progress: 5%"));
    assertFalse(output.contains("Rendering progress: 15%"));
  }

  @Test
  void onProgressUpdate_doesNotRepeatSamePercentage() {
    listener.onProgressUpdate(100, 1000); // 10%
    listener.onProgressUpdate(105, 1000); // 10% again
    listener.onProgressUpdate(109, 1000); // 10% again

    String output = outputStream.toString();
    // Should only print once for 10%
    int count = countOccurrences(output, "Rendering progress: 10%");
    assertEquals(1, count);
  }

  @Test
  void onProgressUpdate_handlesZeroPixels() {
    listener.onProgressUpdate(0, 1000);
    String output = outputStream.toString();
    assertTrue(output.contains("Rendering progress: 0%"));
  }

  @Test
  void onProgressUpdate_handlesFullProgress() {
    listener.onProgressUpdate(1000, 1000);
    String output = outputStream.toString();
    assertTrue(output.contains("Rendering progress: 100%"));
  }

  @Test
  void onProgressUpdate_handlesIntermediateValues() {
    listener.onProgressUpdate(300, 1000); // 30%
    listener.onProgressUpdate(500, 1000); // 50%
    listener.onProgressUpdate(700, 1000); // 70%

    String output = outputStream.toString();
    assertTrue(output.contains("Rendering progress: 30%"));
    assertTrue(output.contains("Rendering progress: 50%"));
    assertTrue(output.contains("Rendering progress: 70%"));
  }

  @Test
  void onTileCompleted_doesNotCrash() {
    assertDoesNotThrow(() -> listener.onTileCompleted(0));
    assertDoesNotThrow(() -> listener.onTileCompleted(42));
  }

  @Test
  void onProgressUpdate_handlesSmallTotalPixels() {
    listener.onProgressUpdate(1, 10); // 10%
    listener.onProgressUpdate(5, 10); // 50%

    String output = outputStream.toString();
    assertTrue(output.contains("Rendering progress: 10%"));
    assertTrue(output.contains("Rendering progress: 50%"));
  }

  @Test
  void onProgressUpdate_handlesLargeNumbers() {
    listener.onProgressUpdate(100000, 1000000); // 10%
    listener.onProgressUpdate(500000, 1000000); // 50%

    String output = outputStream.toString();
    assertTrue(output.contains("Rendering progress: 10%"));
    assertTrue(output.contains("Rendering progress: 50%"));
  }

  @Test
  void fullRenderCycle_printsExpectedMessages() {
    listener.onRenderStart(1000);
    listener.onProgressUpdate(100, 1000); // 10%
    listener.onProgressUpdate(500, 1000); // 50%
    listener.onProgressUpdate(1000, 1000); // 100%
    listener.onRenderComplete();

    String output = outputStream.toString();
    assertTrue(output.contains("Starting render: 1000 pixels"));
    assertTrue(output.contains("Rendering progress: 10%"));
    assertTrue(output.contains("Rendering progress: 50%"));
    assertTrue(output.contains("Rendering progress: 100%"));
    assertTrue(output.contains("Rendering complete: 100%"));
  }

  @Test
  void onProgressUpdate_handlesEdgeCasePercentages() {
    listener.onProgressUpdate(99, 1000); // 9% - no debería imprimir
    listener.onProgressUpdate(100, 1000); // 10% - debería imprimir
    listener.onProgressUpdate(101, 1000); // 10% - no debería repetir

    String output = outputStream.toString();
    assertTrue(output.contains("Rendering progress: 10%"));
    assertFalse(output.contains("Rendering progress: 9%"));
    int count = countOccurrences(output, "Rendering progress: 10%");
    assertEquals(1, count);
  }

  private int countOccurrences(String text, String substring) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(substring, index)) != -1) {
      count++;
      index += substring.length();
    }
    return count;
  }
}
