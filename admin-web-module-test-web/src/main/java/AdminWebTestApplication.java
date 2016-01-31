import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.web.servlet.AbstractAcrossServletInitializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author Arne Vandamme
 */
@Configuration
@Import({ DispatcherServletAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.class,
          ServerPropertiesAutoConfiguration.class, AdminWebTestApplication.Test.class })
public class AdminWebTestApplication
{
	@Configuration
	@EnableAcrossContext(AdminWebModule.NAME)
	static class Test implements BeanDefinitionRegistryPostProcessor, ServletContextInitializer
	{
		@Override
		public void postProcessBeanDefinitionRegistry( BeanDefinitionRegistry registry ) throws BeansException {
			BeanDefinition beanDefinition = registry.getBeanDefinition( "acrossContext" );
			beanDefinition.setLazyInit( true );
		}

		@Override
		public void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory ) throws BeansException {

		}

		@Override
		public void onStartup( ServletContext servletContext ) throws ServletException {
			servletContext.setAttribute( AbstractAcrossServletInitializer.DYNAMIC_INITIALIZER, true );

			AnnotationConfigEmbeddedWebApplicationContext rootContext =
					(AnnotationConfigEmbeddedWebApplicationContext) servletContext.getAttribute(
							WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE );

			// Ensure the AcrossContext has bootstrapped while the ServletContext can be modified
			AcrossContext acrossContext = rootContext.getBean( AcrossContext.class );

			servletContext.setAttribute( WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
			                             AcrossContextUtils.getApplicationContext( acrossContext ) );

		}
	}

	public static void main( String[] args ) {
		SpringApplication.run( AdminWebTestApplication.class, args );
	}
}
