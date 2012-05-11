/*
 * Copyright (C) 2012 by Jason Smith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.jaks.common;

/**
 * {@link SquashedException} squashes checked exceptions, for times when 
 * you know the exception is highly unlikely or impossible, and it makes
 * no sense to visibly propagate the exception up. 
 * @author Jason Smith
 */
public class SquashedException extends RuntimeException
{
	private static final long serialVersionUID = 3294614778938195434L;

	/**
	 * Constructor.
	 * @param throwable Wrapped throwable.
	 */
	public SquashedException(final Throwable throwable)
	{
		super(throwable.getMessage(), throwable);
	}
	
	/**
	 * Re-throw the passed throwable. If the throwable is an {@link Error}
	 * or a {@link RuntimeException}, it is rethrown as-is. Otherwise, it is 
	 * wrapped in an {@link SquashedException}.
	 * @param throwable The throwable.
	 * @return Dummy type for Java type checking.
	 */
	public static <T> T raise(final Throwable throwable)
	{
		if(throwable instanceof Error)
		{
			throw (Error)throwable;
		}
		else if(throwable instanceof RuntimeException)
		{
			throw (RuntimeException)throwable;
		}
		else 
		{
			throw new SquashedException(throwable);
		}
	}
}
