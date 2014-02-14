package cn.slimsmart.sso.client.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unchecked")
public class ReflectUtil {

	private ReflectUtil() {
		// nothing to do
	}

	public static <T> T newInstance(final String className, final Object... args) {
		return newInstance(ReflectUtil.<T> loadClass(className), args);
	}

	public static <T> Class<T> loadClass(final String className) throws IllegalArgumentException {
		try {
			return (Class<T>) Class.forName(className);
		} catch (final ClassNotFoundException e) {
			throw new IllegalArgumentException(className + " class not found.");
		}
	}

	public static <T> T newInstance(final Class<T> clazz, final Object... args) {
		final Class<?>[] argClasses = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			argClasses[i] = args[i].getClass();
		}
		try {
			return clazz.getConstructor(argClasses).newInstance(args);
		} catch (final Exception e) {
			throw new IllegalArgumentException("Error creating new instance of " + clazz, e);
		}
	}

	public static PropertyDescriptor getPropertyDescriptor(final Class<?> clazz, final String propertyName) {
		try {
			return getPropertyDescriptor(Introspector.getBeanInfo(clazz), propertyName);
		} catch (final IntrospectionException e) {
			throw new RuntimeException("Failed getting bean info for " + clazz, e);
		}
	}

	public static PropertyDescriptor getPropertyDescriptor(final BeanInfo info, final String propertyName) {
		for (int i = 0; i < info.getPropertyDescriptors().length; i++) {
			final PropertyDescriptor pd = info.getPropertyDescriptors()[i];
			if (pd.getName().equals(propertyName)) {
				return pd;
			}
		}
		return null;
	}

	public static void setProperty(final String propertyName, final Object value, final Object target) {
		try {
			setProperty(propertyName, value, target, Introspector.getBeanInfo(target.getClass()));
		} catch (final IntrospectionException e) {
			throw new RuntimeException("Failed getting bean info on target JavaBean " + target, e);
		}
	}

	public static void setProperty(final String propertyName, final Object value, final Object target, final BeanInfo info) {
		try {
			final PropertyDescriptor pd = getPropertyDescriptor(info, propertyName);
			pd.getWriteMethod().invoke(target, value);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException("Error setting property " + propertyName, e.getCause());
		} catch (final Exception e) {
			throw new RuntimeException("Error setting property " + propertyName, e);
		}
	}

}
