/*
 * Copyright (C) 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.fortsoft.pippo.core.route;

import ro.fortsoft.pippo.core.Application;
import ro.fortsoft.pippo.core.HttpConstants;
import ro.fortsoft.pippo.core.Request;
import ro.fortsoft.pippo.core.Response;

import java.util.List;

/**
 * @author Decebal Suiu
 */
public class DefaultRouteNotFoundHandler implements RouteNotFoundHandler {

    private Application application;

    public DefaultRouteNotFoundHandler(Application application) {
        this.application = application;
    }

    @Override
    public void handle(String requestMethod, String requestUri, Request request, Response response) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");
        content.append("<div>");
        content.append("Cannot find a route for '");
        content.append(requestMethod);
        content.append(" ");
        content.append(requestUri);
        content.append("'</div>");
        content.append("<div>Available routes:</div>");
        content.append("<ul style=\" list-style-type: none; margin: 0; \">");
        List<Route> routes = application.getRouteMatcher().getRoutes();
        for (Route route : routes) {
            content.append("<li>");
            content.append(route.getRequestMethod());
            content.append(" ");
            content.append(route.getUrlPattern());
            content.append("</li>");
        }
        content.append("</ul>");
        content.append("</body></html>");

        response.status(HttpConstants.StatusCode.NOT_FOUND);
        response.send(content);
    }

}
