package com.googlecode.jaks.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;

public abstract class AbstractJaksMojo extends AbstractMojo
{
	/**
	 * The current project.
	 * @parameter expression="${project}"
	 * @readonly
	 */
	protected final org.apache.maven.project.MavenProject project = null;
	
	/**
	 * The folder containing the JARs.
	 * <br/><strong>Alias</strong>: <tt>lib-folder</tt>
	 * <br/><strong>Default</strong>: <tt>lib</tt>
	 * @parameter alias="lib-folder"
	 */
	protected final String libFolder = "lib";
	
	/**
	 * Items in the generated app folder are added to the archive.
	 * <br/><strong>Alias</strong>: <tt>generated-app-folder</tt>
	 * @parameter alias="lib-folder" expression="${project.build.directory}/generated-app"
	 */	
	protected final File generatedAppFolder = null;
	
	
	/**
	 * Returns the specified app folder, creating it if necessary.
	 * @return The specified app folder.
	 * @throws IOException See {@link IOException}.
	 */
	protected File getGeneratedAppFolder() throws IOException
	{
		project.getBasedir().mkdir();
		new File(project.getBuild().getDirectory()).mkdir();
		
		final File folder = generatedAppFolder.getCanonicalFile();
		if(!folder.getParentFile().isDirectory())
		{
			throw new IOException("Can't create folder: " + folder.getPath());
		}
		folder.mkdir();
		return folder;
	}
	/**
	 * Get the list of compile (JAR) artifacts, including the artifact for the current project if applicable. 
	 * @return The list of compile artifacts.
	 */
	@SuppressWarnings("unchecked")
	protected List<Artifact> getCompileArtifacts()
	{
		final List<Artifact> results = new ArrayList<Artifact>();
		
		if(project.getArtifact() != null && "jar".equals(project.getArtifact().getType()))
		{
			results.add(project.getArtifact());
		}
		
		for(final Artifact artifact : (Set<Artifact>)project.getArtifacts())
		{
			if("jar".equals(artifact.getType()) && !artifact.hasClassifier() && "compile".equals(artifact.getScope()))
			{
				results.add(artifact);
			}
		}
		
		return results;
	}
}
