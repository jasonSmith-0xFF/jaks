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
import java.io.Writer;

/**
 * Aggregates a {@link Writer}, overriding the {@link #close()} method to prevent
 * the consumer from explicitly closing the wrapped stream (it calls {@link #flush()} instead).
 * @author Jason Smith
 */
public class NonClosingWriter extends Writer
{
	private final Writer out;
	
	/**
	 * Constructor.
	 * @param out The {@link Writer} to wrap.
	 */
	public NonClosingWriter(final Writer out)
	{
		this.out = out;
	}
	
	@Override
	public Writer append(final char c) throws IOException 
	{
		return out.append(c);
	}

	@Override
	public Writer append(final CharSequence csq, final int start, final int end) throws IOException 
	{
		return out.append(csq, start, end);
	}

	@Override
	public Writer append(final CharSequence csq) throws IOException 
	{
		return out.append(csq);
	}

	@Override
	public void close() throws IOException 
	{
		out.flush();
	}

	@Override
	public void flush() throws IOException 
	{
		out.flush();
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException 
	{
		out.write(cbuf, off, len);
	}

	@Override
	public void write(final char[] cbuf) throws IOException 
	{
		out.write(cbuf);
	}

	@Override
	public void write(final int c) throws IOException 
	{
		out.write(c);
	}

	@Override
	public void write(final String str, final int off, final int len) throws IOException 
	{
		out.write(str, off, len);
	}

	@Override
	public void write(final String str) throws IOException 
	{
		out.write(str);
	}
}
