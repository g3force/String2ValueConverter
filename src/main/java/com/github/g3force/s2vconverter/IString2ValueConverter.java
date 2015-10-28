/*
 * *********************************************************
 * Copyright (c) 2009 - 2015, DHBW Mannheim - Tigers Mannheim
 * Project: TIGERS - Sumatra
 * Date: Oct 27, 2015
 * Author(s): Nicolai Ommer <nicolai.ommer@gmail.com>
 * *********************************************************
 */
package com.github.g3force.s2vconverter;

import java.util.List;


/**
 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
 */
public interface IString2ValueConverter
{
	/**
	 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
	 * @param clazz
	 * @return
	 */
	boolean supportedClass(Class<?> clazz);
	
	
	/**
	 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
	 * @param impl
	 * @param value
	 * @return
	 */
	Object parseString(final Class<?> impl, final String value);
	
	
	/**
	 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
	 * @param impl
	 * @param genericsImpls
	 * @param value
	 * @return
	 */
	default Object parseString(
			final Class<?> impl,
			final List<Class<?>> genericsImpls,
			final String value)
	{
		return null;
	}
	
	
	/**
	 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
	 * @param impl
	 * @param value
	 * @return
	 */
	String toString(final Class<?> impl, final Object value);
}
