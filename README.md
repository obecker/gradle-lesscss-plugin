# Gradle LessCss Plugin [![Build Status](https://travis-ci.org/obecker/gradle-lesscss-plugin.png?branch=master)](https://travis-ci.org/obecker/gradle-lesscss-plugin)

A gradle plugin that compiles [LESS](http://lesscss.org) files to CSS. Version 1.0-1.3.3 uses LESS version 1.3.3.

This plugin helps to you to integrate the processing of LESS files into your automated build process without the need of installing node.js
on the build server or adding the compiled CSS files to your version control system.



## Usage

#### Add the plugin

Add artifact for the plugin to your [Gradle](http://gradle.org) buildscript dependencies in your *build.gradle*:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'de.obqo.gradle:gradle-lesscss-plugin:1.0-1.3.3'
    }
}
```

**Important notice**: the deployment of the artifact to maven central is currently pending. 
Meanwhile you may build the plugin yourself (`gradle install`) and then add `mavenLocal()`
to the `buildscript` `repositories` section of the project where you want to use the plugin.

#### Activate the plugin

```groovy
apply plugin: 'lesscss'
```

#### Configure the plugin

```groovy
lesscss {
    source = fileTree('src/main/less') {
        include 'style.less'
    }
    dest = 'src/main/webapp/assets/css'
    compress = true
}
```

#### Run the plugin

The plugin adds two tasks to the build script: `lesscss` and (implicitly) `cleanLesscss`.

* `lesscss` - compiles the specified LESS files from the `source` directory to CSS files in the `dest` directory
* `cleanLesscss` - deletes completely the `dest` directory (be careful if there are other than generated CSS files in this directory)

You may optionally add a dependency for example for the `war` task by specifying

```groovy
war {
    dependsOn 'lesscss'
}
```

Then everytime the `war` task is executed the `lesscss` task will be executed before.


**That's it!**

## Options

The `lesscss` object provides 3 properties for configuring the gradle-lesscss-plugin:

* `source` (required)  
describes the LESS sources. The `fileTree` should refer to the LESS base directory (`"src/main/less"` in the example above), then `include` will select
only the files to be compiled (`"style.less"` in the example). The value for `include` might be an Ant-style file pattern (see the section about
[File trees](http://www.gradle.org/docs/current/userguide/working_with_files.html#sec:file_trees) in the gradle user guide).
Note: it is important to correctly set the base directory since all contained files (i.e. imported modules) will be accounted for determining whether the
output CSS files are up-to-date or not.

* `dest` (required)  
describes the target directory for the CSS files. This is either a string or a file object. The names of the generated CSS files are derived from the
source files, thus compiling `style.less` results in `style.css` in the `dest` directory.

* `compress` (optional, defaults to `false`)  
when set to `true` turns on compression of the created CSS files.

## Acknowledgments

Main parts of the build configuration as well as two classes for running JS scripts with Rhino have been taken from Eric Wendelin's great
[gradle-js-plugin](https://github.com/eriwen/gradle-js-plugin). Without his work the development of this plugin would have taken much longer (or would
have possibly not even succeeded). Thanks Eric!

