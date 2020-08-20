/*
 * semanticcms-theme-documentation - SemanticCMS theme tailored for technical documentation.
 * Copyright (C) 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.net.URIEncoder;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.web.resources.registry.Group;
import com.aoindustries.web.resources.registry.Registry;
import com.aoindustries.web.resources.registry.Style;
import com.aoindustries.web.resources.servlet.RegistryEE;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.servlet.SemanticCMS;
import com.semanticcms.core.servlet.Theme;
import com.semanticcms.core.servlet.View;
import com.semanticcms.theme.documentation.style.DocumentationThemeStyle;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

/**
 * A theme designed for technical documentation.
 */
public class DocumentationTheme extends Theme {

	private static final String NAME = "semanticcms-theme-documentation";

	private static final String PREFIX = "/" + NAME;

	private static final String THEME_JSPX      = PREFIX + "/theme.inc.jspx";
	private static final String FRAMESET_JSPX   = PREFIX + "/frameset.inc.jspx";
	private static final String NAVIGATION_JSPX = PREFIX + "/navigation.inc.jspx";

	// TODO: Version from filtered .xml with maven properties
	private static final String YUI_VERSION = "2.9.0";
	private static final String YUI_PREFIX = PREFIX + "/jslib/yui-" + YUI_VERSION;

	/**
	 * The name of the {@link Group} of web resources for YUI.
	 */
	public static final Group.Name YUI_GROUP = new Group.Name(NAME + "/yui-" + YUI_VERSION);

	public static final Style YUI_TREEVIEW = new Style(YUI_PREFIX + "/build/treeview/assets/skins/sam/treeview.css");
	// public static final Style YUI_CALENDAR = new Style(YUI_PREFIX + "/build/calendar/assets/skins/sam/calendar.css");//
	public static final Style YUI_TREE     = new Style(YUI_PREFIX + "/examples/treeview/assets/css/folders/tree.css");

	@WebListener("Registers the \"" + NAME + "\" theme and required scripts in RegistryEE and SemanticCMS.")
	public static class Initializer implements ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent event) {
			ServletContext servletContext = event.getServletContext();

			Registry registry = RegistryEE.Application.get(servletContext);

			registry.getGroup(YUI_GROUP).styles
				.add(
					YUI_TREEVIEW,
					// YUI_CALENDAR,
					YUI_TREE
				)
				// treeview -> calendar -> tree
				.addOrdering(
					YUI_TREEVIEW,
					// YUI_CALENDAR,
					YUI_TREE
				);

			registry.getGroup(DocumentationThemeStyle.NAVIGATION_GROUP).styles
				// tree -> navigation
				.addOrdering(YUI_TREE, DocumentationThemeStyle.NAVIGATION);

			SemanticCMS semanticCMS = SemanticCMS.getInstance(servletContext);
			// TODO: Return a Script object type instead, with a follow-up of "jQuery.noConflict();"
			semanticCMS.addScript("jquery", "/webjars/jquery/" + URIEncoder.encodeURIComponent(Maven.jqueryVersion) + "/jquery.min.js");
			semanticCMS.addTheme(new DocumentationTheme());
		}

		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// Do nothing
		}
	}

	private DocumentationTheme() {}

	@Override
	public String getDisplay() {
		return "SemanticCMS Documentation";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void doTheme(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		View view,
		Page page
	) throws ServletException, IOException, SkipPageException {
		Map<String,Object> args = new LinkedHashMap<>();
		args.put("page", page);

		String target;
		if(Boolean.parseBoolean(request.getParameter("frames"))) {
			// Dispatch to frameset
			target = FRAMESET_JSPX;
		} else if(Boolean.parseBoolean(request.getParameter("navigation"))) {
			// Dispatch to navigation
			target = NAVIGATION_JSPX;
		} else {
			// Dispatch to theme with view
			target = THEME_JSPX;
			args.put("view", view);
		}

		Dispatcher.forward(servletContext, target, request, response, args);
	}
}
