/*
 * Copyright 2012 Eric Wendelin
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
package de.obqo.gradle.helper

import org.gradle.api.Project
import org.gradle.process.ExecResult

/**
 * Utility for executing JS with Rhino.
 *
 * @author Eric Wendelin
 * @date 2/20/12
 */
class RhinoExec {

    private static final String RHINO_MAIN_CLASS = 'org.mozilla.javascript.tools.shell.Main'
    Project project

    void execute(final Iterable<String> execargs, final Map<String, Object> options = [:]) {
        final String workingDirIn = options.get('workingDir', '.')
        final Boolean ignoreExitCode = options.get('ignoreExitCode', false).asBoolean()
        final OutputStream out = options.get('out', System.out) as OutputStream
        def execOptions = {
            main = RHINO_MAIN_CLASS
            classpath = project.configurations.rhino
            args = execargs
            workingDir = workingDirIn
            ignoreExitValue = ignoreExitCode
            standardOutput = out
        }

        ExecResult result = project.javaexec(execOptions)

        if (!ignoreExitCode) {
            result.assertNormalExitValue()
        }
    }

    public RhinoExec(final Project projectIn) {
        project = projectIn
    }
}
