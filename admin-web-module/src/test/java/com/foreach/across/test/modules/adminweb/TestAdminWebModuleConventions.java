package com.foreach.across.test.modules.adminweb;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

/**
 * @author Arne Vandamme
 */
public class TestAdminWebModuleConventions extends AbstractAcrossModuleConventionsTest
{
	@Override
	protected boolean hasSettings() {
		return true;
	}

	@Override
	protected AcrossModule createModule() {
		return new AdminWebModule();
	}
}
