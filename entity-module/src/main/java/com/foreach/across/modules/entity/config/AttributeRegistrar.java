/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.config;

import com.foreach.across.core.support.WritableAttributes;

import java.util.function.BiConsumer;

/**
 * Interface for registering a single attribute.
 * Can be used for helper methods that return something that registers an attribute.
 * <p/>
 * The first parameter will always be the owner of the attribute registry, providing
 * a callback mechanism. The second parameter is the collection where the attribute
 * should be registered.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public interface AttributeRegistrar<T> extends BiConsumer<T, WritableAttributes>
{
}
