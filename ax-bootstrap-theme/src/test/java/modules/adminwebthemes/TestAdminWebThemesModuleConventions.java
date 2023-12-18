package modules.adminwebthemes;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

public class TestAdminWebThemesModuleConventions extends AbstractAcrossModuleConventionsTest {
    @Override
    protected AcrossModule createModule() {
        return new AdminWebModule();
    }
}