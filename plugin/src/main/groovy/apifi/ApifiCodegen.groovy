package apifi

import apifi.codegen.*
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CompileStatic
class ApifiCodegen extends DefaultTask {

    @InputFile
    File openApiSpec

    @Input
    String basePackageName

    @OutputDirectory
    File generatedSourceDir

    @TaskAction
    void generate() throws IOException {
        new CodegenIO().execute(openApiSpec, generatedSourceDir, basePackageName)
    }
}
