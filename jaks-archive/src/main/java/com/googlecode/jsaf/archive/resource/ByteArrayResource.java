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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A resource with content that is an in-memory byte array. This is useful for
 * adding data files (like generated or buffered images) to an archive without having to
 * bother to write them to disk.
 * @author Jason Smith
 */
public class ByteArrayResource extends AbstractResource 
{
	private final byte[] bytes;
	
	/**
	 * Constructor.
	 * @param path The path.
	 * @param lastModified The last-modified date.
	 * @param bytes The resource byte array.
	 */
	public ByteArrayResource(final String path, final byte[] bytes)
	{
		this(path, System.currentTimeMillis(), bytes);
	}
	
	/**
	 * Constructor.
	 * @param path The path.
	 * @param lastModified The last-modified date.
	 * @param bytes The resource byte array.
	 */
	public ByteArrayResource(final String path, final long lastModified, final byte[] bytes) 
	{
		super(path, lastModified);
		this.bytes = bytes;
	}

	@Override
	public InputStream getInputStream() throws IOException 
	{
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public long size() 
	{
		return bytes.length;
	}
}
