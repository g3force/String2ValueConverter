/*
 * *********************************************************
 * Copyright (c) 2009 - 2014, DHBW Mannheim - Tigers Mannheim
 * Project: TIGERS - Sumatra
 * Date: Mar 17, 2014
 * Author(s): Nicolai Ommer <nicolai.ommer@gmail.com>
 * *********************************************************
 */
package com.github.g3force.s2vconverter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Convert values of certain important classes from string to value and vice
 * versa.
 * 
 * Override this class to add support for your own classes.
 * 
 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
 */
public class String2ValueConverter {
	private static final Logger log = Logger
			.getLogger(String2ValueConverter.class.getName());

	/**
	 * Create a converter for default java types
	 */
	public String2ValueConverter() {
	}

	/**
	 * Parse given String-value according to implementation of this parameter
	 * 
	 * @param impl
	 * @param value
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object parseString(final Class<?> impl, final String value) {
		if (impl.isArray()) {
			String[] split = value.split(";");
			Object[] arr;
			try {
				arr = (Object[]) Array.newInstance(impl.getComponentType(),
						split.length);

			} catch (ClassCastException err) {
				log.error(
						"Could not create array of type "
								+ impl.getComponentType(), err);
				return new Object[0];
			}
			for (int i = 0; i < split.length; i++) {
				arr[i] = parseString(impl.getComponentType(), split[i]);
			}
			return arr;
		} else if (impl.equals(Integer.class) || impl.equals(Integer.TYPE)) {
			try {
				return Integer.valueOf(value);
			} catch (NumberFormatException err) {
				return 0;
			}
		} else if (impl.equals(Long.class) || impl.equals(Long.TYPE)) {
			try {
				return Long.valueOf(value);
			} catch (NumberFormatException err) {
				log.error("Could not parse value.", err);
				return 0L;
			}
		} else if (impl.equals(Float.class) || impl.equals(Float.TYPE)) {
			try {
				return Float.valueOf(value);
			} catch (NumberFormatException err) {
				log.error("Could not parse value.", err);
				return 0.0f;
			}
		} else if (impl.equals(Double.class) || impl.equals(Double.TYPE)) {
			try {
				return Double.valueOf(value);
			} catch (NumberFormatException err) {
				log.error("Could not parse value.", err);
				return 0.0;
			}
		} else if (impl.equals(Boolean.class) || impl.equals(Boolean.TYPE)) {
			return Boolean.valueOf(value);
		} else if (impl.equals(String.class)) {
			return value;
		} else if (impl.isEnum())
		{
			return Enum.valueOf(((Class<Enum>) impl), value);
		}else {
			throw new IllegalStateException("Unknown implementation: " + impl
					+ ". Can not parse string.");
		}
	}

	/**
	 * Parse given String-value according to implementation of this parameter
	 * 
	 * @param impl
	 * @param genericsImpls
	 * @param value
	 * @return
	 */
	public Object parseString(final Class<?> impl,
			final List<Class<?>> genericsImpls, final String value) {
		if (impl.equals(List.class)) {
			String[] values = value.split(";");
			List<Object> list = new ArrayList<Object>(values.length);
			for (String val : values) {
				list.add(parseString(genericsImpls.get(0), val));
			}
			return list;
		}
		throw new IllegalStateException("Unknown implementation: " + impl
				+ ". Can not parse string.");
	}

	/**
	 * @param impl
	 * @param value
	 * @return
	 */
	public String toString(final Class<?> impl, final Object value) {
		if (impl.equals(Integer.class) || impl.equals(Integer.TYPE)) {
			return Integer.toString((Integer) value);
		} else if (impl.equals(Long.class) || impl.equals(Long.TYPE)) {
			return Long.toString((Long) value);
		} else if (impl.equals(Float.class) || impl.equals(Float.TYPE)) {
			return Float.toString((Float) value);
		} else if (impl.equals(Double.class) || impl.equals(Double.TYPE)) {
			return Double.toString((Double) value);
		} else if (impl.equals(Boolean.class) || impl.equals(Boolean.TYPE)) {
			return Boolean.toString((Boolean) value);
		} else if (impl.isEnum()) {
			return ((Enum<?>) value).name();
		} else if (impl.equals(String.class)) {
			return value.toString();
		} else if (impl.isArray()) {
			Object[] arr = convertPrimitiveArrays(value);
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Object v : arr) {
				if (!first) {
					sb.append(';');
				}
				first = false;
				sb.append(toString(impl.getComponentType(), v));
			}
			return sb.toString();
		} else if (impl.equals(String.class)) {
			// thats easy :)
			return (String) value;
		} else {
			throw new IllegalStateException("Unknown implementation: " + impl
					+ ". Can not parse string.");
		}
	}

	private Object[] convertPrimitiveArrays(final Object arr) {
		if (arr instanceof Object[]) {
			return (Object[]) arr;
		}
		Object[] ret;
		if (arr instanceof int[]) {
			int[] pArr = (int[]) arr;
			ret = new Integer[pArr.length];
			for (int i = 0; i < pArr.length; i++) {
				ret[i] = (int) pArr[i];
			}
		} else if (arr instanceof long[]) {
			long[] pArr = (long[]) arr;
			ret = new Long[pArr.length];
			for (int i = 0; i < pArr.length; i++) {
				ret[i] = (long) pArr[i];
			}
		} else if (arr instanceof float[]) {
			float[] pArr = (float[]) arr;
			ret = new Float[pArr.length];
			for (int i = 0; i < pArr.length; i++) {
				ret[i] = (float) pArr[i];
			}
		} else if (arr instanceof double[]) {
			double[] pArr = (double[]) arr;
			ret = new Double[pArr.length];
			for (int i = 0; i < pArr.length; i++) {
				ret[i] = (double) pArr[i];
			}
		} else if (arr instanceof char[]) {
			char[] pArr = (char[]) arr;
			ret = new Character[pArr.length];
			for (int i = 0; i < pArr.length; i++) {
				ret[i] = (char) pArr[i];
			}
		} else {
			throw new IllegalArgumentException("Unknown array implementation: "
					+ arr);
		}
		return ret;
	}

	/**
	 * @param value
	 *            either a Class or a String
	 * @return
	 */
	public Class<?> getClassFromValue(final Object value) {
		if (value.getClass() == Class.class) {
			return (Class<?>) value;
		}
		String clazz = (String) value;
		if (clazz.equals("int")) {
			return Integer.TYPE;
		}
		if (clazz.equals("long")) {
			return Long.TYPE;
		}
		if (clazz.equals("float")) {
			return Float.TYPE;
		}
		if (clazz.equals("double")) {
			return Double.TYPE;
		}
		if (clazz.equals("boolean")) {
			return Boolean.TYPE;
		}
		try {
			return Class.forName(clazz);
		} catch (ClassNotFoundException err) {
			return String.class;
		}
	}
}
