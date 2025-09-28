package md.usm.bookstore.utils;

import java.util.Collection;

public final class Utils {

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
