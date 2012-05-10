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
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.googlecode.jsaf.archive.resource.Folder;
import com.googlecode.jsaf.archive.resource.StringResource;

/**
 * Tests for {@link ZipArchiver}.
 * @author Jason Smith
 */
public class TestZipArchiver extends AbstractArchiveTest
{
	/**
	 * Test the ability to create an archive and add some files and a folder to it.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testTarArchiver() throws UnsupportedEncodingException, Exception
	{
		File zip = new File(getTestsFolder(), "archive1.zip");
		try (ZipArchiver zipArchiver = new ZipArchiver(new FileOutputStream(zip)))
		{
			zipArchiver.add(new StringResource("a.txt", "Text for 'a.txt'."));
			zipArchiver.add(new Folder("folder"));
			zipArchiver.add(new StringResource("folder/b.txt", "Text for 'b.txt'."));
			zipArchiver.add(new StringResource("folder/c.txt", "Text for 'c.txt'."));
		}
		
		assertTrue("ZIP archive file was not created.",
				zip.isFile());
		assertTrue("ZIP archive file is empty.",
				zip.length() > 0);
	}
}
