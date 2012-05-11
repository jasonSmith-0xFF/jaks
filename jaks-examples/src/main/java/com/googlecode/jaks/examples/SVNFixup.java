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
package com.googlecode.jaks.examples;

import static com.googlecode.jaks.common.io.Filter.and;
import static com.googlecode.jaks.common.io.Filter.file;
import static com.googlecode.jaks.common.io.Filter.hidden;
import static com.googlecode.jaks.common.io.Filter.mavenAware;
import static com.googlecode.jaks.common.io.Filter.not;
import static com.googlecode.jaks.common.io.Filter.svnAware;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.googlecode.jaks.cli.AbstractCommand;
import com.googlecode.jaks.cli.JaksNonOptionArguments;
import com.googlecode.jaks.common.io.FileUtil;
import com.googlecode.jaks.common.io.SquashedOutputStream;
import com.googlecode.jaks.common.io.StreamUtil;
import com.googlecode.jaks.common.xml.XmlUtil;
import com.googlecode.jaks.system.Subprocess;
import com.googlecode.jaks.system.SubprocessException;

/**
 * When run against one or more Subversion-managed folders, corrects the <tt>svn:mime-type</tt> property
 * for web-ish (html, jpeg, css, etc.) and Maven-repo-ish (md5, pom, jar, etc.) files so that they can 
 * be served directly out of Subversion. This is a good demonstration of running child processes with
 * {@link Subprocess} (the calls to Subversion), basic XML manipulations with {@link XmlUtil}, and file 
 * enumeration with {@link FileUtil} and {@link Filter}.
 * @author Jason Smith
 */
public class SVNFixup extends AbstractCommand
{
	/**
	 * A list of files or folders to be fixed-up.
	 */
	@JaksNonOptionArguments
	public List<File> files;
	
	private final XmlUtil xml = new XmlUtil();
	
	@Override
	public void execute() throws Exception 
	{
		if(files.isEmpty())
		{
			throw new IllegalArgumentException("No target folder specified.");
		}
		
		for(final File file : files)
		{
			processSVNFile(file.getCanonicalFile());
		}
	}
	
	/**
	 * Recursively process one Subversion file or folder. The file must be already managed by Subversion.
	 * @param svnFile The target file.
	 * @throws FileNotFoundException The specified file was not found.
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	protected void processSVNFile(final File svnFile) throws FileNotFoundException, SubprocessException, Exception
	{
		if(!svnFile.exists())
		{
			throw new FileNotFoundException(svnFile.getPath());
		}
		
		/*
		 * Make sure entry point is an SVN folder or file.
		 */
		svnInfo(svnFile);
		
		final List<File> files = new ArrayList<File>();
		if(svnFile.isDirectory())
		{
			for(final File file : FileUtil.listFilesFlat(svnFile, and(svnAware(),mavenAware()) ))
			{
				svnAdd(file);
			}
			files.addAll(FileUtil.listFilesDeep(svnFile, and(file(),not(hidden())), and(svnAware(),mavenAware()) ));
		}
		else
		{
			files.add(svnFile);
		}
		
		for(final File file : files)
		{
			fixupMimetype(file);
		}
	}
	
	/**
	 * Inspect the file and correct the mimetype as necessary. This covers various image, text, and binary
	 * formats I need to serve content through GoogleCode's Subversion.
	 * @param file The target file.
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	protected void fixupMimetype(final File file) throws SubprocessException, Exception
	{
		final Map<String,String> mimes = new HashMap<String,String>();
		mimes.put("^.*\\.png$", "image/png");
		mimes.put("^.*\\.jpg$", "image/jpeg");
		mimes.put("^.*\\.gif$", "image/gif");
		mimes.put("^.*\\.html?$", "text/html");
		mimes.put("^.*\\.css$", "text/css");
		mimes.put("^.*\\.js$", "text/javascript");
		mimes.put("^.*\\.xml$", "text/xml");
		mimes.put("^.*\\.md5$", "text/plain");
		mimes.put("^.*\\.sha1$", "text/plain");
		mimes.put("^.*\\.pom$", "text/xml");
		mimes.put("^.*\\.jar$", "application/java-archive");
		
		for(final String pattern : mimes.keySet())
		{
			if(file.getName().matches(pattern))
			{
				final String mimetype = mimes.get(pattern);
				if(!mimetype.equals(getMimeType(file)))
				{
					System.out.println("Setting mimetype " + mimetype + " on " + file.getPath());
					setMimeType(file, mimetype);
					//commit(file, "Automated. Set mimetype to '" + mimetype + "'.");
				}
				return;
			}
		}
	}
	
	/**
	 * Gets the Subversion info for a file or folder as an XML document. If the file is not being managed by 
	 * Subversion, throws an exception. Document is generated by <tt>svn info --xml</tt>.
	 * @param file The target file or folder.
	 * @return The info document.
	 * @throws SubprocessException See {@link Subprocess#call(java.io.OutputStream, java.io.OutputStream)}.
	 * @throws Exception See {@link Exception}.
	 */
	protected Document svnInfo(final File file) throws SubprocessException, Exception
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		new Subprocess("svn", "info", "--xml", file.getCanonicalPath()).call(out, new SquashedOutputStream());
		return xml.toDocument(new String(out.toByteArray(), StreamUtil.UTF8));
	}
	
	/**
	 * Get the value of the <tt>svn:mime-type</tt> property on a Subversion-managed file. If no mimetype is set,
	 * returns an empty string.
	 * @param file The target file.
	 * @return The value of the <tt>svn:mime-type</tt> property.
	 * @throws SubprocessException See {@link Subprocess#call(java.io.OutputStream, java.io.OutputStream)}.
	 * @throws Exception See {@link Exception}.
	 */
	protected String getMimeType(final File file) throws SubprocessException, Exception
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		new Subprocess("svn", "propget", "--xml", "svn:mime-type", file.getCanonicalPath()).call(out, new SquashedOutputStream());
		return xml.selectString(xml.toDocument(new String(out.toByteArray(), StreamUtil.UTF8)), "/properties/target/property[@name='svn:mime-type']/text()");
	}
	
	/**
	 * Set the <tt>svn:mime-type</tt> property on a Subversion-managed file.
	 * @param file The target file.
	 * @param mimetype The new mimetype.
	 * @throws SubprocessException See {@link Subprocess#call(java.io.OutputStream, java.io.OutputStream)}.
	 * @throws Exception See {@link Exception}.
	 */
	protected void setMimeType(final File file, final String mimetype) throws SubprocessException, Exception
	{
		new Subprocess("svn", "propset", "svn:mime-type", mimetype, file.getCanonicalPath()).call(new SquashedOutputStream(), new SquashedOutputStream());
	}
	
	/**
	 * Commit the changes in a Subversion file or folder.
	 * @param file The file or folder to commit.
	 * @param message The message to associate with the commit.
	 * @throws SubprocessException See {@link Subprocess#call(java.io.OutputStream, java.io.OutputStream)}.
	 * @throws Exception See {@link Exception}.
	 */
	protected void commit(final File file, final String message) throws SubprocessException, Exception
	{
		new Subprocess("svn", "commit", "--message", message, file.getCanonicalPath()).call(new SquashedOutputStream(), new SquashedOutputStream());
	}
	
	/**
	 * Add a file or folder to Subversion management.
	 * @param file File or folder to add.
	 * @throws SubprocessException See {@link Subprocess#call(java.io.OutputStream, java.io.OutputStream)}.
	 * @throws Exception See {@link Exception}.
	 */
	protected void svnAdd(final File file) throws SubprocessException, Exception
	{
		new Subprocess("svn", "add", "--force", file.getCanonicalPath()).call(new SquashedOutputStream(), new SquashedOutputStream());
	}
}
