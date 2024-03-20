package pro.vinyard.specsentry;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class SpecSentryMojoMojoTest extends AbstractMojoTestCase {

    /**
     * @throws Exception if any
     */
    public void testSomething() throws Exception {
        File pom = getTestFile( "src/test/resources/unit/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        SpecSentryMojo myMojo = (SpecSentryMojo) lookupMojo( "scan", pom );
        assertNotNull( myMojo );
        myMojo.execute();
    }
}