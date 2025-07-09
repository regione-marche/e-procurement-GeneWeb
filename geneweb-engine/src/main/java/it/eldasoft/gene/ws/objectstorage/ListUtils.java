package it.eldasoft.gene.ws.objectstorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class ListUtils {

    private ListUtils() { }

    @SafeVarargs
    public static <T> java.util.List<T> of(T...values) {
        return values == null || values.length == 0
                ? Collections.emptyList()
                : new ArrayList<>(Arrays.asList(values));
    }

}
