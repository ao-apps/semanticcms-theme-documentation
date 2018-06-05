/*
 * semanticcms-theme-documentation - SemanticCMS theme tailored for technical documentation.
 * Copyright (C) 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-theme-documentation.
 *
 * semanticcms-theme-documentation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-theme-documentation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-theme-documentation.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.theme.documentation;

import com.semanticcms.core.controller.SemanticCMS;
import com.semanticcms.core.controller.ServletSpace;
import com.semanticcms.core.renderer.html.HtmlRenderer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener("Registers the \"" + DocumentationTheme.THEME_NAME + "\" theme and required scripts in HtmlRenderer and SemanticCMS.")
public class DocumentationThemeContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		HtmlRenderer htmlRenderer = HtmlRenderer.getInstance(servletContext);
		htmlRenderer.addScript("jquery", "/webjars/jquery/2.2.4/jquery.min.js");
		htmlRenderer.addTheme(new DocumentationTheme());
		// TODO: Move to /META-INF/semanticcms-servlet-space.xml?
		// TODO: Allow semanticcms-servlet-space.xml anywhere in the directory structure?
		SemanticCMS semanticCMS = SemanticCMS.getInstance(servletContext);
		ServletSpace.Action.NotFoundAction notFound = ServletSpace.Action.NotFoundAction.getInstance();
		ServletSpace.Action.PassThroughAction passThrough = ServletSpace.Action.PassThroughAction.getInstance();
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf(DocumentationTheme.PREFIX + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				notFound
			)
		);
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf(DocumentationTheme.PREFIX + "/navigation.js"),
				passThrough
			)
		);
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf(DocumentationTheme.PREFIX + "/error-pages" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				notFound
			)
		);
		// TODO: Implement with a matcher *.jsp to not found instead of this nested multi-level stuff?
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf(DocumentationTheme.PREFIX + "/jslib/yui-" + DocumentationTheme.YUI_VERSION + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				notFound
			)
		);
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf(DocumentationTheme.PREFIX + "/jslib/yui-" + DocumentationTheme.YUI_VERSION + ServletSpace.Prefix.BOUNDED_MULTILEVEL + ServletSpace.Prefix.UNBOUNDED_MULTILEVEL),
				passThrough
			)
		);
		// TODO: Versions from filtered .xml with maven properties
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf("/webjars/html5shiv/3.7.3" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
		// TODO: Versions from filtered .xml with maven properties
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf("/webjars/jquery/2.2.4" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
		// TODO: Move to semanticcms-pagegraph (or own project?)
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf("/webjars/d3js/3.5.17" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
		// TODO: Move to docs-taglib:
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf("/images" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf("/images/list-item/16x16" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf("/images/styles" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
		// TODO: Move to -styles project
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf(DocumentationTheme.PREFIX + "/images" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
		semanticCMS.addServletSpace(
			new ServletSpace(
				ServletSpace.Prefix.valueOf(DocumentationTheme.PREFIX + "/styles" + ServletSpace.Prefix.BOUNDED_MULTILEVEL),
				passThrough
			)
		);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Do nothing
	}
}
