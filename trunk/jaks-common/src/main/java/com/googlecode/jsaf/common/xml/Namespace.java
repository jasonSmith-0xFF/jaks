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
package com.googlecode.jsaf.common.xml;

/**
 * An XML namespace.
 * @author Jason Smith
 */
public final class Namespace 
{
	/** The namespace URI. */
	private final String uri;
	/** The prefix representing the namespace. */
	private final String prefix;
	
	/**
	 * Constructor.
	 * @param uri The namespace URI.
	 * @param prefix The prefix representing the namespace.
	 */
	public Namespace(final String uri, final String prefix)
	{
		this.uri = uri;
		this.prefix = prefix;
	}
	
	/**
	 * Get the namespace URI.
	 * @return The namespace URI.
	 */
	public String getUri()
	{
		return uri;
	}
	
	/**
	 * Get the prefix.
	 * @return The prefix.
	 */
	public String getPrefix()
	{
		return prefix;
	}
}
