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

import org.gradle.StartParameter;
import org.gradle.api.internal.artifacts.ivyservice.projectmodule.CompositeConstructingTaskResolver;
import org.gradle.api.internal.artifacts.ivyservice.projectmodule.ProjectArtifactBuilder;
import org.gradle.api.internal.composite.CompositeBuildContext;
import org.gradle.api.internal.tasks.ConstructingTaskResolver;
import org.gradle.initialization.GradleLauncherFactory;
import org.gradle.initialization.IncludedBuildExecuter;
import org.gradle.initialization.IncludedBuildFactory;
import org.gradle.internal.composite.CompositeContextBuilder;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.service.ServiceRegistration;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.internal.service.scopes.PluginServiceRegistry;

public class CompositeBuildServices implements PluginServiceRegistry {
    public void registerGlobalServices(ServiceRegistration registration) {
        registration.addProvider(new CompositeBuildGlobalScopeServices());
    }

    public void registerBuildSessionServices(ServiceRegistration registration) {
        registration.addProvider(new CompositeBuildSessionScopeServices());
    }

    public void registerBuildServices(ServiceRegistration registration) {
    }

    public void registerGradleServices(ServiceRegistration registration) {
    }

    public void registerProjectServices(ServiceRegistration registration) {
    }

    private static class CompositeBuildGlobalScopeServices {
        public ConstructingTaskResolver createResolver() {
            return new CompositeConstructingTaskResolver();
        }
    }

    public static class CompositeBuildSessionScopeServices {
        public IncludedBuildFactory createIncludedBuildFactory(Instantiator instantiator, StartParameter startParameter, GradleLauncherFactory gradleLauncherFactory, ServiceRegistry serviceRegistry) {
            return new DefaultIncludedBuildFactory(instantiator, startParameter, gradleLauncherFactory, serviceRegistry);
        }

        public CompositeBuildContext createCompositeBuildContext() {
            return new DefaultBuildableCompositeBuildContext();
        }

        public CompositeContextBuilder createCompositeContextBuilder(CompositeBuildContext context) {
            return new DefaultCompositeContextBuilder(context);
        }

        public IncludedBuildExecuter createIncludedBuildExecuter(CompositeBuildContext context) {
            return new DefaultIncludedBuildExecuter(context);
        }

        public ProjectArtifactBuilder createCompositeProjectArtifactBuilder(IncludedBuildExecuter includedBuildExecuter) {
            return new CompositeProjectArtifactBuilder(includedBuildExecuter);
        }
    }

}
