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
package com.googlecode.jaks.maven;

import static com.googlecode.jaks.common.util.StringUtil.isEmpty;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.bzip2.CBZip2OutputStream;

import com.googlecode.jaks.archive.ArchiveUtil;
import com.googlecode.jaks.archive.FileSystemArchiver;
import com.googlecode.jaks.archive.TarArchiver;
import com.googlecode.jaks.archive.ZipArchiver;
import com.googlecode.jaks.archive.resource.AbstractNode;
import com.googlecode.jaks.archive.resource.FileResource;
import com.googlecode.jaks.archive.resource.Folder;

/**
 * Assemble a Java application into an archive.
 * @author Jason Smith
 * @requiresDependencyResolution compile
 * @goal assemble
 * @phase package
 */
public class AssembleMojo extends AbstractJaksMojo
{
	/**
	 * For the ZIP archive (if specified in {@link #atypes}, 0 for no compression, 1 for fastest, 9 for smallest.
	 * 2 through 8 are a compromise between fastest and smallest.
	 * <br/><strong>Alias</strong>: <tt>zip-compression</tt>
	 * <br/><strong>Default</strong>: 1
	 * @parameter alias="zip-compression"
	 */
	protected int zipCompression = 1;
	
	/**
	 * Types of archives to create. Any of <code>zip</code>, 
	 * <code>tar</code>, <code>tar.gz</code>, <code>tar.bz2</code>.
	 * <br/><strong>Default</strong>: <tt>["zip", "tar.gz"]</tt>
	 * @parameter 
	 */
	protected List<String> types = Arrays.asList("zip", "tar.gz");
	
	/**
	 * The source application folder. Resources in this folder are added to the 
	 * assembled archives.
	 * <br/><strong>Alias</strong>: <tt>zip-compression</tt>
	 * @parameter alias="app-folder" expression="${project.basedir}/src/main/app"
	 */
	protected File appFolder;
	
	/**
	 * File-system target. The files from the assembly are copied to this location for use
	 * in integration testing and development.
	 * <br/><strong>Alias</strong>: <tt>file-system-target-folder</tt>
	 * @parameter alias="file-system-target-folder" expression="${project.build.directory}/installation"
	 */
	protected File fileSystemTargetFolder;

	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			final Set<AbstractNode> nodes = new TreeSet<AbstractNode>();
			
			if(appFolder.isDirectory())
			{
				nodes.addAll(ArchiveUtil.walkFolder(appFolder.getCanonicalFile()));
			}
			
			nodes.addAll(ArchiveUtil.walkFolder(getGeneratedAppFolder()));
			
			nodes.add(new Folder(libFolder));
			for(final Artifact artifact : getCompileArtifacts())
			{
				final String classifier = artifact.getClassifier();
				nodes.add(
						new FileResource(
								libFolder + "/" + 
								artifact.getArtifactId() + 
								"-" + artifact.getVersion() + 
								(isEmpty(classifier) ? "" : "-"+classifier) +
								".jar", artifact.getFile()));
			}
			
			for(final String type : new LinkedHashSet<String>(types))
			{
				final File archive = createArchive(nodes, type);
				getLog().info("Created: " + archive.getName());
			}
			
			if(fileSystemTargetFolder != null && fileSystemTargetFolder.getParentFile().isDirectory())
			{
				fileSystemTargetFolder.mkdirs();
				try(final FileSystemArchiver archiver = new FileSystemArchiver(fileSystemTargetFolder))
				{
					archiver.addAll(nodes);
				}
			}
		}
		catch(final Exception e)
		{
			throw new MojoExecutionException("I've lost me mojo! " + e.toString(), e);
		}
	}
	
	/**
	 * Create an archive from the nodes.
	 * @param nodes The resources and folders that go into the archive.
	 * @param type The type of archive. One of <tt>zip</tt>, <tt>tar</tt>, <tt>tar.gz</tt>, or <tt>tar.bz2</tt>.
	 * @return The file that was created.
	 * @throws Exception See {@link Exception}.
	 */
	protected File createArchive(final Collection<AbstractNode> nodes, final String type) throws Exception
	{
		final File archiveFile = new File(project.getBuild().getDirectory() + 
				"/" + project.getArtifactId() + "-" + project.getVersion() + "." + type.toLowerCase(Locale.getDefault()));
		
		if("zip".equalsIgnoreCase(type))
		{
			try(final ZipArchiver archiver = new ZipArchiver(new FileOutputStream(archiveFile), 0))
			{
				archiver.addAll(nodes);
			}
		}
		else if("tar".equalsIgnoreCase(type))
		{
			try(final TarArchiver archiver = new TarArchiver(new FileOutputStream(archiveFile)))
			{
				archiver.addAll(nodes);
			}
		}
		else if("tar.gz".equalsIgnoreCase(type))
		{
			try(final TarArchiver archiver = new TarArchiver(new GZIPOutputStream(new FileOutputStream(archiveFile))))
			{
				archiver.addAll(nodes);
			}
		}
		else if("tar.bz2".equalsIgnoreCase(type))
		{
			try(final FileOutputStream out = new FileOutputStream(archiveFile))
			{
				/*
				 * Have to start the stream with BZ for BZip2 to work.
				 */
				new PrintStream(out).print("BZ");
				try(final TarArchiver archiver = new TarArchiver(new CBZip2OutputStream(out)))
				{
					archiver.addAll(nodes);
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Archive type not supported: " + type);
		}
		
		return archiveFile;
	}
}
