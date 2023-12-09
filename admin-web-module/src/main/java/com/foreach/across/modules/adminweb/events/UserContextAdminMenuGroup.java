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

package com.foreach.across.modules.adminweb.events;

import lombok.Getter;
import lombok.Setter;

/**
 * Event published when the admin web layout builds its menu.
 * Users can listen for this event and configure the name and optional thumbnail that should be rendered.
 *
 * @author Steven Gentens
 * @since 3.1.0
 */
@Getter
@Setter
public class UserContextAdminMenuGroup
{
	public final static String MENU_PATH = "/user-context";
	public final static String ATTRIBUTE = "UserContextAdminMenuGroup";

	private String displayName;
	private String thumbnailUrl;
}
