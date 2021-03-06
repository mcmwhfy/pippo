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
package ro.fortsoft.pippo.guice;

import ro.fortsoft.pippo.core.controller.Controller;
import ro.fortsoft.pippo.core.controller.ControllerInstantiationListener;

import com.google.inject.Injector;

/**
 * @author James Moger
 */
public class GuiceControllerInjector implements ControllerInstantiationListener {

    private final Injector guice;

    public GuiceControllerInjector(Injector guice) {
        this.guice = guice;
    }

    @Override
    public void onInstantiation(Controller controller) {
        guice.injectMembers(controller);
    }

}
