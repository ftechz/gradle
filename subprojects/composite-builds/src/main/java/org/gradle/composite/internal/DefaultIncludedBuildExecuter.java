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

package org.gradle.composite.internal;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.component.ProjectComponentSelector;
import org.gradle.api.internal.composite.CompositeBuildContext;
import org.gradle.initialization.IncludedBuildExecuter;
import org.gradle.internal.component.local.model.DefaultProjectComponentSelector;
import org.gradle.internal.resolve.ModuleVersionResolveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

class DefaultIncludedBuildExecuter implements IncludedBuildExecuter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIncludedBuildExecuter.class);

    private final Set<ProjectComponentIdentifier> executingBuilds = Sets.newHashSet();
    private final Multimap<ProjectComponentIdentifier, String> executedTasks = LinkedHashMultimap.create();
    private final CompositeBuildContext compositeBuildContext;

    public DefaultIncludedBuildExecuter(CompositeBuildContext compositeBuildContext) {
        this.compositeBuildContext = compositeBuildContext;
    }

    @Override
    public void execute(ProjectComponentIdentifier buildIdentifier, Iterable<String> taskNames) {
        checkBuildIdentifier(buildIdentifier);
        buildStarted(buildIdentifier);
        try {
            doBuild(buildIdentifier, taskNames);
        } finally {
            buildCompleted(buildIdentifier);
        }
    }

    // TODO:DAZ More id crap
    private void checkBuildIdentifier(ProjectComponentIdentifier buildIdentifier) {
        if (!buildIdentifier.getProjectPath().endsWith("::")) {
            throw new IllegalArgumentException(buildIdentifier + " is not a build identifier");
        }
    }

    private synchronized void buildStarted(ProjectComponentIdentifier build) {
        if (!executingBuilds.add(build)) {
            ProjectComponentSelector selector = new DefaultProjectComponentSelector(build.getProjectPath());
            throw new ModuleVersionResolveException(selector, "Dependency cycle including " + build);
        }
    }

    private synchronized void buildCompleted(ProjectComponentIdentifier project) {
        executingBuilds.remove(project);
    }

    private void doBuild(ProjectComponentIdentifier buildId, Iterable<String> taskPaths) {
        List<String> tasksToExecute = Lists.newArrayList();
        for (String taskPath : taskPaths) {
            if (executedTasks.put(buildId, taskPath)) {
                tasksToExecute.add(taskPath);
            }
        }
        if (tasksToExecute.isEmpty()) {
            return;
        }
        LOGGER.info("Executing " + buildId + " tasks " + taskPaths);

        IncludedBuildInternal build = (IncludedBuildInternal) compositeBuildContext.getBuild(buildId);
        build.execute(tasksToExecute);
    }

}
