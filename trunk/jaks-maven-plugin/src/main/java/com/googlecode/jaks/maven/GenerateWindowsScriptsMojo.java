package com.googlecode.jaks.maven;

import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Generate Windows BAT launch scripts for Jaks commands.
 * @author Jason Smith
 * @requiresDependencyResolution compile
 * @goal generate-windows-scripts
 * @phase package
 */
public class GenerateWindowsScriptsMojo extends AbstractGenerateScriptsMojo
{
	@Override
	protected String getScript(final String command, final Class<?> commandClass) throws Exception 
	{
		return "@echo off \r\n" +
				"java.exe cp " + getClassPath() + " " + 
				"-Djava.awt.headless=true " +
				"-Djava.net.preferIPv4Stack=true " + 
				"-XX:-OmitStackTraceInFastThrow " + 
				quote("-Dmaven.groupId=" + project.getGroupId()) + " " + 
				quote("-Dmaven.artifactId=" + project.getArtifactId()) + " " +
				quote("-Dmaven.version=" + project.getVersion()) + " " +
				"-Djaks.ui.cols=0 " + 
				quote("-Djaks.launch.script=%~f0") + " " +
				quote("-Djaks.command.class=" + commandClass.getName()) + " " +
				launcher.getName() + " " +
				"%* \r\n" +
				"SET errlev=%ERRORLEVEL% \r\n" +
				"cmd /C exit /B %errlev% \r\n";
	}
	
	@Override
	protected String getClassPath() throws Exception 
	{
		final StringBuilder s = new StringBuilder();
		s.append('"');
		for(final Artifact artifact : getCompileArtifacts())
		{
			if(s.length() > 1)
			{
				s.append(';');
			}
			s.append("%~dp0\\");
			s.append(this.getRelPathFromBinToLib().replace("/", "\\"));
			s.append("\\" + artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar");
		}
		s.append('"');
		return s.toString();
	}
	
	protected String quote(final String text)
	{
		return "\"" + text.replace("\"", "^\"") + "\"";
	}

	@Override
	protected String adjustScriptFileNameToOS(String command) 
	{
		return command + ".bat";
	}
}
