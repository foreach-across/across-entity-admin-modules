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

package admin.application.config;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.MutableIconSet;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.bootstrapui.config.FontAwesomeIconSetConfiguration.FONT_AWESOME_SOLID_ICON_SET;
import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;

@Configuration
public class CustomAdminWebIcons
{
	public static final String DELETE = "delete";
	public static final String DOWNLOAD = "download";
	public static final String ALERT = "delete";

	@Autowired
	public void registerAdminWebIconSet() {
		MutableIconSet adminWebIconSet = IconSetRegistry.getIconSet( AdminWebModule.NAME );
		adminWebIconSet.add( DELETE, ( imageName ) -> iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "trash" ) );
		adminWebIconSet.add( DOWNLOAD, ( imageName ) -> iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "download" ) );
		adminWebIconSet.add( ALERT, ( imageName ) -> iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "exclamation-triangle" ) );

		IconSetRegistry.addIconSet( AdminWebModule.NAME, adminWebIconSet );
	}
}
