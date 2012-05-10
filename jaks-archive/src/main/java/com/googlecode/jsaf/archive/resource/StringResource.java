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
package com.googlecode.jsaf.archive.resource;

import java.io.UnsupportedEncodingException;

import com.googlecode.jsaf.common.SquashedException;
import com.googlecode.jsaf.common.io.StreamUtil;

/**
 * A resource with content that is an in-memory UTF-8 encoded text string. This is useful for
 * adding text files (like generated read-me files or indexes) to an archive without having to
 * bother to write them to disk.
 * @author Jason Smith
 */
public class StringResource extends ByteArrayResource
{
	/**
	 * Constructor.
	 * @param path The path.
	 * @param text The resource text.
	 */
	public StringResource(final String path, final String text)
	{
		this(path, System.currentTimeMillis(), text);
	}
	
	/**
	 * Constructor.
	 * @param path The path.
	 * @param lastModified The last modified date.
	 * @param text The resource text.
	 */
	public StringResource(final String path, final long lastModified, final String text)  
	{
		super(path, lastModified, getStringBytes(text));
	}
	
	private static byte[] getStringBytes(final String text)
	{
		try
		{
			return text.getBytes(StreamUtil.UTF8);
		}
		catch(UnsupportedEncodingException e)
		{
			/*
			 * This exception should never be thrown, since Java always understands UTF-8.
			 */
			return SquashedException.raise(e);
		}
	}
}
