package it.eldasoft.gene.ws.objectstorage;

public final class Pair<T, X> {

    private final T key;
    private final X value;

    private Pair(T key, X value) {
        this.key = key;
        this.value = value;
    }

    public static <T, X> Pair<T, X> of(T key, X value) {
        return new Pair<>(key, value);
    }

    public T key() {
        return key;
    }
    public X value() {
        return value;
    }
    public T left() {
        return key;
    }
    public X right() {
        return value;
    }

}
