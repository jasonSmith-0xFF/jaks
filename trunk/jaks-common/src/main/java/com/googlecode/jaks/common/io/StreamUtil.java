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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Utility methods for {@link InputStream}s, {@link OutputStream}s, {@link Reader}s, and {@link Writer}s.
 * @author Jason Smith
 */
public final class StreamUtil 
{
	private static final int BUFFER_SIZE = 4096;
	
	/** Literally 'UTF-8'; for use with methods that take a char-set encoding value. */
	public static final String UTF8 = "UTF-8";
	/** Literally 'US-ASCII'; for use with methods that take a char-set encoding value. */
	public static final String US_ASCII = "US-ASCII";
	/** The system-default character set. */
	public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();
	
	/**
	 * Private constructor.
	 */
	private StreamUtil()
	{
	}
	
	/**
	 * Transfer all the characters from the {@code source} to the {@code target}, calling the {@code close()}
	 * method for both on exit. Source is wrapped in a {@link BufferedReader};
	 * target is wrapped in a {@link BufferedWriter}.
	 * @param source The source character stream.
	 * @param target The target character stream.
	 * @throws IOException See {@link IOException}.
	 * @see NonClosingReader
	 * @see NonClosingWriter
	 */
	public static void transfer(final Reader source, final Writer target) throws IOException
	{
		try
		(
			Reader in = new BufferedReader(source, BUFFER_SIZE);
			Writer out = new BufferedWriter(target, BUFFER_SIZE);
		)
		{
			final char[] cbuf = new char[BUFFER_SIZE];
			for(;;)
			{
				final int len = in.read(cbuf);
				if(len == -1)
				{
					break;
				}
				out.write(cbuf, 0, len);
			}
		}
	}
	
	/**
	 * Transfer all the bytes from the {@code source} to the {@code target}, calling the {@code close()}
	 * method for both on exit. Source is wrapped in a {@link BufferedInputStream};
	 * target is wrapped in a {@link BufferedOutputStream}.
	 * @param source The source byte stream.
	 * @param target The target byte stream.
	 * @throws IOException See {@link IOException}.
	 * @see NonClosingInputStream
	 * @see NonClosingOutputStream
	 */
	public static void transfer(final InputStream source, final OutputStream target) throws IOException
	{
		try
		(
			final InputStream in = new BufferedInputStream(source, BUFFER_SIZE);
			final OutputStream out = new BufferedOutputStream(target, BUFFER_SIZE);
		)
		{
			final byte[] buf = new byte[BUFFER_SIZE];
			for(;;)
			{
				final int len = in.read(buf);
				if(len == -1)
				{
					break;
				}
				out.write(buf, 0, len);
			}
		}
	}
	
	/**
	 * Write the source text string to the target writer.
	 * @param source The source text.
	 * @param target The target writer. 
	 * @throws IOException See {@link #transfer(Reader, Writer)}.
	 */
	public static void write(final String text, final Writer target) throws IOException
	{
		transfer(new StringReader(text), target);
	}

	/**
	 * Write the source text string to the target output stream.
	 * @param source The source text.
	 * @param target The target output stream.
	 * @param charset The character-encoding of the data. 
	 * @throws IOException See {@link #transfer(InputStream, OutputStream)}.
	 */
	public static void write(final String text, final OutputStream target, final String charset) throws IOException
	{
		transfer(new ByteArrayInputStream(text.getBytes(charset==null?StreamUtil.DEFAULT_CHARSET:charset)), target);
	}
}
