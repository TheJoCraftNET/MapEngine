package de.pianoman911.mapengine.common.util;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Objects;

public final class ReflectionUtil {

    private static final Unsafe UNSAFE;
    private static final MethodHandles.Lookup TRUSTED_LOOKUP;

    static {
        Unsafe unsafe = null;
        for (Field field : Unsafe.class.getDeclaredFields()) {
            if (field.getType() == Unsafe.class) {
                try {
                    field.trySetAccessible();
                    unsafe = (Unsafe) field.get(null);
                } catch (ReflectiveOperationException exception) {
                    throw new RuntimeException(exception);
                }
                break;
            }
        }
        UNSAFE = Objects.requireNonNull(unsafe, "Can't find unsafe instance");

        try {
            MethodHandles.lookup(); // load class before getting the trusted lookup
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            long lookupFieldOffset = UNSAFE.staticFieldOffset(lookupField);
            TRUSTED_LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(MethodHandles.Lookup.class, lookupFieldOffset);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private ReflectionUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static MethodHandle getConstructor(Class<?> clazz, Class<?>... parameters) throws ReflectiveOperationException {
        return TRUSTED_LOOKUP.findConstructor(clazz, MethodType.methodType(void.class, parameters));
    }

    public static <T, V> void setFinalField(Class<T> clazz, Class<V> type, int offset, T instance, V value) {
        try {
            Field field = lookupField(clazz, type, offset);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Field lookupField(Class<?> clazz, Class<?> type, int offset) {
        int i = 0;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() != type) {
                continue;
            }
            if (i++ == offset) {
                return field;
            }
        }
        throw new IllegalArgumentException("Can't find field " + type + " with offset " + offset + " in " + clazz.getName());
    }
}
