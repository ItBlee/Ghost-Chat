package utils;

public class ObjectUtil {
    @SuppressWarnings("unchecked")
    public static <T> T getFirstFromArray(Object[] array, Class<T> clazz) {
        for (Object o:array) {
            if (clazz.isInstance(o))
                return (T) o;
        }
        throw new IllegalArgumentException();
    }
}
