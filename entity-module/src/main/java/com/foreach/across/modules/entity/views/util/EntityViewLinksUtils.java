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

package com.foreach.across.modules.entity.views.util;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import lombok.experimental.UtilityClass;

/**
 * Supports configuring the url that should be redirected to after successfully saving an entity {@link #afterSave}
 * or reconfiguring the url on a {@link ButtonViewElement} on the page {@link #button}.
 * When configuring a redirect, one of the default views can be used or link can be built for the view specifially.
 * <p>
 * If one of the default links is used, the {@code 'from'} request parameter is taken into account. If present,
 * it will take precedence over the fixed configured url, following the flow of the default entity views.
 * <p>
 * The predefined link options do <b>not</b> include the defaults, being:
 * - After saving, an update view redirects to itself
 * - Cancel button (update/create/delete views) redirects to the list view
 * - Back button (detail view) redirects to the list view
 *
 * @author Marc Vanbrabant
 * @author Steven Gentens
 * @since 4.2.0
 */
@UtilityClass
public class EntityViewLinksUtils
{
	public static final EntityViewLinksUtilsHandlers.AfterSaveLinkHandler afterSave = new EntityViewLinksUtilsHandlers.AfterSaveLinkHandler();
	public static final EntityViewLinksUtilsHandlers.ButtonLinkHandler button = new EntityViewLinksUtilsHandlers.ButtonLinkHandler();
}
