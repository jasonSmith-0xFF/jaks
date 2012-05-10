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
package com.googlecode.jsaf.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Aggregates an {@link InputStream}, overriding the {@link #close()} method to prevent
 * the consumer from explicitly closing the wrapped stream.
 * @author Jason Smith
 */
public class NonClosingInputStream extends InputStream
{
	private final InputStream in;
	
	/**
	 * Constructor.
	 * @param in The {@link InputStream} to wrap.
	 */
	public NonClosingInputStream(final InputStream in)
	{
		this.in = in;
	}
	
	@Override
	public int read() throws IOException 
	{
		return in.read();
	}

	@Override
	public int available() throws IOException 
	{
		return in.available();
	}

	@Override
	public void close() throws IOException 
	{
	}

	@Override
	public void mark(final int readlimit) 
	{
		in.mark(readlimit);
	}

	@Override
	public boolean markSupported() 
	{
		return in.markSupported();
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException 
	{
		return in.read(b, off, len);
	}

	@Override
	public int read(final byte[] b) throws IOException 
	{
		return in.read(b);
	}

	@Override
	public void reset() throws IOException 
	{
		in.reset();
	}

	@Override
	public long skip(final long n) throws IOException 
	{
		return in.skip(n);
	}
}
