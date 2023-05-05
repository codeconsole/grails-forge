/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.forge.feature.github.workflows;

import org.grails.forge.application.ApplicationType;
import org.grails.forge.feature.Category;
import org.grails.forge.feature.Feature;

import java.util.Collections;
import java.util.List;

/**
 * GitHub workflow feature.
 *
 * @author Pavol Gressa
 * @since 6.0.0
 */
public abstract class GitHubWorkflowFeature implements Feature {

    @Override
    public boolean isPreview() {
        return true;
    }

    @Override
    public boolean supports(ApplicationType applicationType) {
        return true;
    }

    @Override
    public String getCategory() {
        return Category.CICD;
    }

    public List<Secret> getSecrets() {
        return Collections.emptyList();
    }

    public String getWorkflowFilePath() {
        return ".github/workflows/" + getWorkflowFileName();
    }

    protected abstract String getWorkflowFileName();
}
