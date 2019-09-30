package nl.menninga.menno.as.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors()
			.and()
			.csrf()
				.disable()
			.exceptionHandling()
			.authenticationEntryPoint(
					(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
			)
			.and()
			.authorizeRequests()
			    .antMatchers(HttpMethod.GET, "/api/user/**")
			    	.hasAnyRole("AS_ADMIN", "AS_USER")
			    .antMatchers(HttpMethod.GET, "/api/**")
			    	.hasRole("AS_ADMIN")
			    .antMatchers(HttpMethod.GET, "/api/**")
			    	.hasRole("AS_ADMIN")
			    .antMatchers(HttpMethod.POST, "/api/**")
			    	.hasRole("AS_ADMIN")
			    .antMatchers(HttpMethod.PUT, "/api/**")
				    .hasAnyRole("AS_ADMIN", "AS_USER")
			    .antMatchers(HttpMethod.DELETE, "/api/**")
				    .hasRole("AS_ADMIN")
			    .antMatchers("/actuator/**","/assets/**")
	                .permitAll()
	            .antMatchers(HttpMethod.OPTIONS)
	                .permitAll()
			    .anyRequest()
				    .authenticated();
    }
}