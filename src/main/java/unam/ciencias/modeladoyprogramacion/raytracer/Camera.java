package unam.ciencias.modeladoyprogramacion.raytracer;

/**
 * Representa la cámara virtual desde la cual se observa la escena.
 *
 * <p>Define la posición, orientación y parámetros de proyección de la vista.
 *
 * @author Cristopher Carrada
 */
public final class Camera {
  private final Vector3D position;
  private final Vector3D direction;
  private final Vector3D up;
  private final double fov;
  private final double focalDistance;

  /**
   * Construye una cámara (constructor público para retrocompatibilidad).
   *
   * @param position posición de la cámara
   * @param direction dirección hacia donde mira (será normalizada)
   * @param up vector "arriba" de la cámara (será normalizada)
   * @param fov campo de visión en grados
   * @param focalDistance distancia focal
   * @deprecated Usar {@link Builder} en su lugar
   */
  @Deprecated
  public Camera(Vector3D position, Vector3D direction, Vector3D up, double fov, double focalDistance) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (direction == null) {
      throw new IllegalArgumentException("Direction cannot be null");
    }
    if (up == null) {
      throw new IllegalArgumentException("Up vector cannot be null");
    }
    if (fov <= 0 || fov >= 180) {
      throw new IllegalArgumentException("FOV must be between 0 and 180 degrees");
    }
    if (focalDistance <= 0) {
      throw new IllegalArgumentException("Focal distance must be positive");
    }

    this.position = position;
    this.direction = direction.normalize();
    this.up = up.normalize();
    this.fov = fov;
    this.focalDistance = focalDistance;
  }

  public Vector3D getPosition() {
    return position;
  }

  public Vector3D getDirection() {
    return direction;
  }

  public Vector3D getUp() {
    return up;
  }

  public double getFov() {
    return fov;
  }

  public double getFocalDistance() {
    return focalDistance;
  }

  /**
   * Calcula el vector "right" (derecha) de la cámara.
   *
   * @return vector unitario apuntando a la derecha de la cámara
   */
  public Vector3D getRight() {
    return direction.cross(up).normalize();
  }

  @Override
  public String toString() {
    return String.format(
        "Camera[position=%s, direction=%s, fov=%.1f°]", position, direction, fov);
  }

  /** Builder para construir cámaras con interfaz fluida. */
  public static class Builder {
    private Vector3D position = new Vector3D(0, 0, 0);
    private Vector3D direction = new Vector3D(0, 0, -1);
    private Vector3D up = new Vector3D(0, 1, 0);
    private double fov = 60.0;
    private double focalDistance = 1.0;

    public Builder position(Vector3D position) {
      if (position == null) {
        throw new IllegalArgumentException("Position cannot be null");
      }
      this.position = position;
      return this;
    }

    public Builder direction(Vector3D direction) {
      if (direction == null) {
        throw new IllegalArgumentException("Direction cannot be null");
      }
      this.direction = direction;
      return this;
    }

    /**
     * Establece la dirección mirando hacia un punto específico.
     *
     * @param target punto objetivo
     * @return este builder
     */
    public Builder lookAt(Vector3D target) {
      if (target == null) {
        throw new IllegalArgumentException("Target cannot be null");
      }
      this.direction = target.subtract(position).normalize();
      return this;
    }

    public Builder up(Vector3D up) {
      if (up == null) {
        throw new IllegalArgumentException("Up vector cannot be null");
      }
      this.up = up;
      return this;
    }

    public Builder fov(double fov) {
      if (fov <= 0 || fov >= 180) {
        throw new IllegalArgumentException("FOV must be between 0 and 180 degrees");
      }
      this.fov = fov;
      return this;
    }

    public Builder focalDistance(double focalDistance) {
      if (focalDistance <= 0) {
        throw new IllegalArgumentException("Focal distance must be positive");
      }
      this.focalDistance = focalDistance;
      return this;
    }

    public Camera build() {
      return new Camera(position, direction, up, fov, focalDistance);
    }
  }
}
