/*
 *  Copyright 2013 Oliver Becker, ob@obqo.de
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.obqo.gradle.lesscss

import de.obqo.gradle.helper.ResourceUtil
import de.obqo.gradle.helper.RhinoExec

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Implementation class for the <code>lesscss</code> task.
 *
 * @author Oliver Becker
 * @since 17.02.13
 */
class LessTask extends DefaultTask {

    private static final Logger logger = LoggerFactory.getLogger(LessTask.class)

    private static final String LESS_PATH_PREFIX = 'less-rhino-'
    private static final String LESS_PATH_SUFFIX = '.js'
    private static final List<String> LESS_VERSIONS = ['1.1.3', '1.1.5', '1.3.1', '1.3.2','1.3.3', '1.4.0']
    private static final String LESS_DEFAULT_VERSION = LESS_VERSIONS[4]
    private static final String TMP_DIR = "tmp${File.separator}js"

    def source

    @InputFiles
    @SkipWhenEmpty
    FileTree getSourceFiles() {
        if (source == null || source.empty) {
            throw new InvalidUserDataException("missing property source for lesscss")
        }
        if (source instanceof ConfigurableFileTree) {
            source
        } else {
            project.files(source).asFileTree
        }
    }

    /**
     * The property <code>sourceDir</code> enables the incremental build. Its value is either the parent directly of a single source file or the base directory
     * of a source files tree. All contained files will be accounted for the incremental build.
     * Moreover, the <code>sourceDir</code> will be used to determine the destination subdirectory (in case the compiled LESS files are in subdirectories under
     * the base directory).
     */
    @InputDirectory
    @SkipWhenEmpty
    File getSourceDir() {
        FileTree tree = getSourceFiles()
        if (source.metaClass.hasProperty(source, "dir")) { // use source instead of tree for the tests
            source.dir
        } else if (tree.files.size() == 1) {
            tree.singleFile.parentFile
        } else {
            throw new InvalidUserDataException("use fileTree() for compiling multiple less files")
        }
    }

    /** The target directory for the compiled CSS files */
    @Input
    def dest

    @OutputDirectory
    File getDestDir() {
        if (dest == null) {
            throw new InvalidUserDataException("missing property dest for lesscss")
        }
        project.file(dest)
    }

    /** If set to <code>true</code> the resulting CSS will be compressed */
    @Input
    boolean compress = false

    /** Set the version of the less compiler, supported versions <code>1.1.3, 1.1.5, 1.3.1, 1.3.2 ,1.3.3, 1.4.0</code> */
    @Input
    String lessVersion = LESS_DEFAULT_VERSION

    String getLessPath() {
        if( ! (lessVersion in LESS_VERSIONS)){
            throw new InvalidUserDataException("Unsupported less compiler version for property lessVersion. Supported versions $LESS_VERSIONS")
        }
        //build the filepath for the less js file
        StringBuilder lessPathBuilder = new StringBuilder()
        lessPathBuilder << LESS_PATH_PREFIX
        lessPathBuilder << lessVersion
        lessPathBuilder << LESS_PATH_SUFFIX
        lessPathBuilder.toString()
    }

    @TaskAction
    def run() {
        final File lessFile = ResourceUtil.extractFileToDirectory(new File(project.buildDir, TMP_DIR), getLessPath())
        final RhinoExec rhino = new RhinoExec(project)

        String sourceDirPath = getSourceDir().canonicalPath
        logger.info("Base less directory is " + sourceDirPath)
        File destDir = getDestDir()
        getSourceFiles().each { lessSource ->
            def sourcePath = lessSource.canonicalPath
            final List<String> args = [lessFile.canonicalPath, sourcePath]
            if (compress) {
                args.add('-x')
            }

            String relativePath = sourcePath.startsWith(sourceDirPath) ? sourcePath.substring(sourceDirPath.length()) : lessSource.name
            File destFile = new File(destDir, relativePath.replace('.less', '.css'))
            destFile.parentFile.mkdirs()
            logger.info("Compile ${sourcePath} to ${destFile.canonicalPath}");
            rhino.execute(args, [workingDir: project.projectDir.canonicalPath, out: new FileOutputStream(destFile)])
        }
    }

}
