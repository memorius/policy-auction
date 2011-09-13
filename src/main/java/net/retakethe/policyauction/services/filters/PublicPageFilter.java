// Based on http://tapestryjava.blogspot.com/2009/12/securing-tapestry-pages-with.html
package net.retakethe.policyauction.services.filters;

import java.io.IOException;

import net.retakethe.policyauction.annotations.RestrictedPage;
import net.retakethe.policyauction.data.api.types.UserRole;
import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.session.NewSession;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

/**
 * The Class PublicPageFilter. Designed to protect pages and redirect to logon page should a user not be logged on/in session.
 */
public class PublicPageFilter implements ComponentRequestFilter {

	private final PageRenderLinkSource pageRenderLinkSource;
	private final ComponentSource componentSource;
	private final Response response;
	private ApplicationStateManager sessionStateManager;
	private final Logger logger;
	
	public PublicPageFilter(PageRenderLinkSource pageRenderLinkSource, ComponentSource componentSource,
			Response response, ApplicationStateManager asm, Logger logger) {
		this.pageRenderLinkSource = pageRenderLinkSource;
		this.componentSource = componentSource;
		this.response = response;
		this.sessionStateManager = asm;
		this.logger = logger;
	}

	@Override
	public void handleComponentEvent(ComponentEventRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
		if (isAuthorisedToPage(parameters.getActivePageName())) {
			handler.handleComponentEvent(parameters);
		}
		else {
			// The method will have redirected us to the login page
			return;
		}

	}

	@Override
	public void handlePageRender(PageRenderRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
		if (isAuthorisedToPage(parameters.getLogicalPageName())) {
			handler.handlePageRender(parameters);
		}
		else {
			// The method will have redirected us to the login page
			return;
		}
	}

	private boolean isAuthorisedToPage(String logicalPageName) throws IOException {
		// If the requested page is annotated @RestrictedPage...

		Component page = componentSource.getPage(logicalPageName);
		boolean restrictedPage = page.getClass().getAnnotation(RestrictedPage.class) != null;

		
		if (restrictedPage) {
			UserRole[] allowedRoles = page.getClass().getAnnotation(RestrictedPage.class).allowedRoles();
			// If the session contains a User then you have already been authenticated

			if (sessionStateManager.exists(User.class)) {
				if (allowedRoles != null && allowedRoles.length > 0) {
					for (UserRole pageRoles : allowedRoles) {
						if (sessionStateManager.get(User.class).getRoles().contains(pageRoles)) {
							return true;
						}
					}
					// user does not have required role to do anything with page. Sorry!
					return false;
				} else {
					// No set role restrictions required, so as long as user is logged in we consider this ok.
					return true;
				}
			}

			// Else go to the Login page

			else {
				Link loginPageLink = pageRenderLinkSource.createPageRenderLink(NewSession.class);
				response.sendRedirect(loginPageLink);
				return false;
			}

		}
		else {
			return true;
		}
	}

}
