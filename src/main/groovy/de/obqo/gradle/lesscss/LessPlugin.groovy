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

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin class for the gradle-lesscss-plugin. Adds the <code>lesscss</code> task to the current project.
 *
 * @author Oliver Becker
 * @since 17.02.13
 */
class LessPlugin implements Plugin<Project> {

    void apply(final Project project) {
        project.extensions.create(LessExtension.NAME, LessExtension)

        configureDependencies(project)

        project.task('lesscss', type: LessTask, group: 'Build', description: 'Compile LESS files into CSS files')
    }

    void configureDependencies(final Project project) {
        project.configurations {
            rhino
        }
        project.repositories {
            mavenCentral()
        }
        project.dependencies {
            rhino 'org.mozilla:rhino:1.7R4'
        }
    }

}
