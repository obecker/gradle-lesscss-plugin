/**
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

    private static final String LESS_PATH = 'less-rhino-1.3.3.js'
    private static final String TMP_DIR = "tmp${File.separator}js"

    /**
     * The property <code>sourceDir</code> enables the incremental build. Its value is the base directory of the source files tree
     * (see {@link LessExtension#source}), that means all contained files will be accounted for the incremental build.
     * Moreover, it will be used to determine the destination subdirectory (in case the compiled LESS files are in subdirectories under
     * the base directory).
     */
    @InputFiles
    @SkipWhenEmpty
    File getSourceDir() {
        if (project.lesscss.source == null) {
            throw new InvalidUserDataException("missing property source for lesscss")
        }
        project.lesscss.source.dir
    }

    /**
     * The property <code>destDir</code> represents the directory object defined in {@link LessExtension#dest}
     */
    @OutputDirectory
    File getDestDir() {
        if (project.lesscss.dest == null) {
            throw new InvalidUserDataException("missing property dest for lesscss")
        }
        project.file(project.lesscss.dest)
    }


    @TaskAction
    def run() {
        final File lessFile = ResourceUtil.extractFileToDirectory(new File(project.buildDir, TMP_DIR), LESS_PATH)
        final RhinoExec rhino = new RhinoExec(project)

        String sourceDirPath = getSourceDir().canonicalPath
        File destDir = getDestDir()
        project.lesscss.source.each { lessSource ->
            def sourcePath = lessSource.canonicalPath
            final List<String> args = [lessFile.canonicalPath, sourcePath]
            if (project.lesscss.compress) {
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
