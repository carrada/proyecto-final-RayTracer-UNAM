package unam.ciencias.modeladoyprogramacion.raytracer.observers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Tests for RenderProgressListener interface and its contract. */
class RenderProgressListenerTest {

  @Test
  void implementationCanBeCreated() {
    RenderProgressListener listener = new TestProgressListener();
    assertNotNull(listener);
  }

  @Test
  void onProgressUpdate_canBeCalled() {
    TestProgressListener listener = new TestProgressListener();
    listener.onProgressUpdate(50, 100);
    assertEquals(50, listener.lastPixelsRendered);
    assertEquals(100, listener.lastTotalPixels);
  }

  @Test
  void onTileCompleted_canBeCalled() {
    TestProgressListener listener = new TestProgressListener();
    listener.onTileCompleted(5);
    assertEquals(5, listener.lastTileId);
  }

  @Test
  void onRenderStart_canBeCalled() {
    TestProgressListener listener = new TestProgressListener();
    listener.onRenderStart(1000);
    assertEquals(1000, listener.startTotalPixels);
  }

  @Test
  void onRenderComplete_canBeCalled() {
    TestProgressListener listener = new TestProgressListener();
    listener.onRenderComplete();
    assertTrue(listener.renderCompleted);
  }

  @Test
  void multipleUpdates_trackProgress() {
    TestProgressListener listener = new TestProgressListener();
    listener.onProgressUpdate(25, 100);
    listener.onProgressUpdate(50, 100);
    listener.onProgressUpdate(75, 100);
    listener.onProgressUpdate(100, 100);

    assertEquals(100, listener.lastPixelsRendered);
    assertEquals(100, listener.lastTotalPixels);
  }

  @Test
  void multipleTiles_canBeCompleted() {
    TestProgressListener listener = new TestProgressListener();
    listener.onTileCompleted(0);
    listener.onTileCompleted(1);
    listener.onTileCompleted(2);

    assertEquals(2, listener.lastTileId);
  }

  @Test
  void fullRenderLifecycle_worksCorrectly() {
    TestProgressListener listener = new TestProgressListener();

    listener.onRenderStart(1000);
    assertEquals(1000, listener.startTotalPixels);

    listener.onProgressUpdate(250, 1000);
    assertEquals(250, listener.lastPixelsRendered);

    listener.onTileCompleted(0);
    assertEquals(0, listener.lastTileId);

    listener.onProgressUpdate(500, 1000);
    assertEquals(500, listener.lastPixelsRendered);

    listener.onTileCompleted(1);
    assertEquals(1, listener.lastTileId);

    listener.onProgressUpdate(1000, 1000);
    assertEquals(1000, listener.lastPixelsRendered);

    listener.onRenderComplete();
    assertTrue(listener.renderCompleted);
  }

  @Test
  void progressUpdate_acceptsZeroPixels() {
    TestProgressListener listener = new TestProgressListener();
    assertDoesNotThrow(() -> listener.onProgressUpdate(0, 100));
    assertEquals(0, listener.lastPixelsRendered);
  }

  @Test
  void progressUpdate_acceptsFullProgress() {
    TestProgressListener listener = new TestProgressListener();
    assertDoesNotThrow(() -> listener.onProgressUpdate(100, 100));
    assertEquals(100, listener.lastPixelsRendered);
  }

  /** Test implementation of RenderProgressListener for testing purposes. */
  private static class TestProgressListener implements RenderProgressListener {
    int lastPixelsRendered = -1;
    int lastTotalPixels = -1;
    int lastTileId = -1;
    int startTotalPixels = -1;
    boolean renderCompleted = false;

    @Override
    public void onProgressUpdate(int pixelsRendered, int totalPixels) {
      this.lastPixelsRendered = pixelsRendered;
      this.lastTotalPixels = totalPixels;
    }

    @Override
    public void onTileCompleted(int tileId) {
      this.lastTileId = tileId;
    }

    @Override
    public void onRenderStart(int totalPixels) {
      this.startTotalPixels = totalPixels;
    }

    @Override
    public void onRenderComplete() {
      this.renderCompleted = true;
    }
  }
}
