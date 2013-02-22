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
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * @author Oliver Becker
 * @since 17.02.13
 */
class LessTask extends DefaultTask {

    private static final String LESS_PATH = 'less-rhino-1.3.3.js'
    private static final String TMP_DIR = "tmp${File.separator}js"

    private final RhinoExec rhino = new RhinoExec(project)

    @InputFiles
    def sourceDir
    @InputFile
    def source
    @OutputFile
    def dest

    FileCollection getSourceDir() {
        project.lesscss.sourceDir
    }

    File getDest() {
        project.file(project.lesscss.dest)
    }

    File getSource() {
        project.file(project.lesscss.source)
    }

    @TaskAction
    def run() {
        final File lessFile = ResourceUtil.extractFileToDirectory(new File(project.buildDir, TMP_DIR), LESS_PATH)
        final List<String> args = [lessFile.canonicalPath, getSource().canonicalPath]

        if (project.lesscss.compress) {
            args.add('-x')
        }
        rhino.execute(args, [workingDir: project.projectDir.canonicalPath, out: new FileOutputStream(getDest())])
    }

}
