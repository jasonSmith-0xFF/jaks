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
package com.googlecode.jaks.common.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Aggregates an {@link Reader}, overriding the {@link #close()} method to prevent
 * the consumer from explicitly closing the wrapped stream.
 * @author Jason Smith
 */
public class NonClosingReader extends Reader
{
	private final Reader in;
	
	/**
	 * Constructor.
	 * @param in The {@link Reader} to wrap.
	 */
	public NonClosingReader(final Reader in)
	{
		this.in = in;
	}
	
	@Override
	public void mark(final int readAheadLimit) throws IOException 
	{
		in.mark(readAheadLimit);
	}

	@Override
	public boolean markSupported() 
	{
		return in.markSupported();
	}

	@Override
	public int read() throws IOException 
	{
		return in.read();
	}

	@Override
	public int read(final char[] cbuf) throws IOException 
	{
		return in.read(cbuf);
	}

	@Override
	public int read(final CharBuffer target) throws IOException 
	{
		return in.read(target);
	}

	@Override
	public boolean ready() throws IOException 
	{
		return in.ready();
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

	@Override
	public void close() throws IOException 
	{
		in.close();
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException 
	{
		return in.read(cbuf, off, len);
	}
}
