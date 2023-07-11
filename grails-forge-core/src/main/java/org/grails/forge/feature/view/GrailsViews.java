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
package org.grails.forge.feature.view;

import org.grails.forge.application.ApplicationType;
import org.grails.forge.feature.Category;
import org.grails.forge.feature.FeatureContext;
import org.grails.forge.feature.OneOfFeature;
import org.grails.forge.feature.web.GrailsWeb;

public abstract class GrailsViews implements OneOfFeature {

    public GrailsWeb grailsWeb;

    public GrailsViews(GrailsWeb grailsWeb) {
        this.grailsWeb = grailsWeb;
    }

    @Override
    public Class<?> getFeatureClass() {
        return GrailsViews.class;
    }

    @Override
    public boolean supports(ApplicationType applicationType) {
        return true;
    }

    @Override
    public String getCategory() {
        return Category.VIEW;
    }

    @Override
    public String getDocumentation() {
        return "https://views.grails.org/";
    }

    public String getViewFolderPath() {
        return "grails-app/views/";
    }

    @Override
    public void processSelectedFeatures(FeatureContext featureContext) {
        if (!featureContext.isPresent(GrailsWeb.class) && grailsWeb != null) {
            featureContext.addFeature(grailsWeb);
        }
    }
}
