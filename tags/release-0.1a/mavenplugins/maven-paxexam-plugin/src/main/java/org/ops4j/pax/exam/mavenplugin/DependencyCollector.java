
package org.ops4j.pax.exam.mavenplugin;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

public interface DependencyCollector {

    public abstract List<Dependency> getDependencies()
            throws MojoExecutionException;

}