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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The routes are matched in the order they are defined.
 *
 * @author Decebal Suiu
 */
public class DefaultRouteMatcher extends AbstractRouteMatcher {

    private static final Logger log = LoggerFactory.getLogger(DefaultRouteMatcher.class);

    //Matches: {id} AND {id: .*?}
    // group(1) extracts the name of the group (in that case "id").
    // group(3) extracts the regex if defined
    final Pattern PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE
        = Pattern.compile("\\{(.*?)(:\\s(.*?))?\\}");

    /**
     * This regex matches everything in between path slashes.
     */
    final String VARIABLE_ROUTES_DEFAULT_REGEX = "([^/]*)";

    private Map<String, List<PatternBinding>> bindingsCache; // key = request method

    public DefaultRouteMatcher() {
        super();

        bindingsCache = new HashMap<>();
    }

    @Override
    public List<RouteMatch> findRoutes(String requestUri, String requestMethod) {
        log.debug("Finding route for '{} {}'", requestUri, requestMethod);
        List<PatternBinding> bindings = bindingsCache.get(requestMethod);
        if (bindings == null) {
            return Collections.emptyList();
        }

        List<RouteMatch> routeMatches = new ArrayList<>();
        for (PatternBinding binding : bindings) {
            if (binding.getPattern().matcher(requestUri).matches()) {
                // TODO improve (it's possible to have the same urlPattern for many routes => same parameters)
                routeMatches.add(new RouteMatch(binding.getRoute(), getParameters(binding, requestUri)));
            }
        }

        return routeMatches;
    }

    @Override
    public void addRoute(Route route) throws Exception {
        super.addRoute(route);

        addBinding(route);
    }

    @Override
    public String urlFor(String urlPattern, Map<String, Object> parameters) {
        PatternBinding binding = getPatternBinding(urlPattern);

        return (binding != null) ? urlFor(binding, parameters) : null;
    }

    private void addBinding(Route route) {
        String urlPattern = route.getUrlPattern();
        // TODO improve (it's possible to have the same urlPattern for many routes => same pattern)
        String regex = getRegex(urlPattern);
        Pattern pattern = Pattern.compile(regex);
        List<String> parameterNames = getParameterNames(urlPattern);
        PatternBinding binding = new PatternBinding(pattern, route, parameterNames);
        String requestMethod = route.getRequestMethod();
        if (!bindingsCache.containsKey(requestMethod)) {
            bindingsCache.put(requestMethod, new ArrayList<PatternBinding>());
        }
        bindingsCache.get(requestMethod).add(binding);
    }

    /**
     * Transforms an url pattern like "/{name}/id/*" into a regex like "/([^/]*)/id/*."
     *
     * Also handles regular expressions if defined inside routes:
     * For instance "/users/{username: [a-zA-Z][a-zA-Z_0-9]}" becomes
     * "/users/([a-zA-Z][a-zA-Z_0-9])"
     *
     * @return The converted regex with default matching regex - or the regex
     *          specified by the user.
     */
    private String getRegex(String urlPattern) {
        Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(urlPattern);

        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {

            // By convention group 3 is the regex if provided by the user.
            // If it is not provided by the user the group 3 is null.
            String namedVariablePartOfRoute = matcher.group(3);
            String namedVariablePartOfORouteReplacedWithRegex;

            if (namedVariablePartOfRoute != null) {
                // we convert that into a regex matcher group itself
                namedVariablePartOfORouteReplacedWithRegex
                    = "(" + Matcher.quoteReplacement(namedVariablePartOfRoute) + ")";
            } else {
                // we convert that into the default namedVariablePartOfRoute regex group
                namedVariablePartOfORouteReplacedWithRegex
                    = VARIABLE_ROUTES_DEFAULT_REGEX;
            }
            // we replace the current namedVariablePartOfRoute group
            matcher.appendReplacement(stringBuffer, namedVariablePartOfORouteReplacedWithRegex);

        }

        // .. and we append the tail to complete the stringBuffer
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    /**
    *
    * Extracts the name of the parameters from a route
    *
    * /{my_id}/{my_name}
    *
    * would return a List with "my_id" and "my_name"
    *
    * @param urlPattern
    * @return a list with the names of all parameters in the url pattern
    */
    private List<String> getParameterNames(String urlPattern) {
        List<String> list = new ArrayList<String>();

        Matcher m = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(urlPattern);

        while (m.find()) {
            // group(1) is the name of the group. Must be always there...
            // "/assets/{file}" and "/assets/{file: [a-zA-Z][a-zA-Z_0-9]}"
            // will return file.
            list.add(m.group(1));
        }

        return list;
    }

    private Map<String, String> getParameters(PatternBinding binding, String requestUri) {
        Map<String, String> parameters = new HashMap<>();

        List<String> parameterNames = binding.getParameterNames();
        Matcher matcher = binding.getPattern().matcher(requestUri);
        matcher.matches();
        int groupCount = matcher.groupCount();
        if (groupCount > 0) {
            for (int i = 1; i <= groupCount; i++) {
                parameters.put(parameterNames.get(i - 1), matcher.group(i));
            }
        }

        return parameters;
    }

    private PatternBinding getPatternBinding(String urlPattern) {
        Iterator<List<PatternBinding>> iterator = bindingsCache.values().iterator();
        while (iterator.hasNext()) {
            List<PatternBinding> bindings = iterator.next();
            for (PatternBinding binding : bindings) {
                if (urlPattern.equals(binding.getRoute().getUrlPattern())) {
                    return binding;
                }
            }
        }

        return null;
    }

    private String urlFor(PatternBinding binding, Map<String, Object> parameters) {
        String urlPattern = binding.getRoute().getUrlPattern();

        // remove wildcards
        String url = urlPattern.replaceAll("\\*", "");

        List<String> parameterNames = binding.getParameterNames();
        if (!parameters.keySet().containsAll(parameterNames)) {
            log.error("You must provide values for all path parameters. {} vs {}", parameterNames, parameters.keySet());
        }

        Map<String, Object> queryParameters = new HashMap<>(parameters);
        for (String parameterName : parameterNames) {
            // replace parameter name with parameter value
            url = url.replaceAll(":" + parameterName, parameters.get(parameterName).toString());
            queryParameters.remove(parameterName);
        }

        if (!queryParameters.isEmpty()) {
            // add remaining parameters as query parameters
            StringBuilder query = new StringBuilder();
            Iterator<String> iterator = queryParameters.keySet().iterator();
            while (iterator.hasNext()) {
                String parameterName = iterator.next();
                Object parameterValue =  queryParameters.get(parameterName);
                query.append(parameterName).append("=").append(parameterValue.toString());
                if (iterator.hasNext()) {
                    query.append("&");
                }
            }

            url += "?" + query;
        }

        return url;
    }

    private class PatternBinding {

        private final Pattern pattern;
        private final Route route;
        private final List<String> parameterNames;

        private PatternBinding(Pattern pattern, Route route, List<String> parameterNames) {
            this.pattern = pattern;
            this.route = route;
            this.parameterNames = parameterNames;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public Route getRoute() {
            return route;
        }

        public List<String> getParameterNames() {
            return parameterNames;
        }

        @Override
        public String toString() {
            return "PatternBinding{" +
                    "pattern=" + pattern +
                    ", route=" + route +
                    ", parameterNames=" + parameterNames +
                    '}';
        }

    }

}
