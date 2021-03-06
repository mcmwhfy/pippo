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
package ro.fortsoft.pippo.core.controller;

import java.lang.reflect.Method;

/**
 * Listener interface that receives messages when controllers methods are invoked.
 *
 * @author Decebal Suiu
 */
public interface ControllerInvokeListener {

    /**
     * Called for every controller before its action method is invoke.
     *
     * @param controller
     */
    public void onInvoke(Controller controller, Method method);

}
