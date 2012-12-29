package com.googlecode.jaks.maven;


/**
 * Generate BASH launch scripts for Jaks commands.
 * @author Jason Smith
 * @requiresDependencyResolution compile
 * @goal generate-bash-scripts
 * @phase package
 */
public class GenerateBashScriptsMojo extends AbstractGenerateScriptsMojo
{

	@Override
	protected String getClassPath() throws Exception 
	{
		/*
		 * This is a simple bash recipe for finding all the JAR files in the library and turning that
		 * into a *nix-compatible class path.
		 */
		return "\"$(find \"$(dirname $0)/" + getRelPathFromBinToLib().replace("\\", "/") + "\" -maxdepth 1 -name '*.jar' | tr '\\n' ':')\"";
	}

	@Override
	protected String getScript(String command, Class<?> commandClass) throws Exception
	{
		return "#!/bin/bash \n" +
				"CLASSPATH= \n" +
				"exec -a " + quote(command) + "\n" +
				"    java \\\n" +
				"    -cp " + getClassPath() + " \\\n" +
				"    -Djava.awt.headless=true \\\n" +
				"    -Djava.net.preferIPv4Stack=true \\\n" +
				"    -XX:-OmitStackTraceInFastThrow \\\n" +
				"    -Dmaven.groupId=" + quote(project.getGroupId()) + " \\\n" +
				"    -Dmaven.artifactId=" + quote(project.getArtifactId()) + " \\\n" +
				"    -Dmaven.version=" + quote(project.getVersion()) + "\\\n" +
				"    -Djaks.ui.cols=$(tput cols 2> /dev/null || echo 0) \\\n" +
				"    -Djaks.launch.script=$(dirname $0)/$(basename $0) \\\n" +
				"    -Djaks.command.class=" + commandClass.getName() + " \\\n" + 
				"    " + launcher.getName() + " \\\n" +
				"    \"$@\"";
	}
	
	protected String quote(final String text)
	{
		return "'" + text.replace("'", "'\\''") + "'";
	}

	@Override
	protected String adjustScriptFileNameToOS(final String command) 
	{
		return command;
	}
}
