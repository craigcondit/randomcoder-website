package org.randomcoder.config;

import java.util.*;

import javax.inject.Inject;

import net.sf.ehcache.*;

import org.acegisecurity.AccessDecisionManager;
import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.intercept.web.*;
import org.acegisecurity.providers.*;
import org.acegisecurity.providers.anonymous.*;
import org.acegisecurity.providers.dao.*;
import org.acegisecurity.providers.dao.cache.EhCacheBasedUserCache;
import org.acegisecurity.providers.encoding.*;
import org.acegisecurity.providers.rememberme.RememberMeAuthenticationProvider;
import org.acegisecurity.ui.*;
import org.acegisecurity.ui.logout.*;
import org.acegisecurity.ui.rememberme.*;
import org.acegisecurity.ui.webapp.*;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.memory.*;
import org.acegisecurity.util.FilterChainProxy;
import org.acegisecurity.vote.*;
import org.acegisecurity.wrapper.SecurityContextHolderAwareRequestFilter;
import org.randomcoder.bo.UserBusiness;
import org.randomcoder.db.UserDao;
import org.randomcoder.security.*;
import org.randomcoder.security.userdetails.UserDetailsServiceImpl;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

// TODO refactor ACEGI to use spring security

@Configuration
@SuppressWarnings("javadoc")
public class AcegiContext
{
	// ACEGI filter definition source
	private static final String FILTER_DEFINITION_SOURCE =
			"CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON\r\n" +
					"PATTERN_TYPE_APACHE_ANT\r\n" +
					"/css/**=#NONE#\r\n" +
					"/images/**=#NONE#\r\n" +
					"/js/**=#NONE#\r\n" +
					"/**=httpSessionContextIntegrationFilter," +
					"logoutFilter," +
					"authenticationProcessingFilter," +
					"securityContextHolderAwareRequestFilter," +
					"rememberMeProcessingFilter," +
					"anonymousProcessingFilter," +
					"exceptionTranslationFilter," +
					"filterInvocationInterceptor";

	private static final String OBJECT_DEFINITION_SOURCE =
			"CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON\r\n" +
					"PATTERN_TYPE_APACHE_ANT\r\n" +
					"/article=ROLE_MANAGE_ARTICLES,ROLE_POST_ARTICLES\r\n" +
					"/article/**=ROLE_MANAGE_ARTICLES,ROLE_POST_ARTICLES\r\n" +
					"/redirect=IS_AUTHENTICATED_REMEMBERED\r\n" +
					"/user/profile=IS_AUTHENTICATED_REMEMBERED\r\n" +
					"/user/profile/**=IS_AUTHENTICATED_REMEMBERED\r\n" +
					"/user=ROLE_MANAGE_USERS\r\n" +
					"/user/**=ROLE_MANAGE_USERS\r\n" +
					"/comment=ROLE_MANAGE_COMMENTS\r\n" +
					"/comment/**=ROLE_MANAGE_COMMENTS\r\n" +
					"/tag=ROLE_MANAGE_TAGS\r\n" +
					"/tag/**=ROLE_MANAGE_TAGS\r\n" +
					"/**=IS_AUTHENTICATED_ANONYMOUSLY";

	@Inject
	Environment env;

	@Bean
	public FilterChainProxy filterChainProxy()
	{
		FilterInvocationDefinitionSourceEditor editor = new FilterInvocationDefinitionSourceEditor();
		editor.setAsText(FILTER_DEFINITION_SOURCE);
		FilterInvocationDefinitionSource source = (FilterInvocationDefinitionSource) editor.getValue();

		FilterChainProxy proxy = new FilterChainProxy();
		proxy.setFilterInvocationDefinitionSource(source);
		return proxy;
	}

	@Bean
	public HttpSessionContextIntegrationFilter httpSessionContextIntegrationFilter() throws Exception
	{
		return new HttpSessionContextIntegrationFilter();
	}

	@Bean
	public LogoutFilter logoutFilter(final TokenBasedRememberMeServices rememberMeServices)
	{
		NullLogoutHandler nlh = new NullLogoutHandler();
		nlh.setLogoutHandler(rememberMeServices);

		SecurityContextLogoutHandler sclh = new SecurityContextLogoutHandler();

		LogoutFilter filter = new LogoutFilter("/", new LogoutHandler[] { nlh, sclh });
		filter.setFilterProcessesUrl("/logout");

		return filter;
	}

	@Bean
	public AuthenticationProcessingFilter authenticationProcessingFilter(
			final ProviderManager authenticationManager)
	{
		AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter();
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationFailureUrl("/login-error");
		filter.setDefaultTargetUrl("/");
		filter.setFilterProcessesUrl("/j_security_check");
		return filter;
	}

	@Bean
	public SecurityContextHolderAwareRequestFilter securityContextHolderAwareRequestFilter()
	{
		return new SecurityContextHolderAwareRequestFilter();
	}

	@Bean
	public RememberMeProcessingFilter rememberMeProcessingFilter(
			final ProviderManager authenticationManager,
			final RememberMeServices rememberMeServices)
	{
		RememberMeProcessingFilter filter = new RememberMeProcessingFilter();
		filter.setAuthenticationManager(authenticationManager);
		filter.setRememberMeServices(rememberMeServices);
		return filter;
	}

	@Bean
	public AnonymousProcessingFilter anonymousProcessingFilter()
	{
		UserAttributeEditor editor = new UserAttributeEditor();
		editor.setAsText("anonymousUser,ROLE_ANONYMOUS");

		AnonymousProcessingFilter filter = new AnonymousProcessingFilter();
		filter.setKey(env.getRequiredProperty("acegi.anonymous.key"));
		filter.setUserAttribute((UserAttribute) editor.getValue());

		return filter;
	}

	@Bean
	public ExceptionTranslationFilter exceptionTranslationFilter()
	{
		ExceptionTranslationFilter filter = new ExceptionTranslationFilter();
		filter.setAuthenticationEntryPoint(authenticationProcessingFilterEntryPoint());
		filter.setAccessDeniedHandler(accessDeniedHandler());
		return filter;
	}

	@Bean
	public FilterSecurityInterceptor filterInvocationInterceptor(
			final ProviderManager authenticationManager)
	{
		FilterInvocationDefinitionSourceEditor editor = new FilterInvocationDefinitionSourceEditor();
		editor.setAsText(OBJECT_DEFINITION_SOURCE);

		FilterSecurityInterceptor it = new FilterSecurityInterceptor();
		it.setAuthenticationManager(authenticationManager);
		it.setAccessDecisionManager(accessDecisionManager());
		it.setObjectDefinitionSource((FilterInvocationDefinitionSource) editor.getValue());
		return it;
	}

	@Bean
	public AccessDecisionManager accessDecisionManager()
	{
		List<AccessDecisionVoter> voters = new ArrayList<AccessDecisionVoter>();
		voters.add(new RoleVoter());
		voters.add(new AuthenticatedVoter());

		AffirmativeBased voter = new AffirmativeBased();
		voter.setAllowIfAllAbstainDecisions(false);
		voter.setDecisionVoters(voters);
		return voter;
	}

	@Bean
	public AuthenticationProcessingFilterEntryPoint authenticationProcessingFilterEntryPoint()
	{
		AuthenticationProcessingFilterEntryPoint ep = new AuthenticationProcessingFilterEntryPoint();
		ep.setLoginFormUrl("/login");
		ep.setForceHttps(false);
		return ep;
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler()
	{
		return new AccessDeniedHandlerImpl();
	}

	@Bean
	public ProviderManager authenticationManager(
			final DaoAuthenticationProvider daoAuthenticationProvider,
			final AnonymousAuthenticationProvider anonymousAuthenticationProvider,
			final RememberMeAuthenticationProvider rememberMeAuthenticationProvider)
	{
		List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();
		providers.add(daoAuthenticationProvider);
		providers.add(anonymousAuthenticationProvider);
		providers.add(rememberMeAuthenticationProvider);

		ProviderManager am = new ProviderManager();
		am.setProviders(providers);
		return am;
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(final UserDetailsService userDetailsService)
	{
		DaoAuthenticationProvider prov = new DaoAuthenticationProvider();
		prov.setUserDetailsService(userDetailsService);
		prov.setPasswordEncoder(passwordEncoder());
		prov.setUserCache(userCache());
		return prov;
	}

	@Bean
	public AnonymousAuthenticationProvider anonymousAuthenticationProvider()
	{
		AnonymousAuthenticationProvider aap = new AnonymousAuthenticationProvider();
		aap.setKey(env.getRequiredProperty("acegi.anonymous.key"));
		return aap;
	}

	@Bean
	public RememberMeAuthenticationProvider rememberMeAuthenticationProvider()
	{
		RememberMeAuthenticationProvider rmap = new RememberMeAuthenticationProvider();
		rmap.setKey(env.getRequiredProperty("acegi.rememberme.key"));
		return rmap;
	}

	@Bean
	public UserCache userCache()
	{
		EhCacheBasedUserCache uc = new EhCacheBasedUserCache();
		uc.setCache(cache());
		return uc;
	}

	@Bean
	public Cache cache()
	{
		return CacheManager.getInstance().getCache("acegiUserCache");
	}

	@Bean
	public TokenBasedRememberMeServices rememberMeServices(final UserDetailsService userDetailsService)
	{
		TokenBasedRememberMeServices rms = new TokenBasedRememberMeServices();
		rms.setUserDetailsService(userDetailsService);
		rms.setParameter("j_persist");
		rms.setKey(env.getRequiredProperty("acegi.rememberme.key"));
		return rms;
	}

	@Bean
	public UserDetailsService userDetailsService(final UserDao userDao)
	{
		UserDetailsServiceImpl uds = new UserDetailsServiceImpl();
		uds.setUserDao(userDao);
		uds.setDebug(false);
		return uds;
	}

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new ShaPasswordEncoder();
	}

	@Bean
	public AuthenticationAuditListener authenticationEventListener(final UserBusiness userBusiness)
	{
		AuthenticationAuditListener aal = new AuthenticationAuditListener();
		aal.setUserBusiness(userBusiness);
		return aal;
	}
}