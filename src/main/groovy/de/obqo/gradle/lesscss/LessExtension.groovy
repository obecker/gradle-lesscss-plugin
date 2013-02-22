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

/**
 * @author Oliver Becker
 * @since 17.02.13
 */
class LessExtension {

    public static final String NAME = 'lesscss'

    /** A <code>FileCollection</code> containing all source files that <code>source</code> depends on */
    def sourceDir = null

    /** The name and path of the less file that should be compiled */
    def source = null

    /** The name and path of the css file to be generated */
    def dest = null

    /** If set to <code>true</code> the resulting css will be compressed */
    def compress = false

}
