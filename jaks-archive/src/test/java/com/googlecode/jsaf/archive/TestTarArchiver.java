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
package com.googlecode.jsaf.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

import org.apache.tools.bzip2.CBZip2OutputStream;
import org.junit.Test;

import com.googlecode.jsaf.archive.resource.Folder;
import com.googlecode.jsaf.archive.resource.StringResource;

/**
 * Some tests for {@link TarArchiver}.
 * @author Jason Smith
 */
public class TestTarArchiver extends AbstractArchiveTest
{
	/**
	 * Test the ability to create an archive and add some files and a folder to it.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testTarArchiver() throws UnsupportedEncodingException, Exception
	{
		File tar = new File(getTestsFolder(), "archive1.tar");
		try (TarArchiver tarArchiver = new TarArchiver(new FileOutputStream(tar)))
		{
			tarArchiver.add(new StringResource("a.txt", "Text for 'a.txt'."));
			tarArchiver.add(new Folder("folder"));
			tarArchiver.add(new StringResource("folder/b.txt", "Text for 'b.txt'."));
			tarArchiver.add(new StringResource("folder/c.txt", "Text for 'c.txt'."));
		}
		assertTrue("TAR archive file was not created.",
				tar.isFile());
		assertTrue("TAR archive file is empty.",
				tar.length() > 0);
	}
	
	/**
	 * Test the ability to create an archive, compress it with GZip, and add some files and a folder to it.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testTarArchiverWithGZip() throws UnsupportedEncodingException, Exception
	{
		File tar = new File(getTestsFolder(), "archive1.tar.gz");
		try (TarArchiver tarArchiver = new TarArchiver(new GZIPOutputStream(new FileOutputStream(tar))))
		{
			tarArchiver.add(new StringResource("a.txt", "Text for 'a.txt'."));
			tarArchiver.add(new Folder("folder"));
			tarArchiver.add(new StringResource("folder/b.txt", "Text for 'b.txt'."));
			tarArchiver.add(new StringResource("folder/c.txt", "Text for 'c.txt'."));
		}
		assertTrue("TAR.GZ archive file was not created.",
				tar.isFile());
		assertTrue("TAR.GZ archive file is empty.",
				tar.length() > 0);
	}
	
	/**
	 * Test the ability to create an archive, compress it with BZip2, and add some files and a folder to it.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testTarArchiverWithBZip2() throws UnsupportedEncodingException, Exception
	{
		File tar = new File(getTestsFolder(), "archive1.tar.bz2");
		
		try(FileOutputStream fileOut = new FileOutputStream(tar))
		{
			/* To work, first two bytes in stream must be 'BZ'. Not a bug. */
			new PrintStream(fileOut).print("BZ");
			try (TarArchiver tarArchiver = new TarArchiver(new CBZip2OutputStream(fileOut)))
			{
				tarArchiver.add(new StringResource("a.txt", "Text for 'a.txt'."));
				tarArchiver.add(new Folder("folder"));
				tarArchiver.add(new StringResource("folder/b.txt", "Text for 'b.txt'."));
				tarArchiver.add(new StringResource("folder/c.txt", "Text for 'c.txt'."));
			}
		}
		assertTrue("TAR.BZ2 archive file was not created.",
				tar.isFile());
		assertTrue("TAR.BZ2 archive file is empty.",
				tar.length() > 0);
	}
}
