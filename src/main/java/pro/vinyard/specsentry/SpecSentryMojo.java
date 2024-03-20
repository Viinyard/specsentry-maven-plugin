package pro.vinyard.specsentry;



import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Scanner for Java source files.
 */
@Mojo(name = "scan", threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class SpecSentryMojo extends AbstractMojo  {
    @Parameter(property = "sources", defaultValue = "${project.build.sourceDirectory}")
    private File[] sources;

    public void execute() throws MojoExecutionException {
        getLog().warn("Scanning Java files in " + Arrays.toString(sources) );
        for (File source : sources) {
            scanDirectory(source);
        }
    }

    private void scanDirectory(File sourceDirectory) throws MojoExecutionException {
        try (Stream<Path> paths = Files.walk(Paths.get(sourceDirectory.getPath()))) {
            List<File> javaFiles = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();

            for (File javaFile : javaFiles) {
                ParseResult<CompilationUnit> parseResult = new JavaParser().parse(javaFile);
                if (parseResult.isSuccessful()) {
                    CompilationUnit cu = parseResult.getResult().get();
                    List<Comment> comments = cu.getAllComments();
                    for (Comment comment : comments) {
                        getLog().info("Comment in file " + javaFile.getName() + ": " + comment.getContent());
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error while reading source files", e);
        }
    }
}
