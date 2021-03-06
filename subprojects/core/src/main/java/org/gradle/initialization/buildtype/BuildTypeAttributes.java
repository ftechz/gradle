/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.initialization.buildtype;

/**
 * Allows a plugin to detect various aspects about the type of build being executed.
 */
public interface BuildTypeAttributes {
    /**
     * Is the build 'nested' within another build? Examples of nested builds are: buildSrc, GradleBuild task, included build.
     */
    boolean isNestedBuild();

    /**
     * Is this build a composite build?
     */
    boolean isCompositeBuild();

    /**
     * Was this build triggered by the Tooling API?
     */
    boolean isToolingApiBuild();

    /**
     * Is this build a tooling model request?
     */
    boolean isToolingModelRequest();
}
