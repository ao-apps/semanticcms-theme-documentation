/*
 * semanticcms-theme-documentation - SemanticCMS theme tailored for technical documentation.
 * Copyright (C) 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import com.semanticcms.core.servlet.SemanticCMS;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener("Registers the \"" + DocumentationTheme.THEME_NAME + "\" theme and required scripts in SemanticCMS.")
public class DocumentationThemeContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		SemanticCMS semanticCMS = SemanticCMS.getInstance(event.getServletContext());
		// TODO: Return a Script object type instead, with a follow-up of "jQuery.noConflict();"
		semanticCMS.addScript("jquery", "/webjars/jquery/" + Maven.properties.getProperty("jquery.version") + "/jquery.min.js");
		semanticCMS.addTheme(new DocumentationTheme());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Do nothing
	}
}
