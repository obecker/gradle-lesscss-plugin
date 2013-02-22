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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * @author Oliver Becker
 * @since 17.02.13
 */
class LessTaskTest extends Specification {

    @Rule TemporaryFolder dir = new TemporaryFolder()

    Project project = ProjectBuilder.builder().build()
    def task
    def lesscss

    def setup() {
        project.apply(plugin: LessPlugin)
        task = project.tasks.lesscss
        lesscss = project.lesscss
        lesscss.dest = dir.newFile()
    }

    def getFile(String name) {
        new File(Thread.currentThread().contextClassLoader.getResource(name).toURI())
    }

    def 'simple run of less'() {
        given:
        lesscss.source = getFile("style.less")

        when:
        task.run()

        then:
        def actual = task.dest.readLines()
        def expected = getFile("style.css").readLines()
        actual == expected
    }

    def 'run less with compress option'() {
        given:
        lesscss.source = getFile("style.less")
        lesscss.compress = true

        when:
        task.run()

        then:
        def actual = task.dest.readLines()
        def expected = getFile("style.min.css").readLines()
        actual == expected
    }

}
