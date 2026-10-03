package ludo.domain.model;

public class MysteryCellUpdate {

    public enum Type {
        NONE,
        SPAWNED,
        RELOCATED
    }

    private final Type type;
    private final Integer position;

    private MysteryCellUpdate(Type type, Integer position) {
        if (type == null) {
            throw new IllegalArgumentException("Mystery Cell update type cannot be null.");
        }

        if (type != Type.NONE && position == null) {
            throw new IllegalArgumentException("Mystery Cell position is required for this update.");
        }

        this.type = type;
        this.position = position;
    }

    public static MysteryCellUpdate none() {
        return new MysteryCellUpdate(Type.NONE, null);
    }

    public static MysteryCellUpdate spawned(int position) {
        return new MysteryCellUpdate(Type.SPAWNED, position);
    }

    public static MysteryCellUpdate relocated(int position) {
        return new MysteryCellUpdate(Type.RELOCATED, position);
    }

    public Type getType() {
        return type;
    }

    public Integer getPosition() {
        return position;
    }

    public boolean hasChanged() {
        return type != Type.NONE;
    }
}