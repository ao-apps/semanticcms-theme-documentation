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

import static com.aoindustries.net.Path.SEPARATOR_CHAR;
import com.aoindustries.net.pathspace.Prefix;
import static com.aoindustries.net.pathspace.Prefix.GREEDY_SUFFIX;
import static com.aoindustries.net.pathspace.Prefix.WILDCARD_SUFFIX;
import static com.aoindustries.net.pathspace.Prefix.valueOf;
import com.aoindustries.servlet.firewall.pathspace.FirewallComponent;
import com.aoindustries.servlet.firewall.pathspace.FirewallPathSpace;
import com.aoindustries.servlet.firewall.pathspace.Rules;
import static com.aoindustries.servlet.firewall.rules.Rules.chain.doFilter;
import static com.aoindustries.servlet.firewall.rules.Rules.request.dispatcherType.isError;
import static com.aoindustries.servlet.firewall.rules.Rules.request.dispatcherType.isForward;
import static com.aoindustries.servlet.firewall.rules.Rules.request.dispatcherType.isInclude;
import static com.aoindustries.servlet.firewall.rules.Rules.request.dispatcherType.isRequest;
import static com.aoindustries.servlet.firewall.rules.Rules.request.method.GET;
import static com.aoindustries.servlet.firewall.rules.Rules.request.method.constrain;
import static com.aoindustries.servlet.firewall.rules.Rules.response.sendError.FORBIDDEN;
import static com.aoindustries.servlet.firewall.rules.Rules.response.sendError.NOT_FOUND;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.style.AoStyle;
import com.aoindustries.web.resources.registry.Registry;
import com.aoindustries.web.resources.registry.Style;
import com.aoindustries.web.resources.registry.Styles;
import com.aoindustries.web.resources.servlet.RegistryEE;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.renderer.html.HtmlRenderer;
import com.semanticcms.core.renderer.html.Theme;
import com.semanticcms.core.renderer.html.View;
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

	/**
	 * The name of the {@link Group} of web resources for the documentation theme.
	 */
	public static final String STYLE_GROUP = NAME;

	private static final String PREFIX = "/" + NAME;

	public static final Style THEME_CSS = new Style(PREFIX + "/styles/semanticcms-theme-documentation.css");
	public static final Style THEME_PRINT_CSS = Style.builder().uri(PREFIX + "/styles/semanticcms-theme-documentation-print.css").media("print").build();

	private static final String JSPX_TARGET = PREFIX + "/theme.jspx";

	// TODO: Version from filtered .xml with maven properties
	private static final String YUI_VERSION = "2.9.0";
	private static final String YUI_PREFIX = PREFIX + "/jslib/yui-" + YUI_VERSION;

	/**
	 * The name of the {@link Group} of web resources for YUI.
	 */
	public static final String YUI_GROUP = "yui-" + YUI_VERSION;

	@WebListener("Registers the \"" + NAME + "\" theme and required scripts in RegistryEE and HtmlRenderer.")
	public static class Initializer implements ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent event) {
			ServletContext servletContext = event.getServletContext();

			Registry registry = RegistryEE.get(servletContext);
			// TODO: Move style initialization to -style project, reverse dependency order
			Styles styles = registry.getGroup(STYLE_GROUP).styles;
			styles.add(AoStyle.AO_STYLE);
			styles.add(THEME_CSS);
			styles.add(THEME_PRINT_CSS);

			Styles yuiStyles = registry.getGroup(YUI_GROUP).styles;
			Style treeview = new Style(YUI_PREFIX + "/build/treeview/assets/skins/sam/treeview.css");
			//Style calendar = new Style(YUI_PREFIX + "/build/calendar/assets/skins/sam/calendar.css");//
			Style tree     = new Style(YUI_PREFIX + "/examples/treeview/assets/css/folders/tree.css");
			yuiStyles.add(treeview);
			//yuiStyles.add(calendar);
			yuiStyles.add(tree);
			// treeview -> calendar -> tree
			//yuiStyles.addOrdering(treeview, calendar);
			//yuiStyles.addOrdering(calendar, tree);
			yuiStyles.addOrdering(treeview, tree);

			HtmlRenderer htmlRenderer = HtmlRenderer.getInstance(servletContext);
			// TODO: Return a Script object type instead, with a follow-up of "jQuery.noConflict();"
			htmlRenderer.addScript("jquery", "/webjars/jquery/" + Maven.properties.getProperty("jquery.version") + "/jquery.min.js");
			htmlRenderer.addTheme(new DocumentationTheme());
			// TODO: Move to /META-INF/semanticcms-servlet-space.xml?
			// TODO: Allow semanticcms-servlet-space.xml anywhere in the directory structure?
			FirewallPathSpace.getInstance(servletContext).add(
				// /semanticcms-theme-documentation/*
				FirewallComponent.newInstance(
					valueOf(PREFIX + WILDCARD_SUFFIX),
					// /navigation.js as GET on request only
					isRequest(
						// If ever have more than one .js file, could use endsWith .js, or "map" to avoid sequential scan
						Rules.pathMatch.path.equals("/navigation.js",
							constrain(GET),
							doFilter
						),
						// 404 everything else on "REQUEST" dispatcher
						NOT_FOUND
					),
					// *.inc.jspx as include only
					isInclude(
						Rules.pathMatch.path.endsWith(".inc.jspx", doFilter)
					),
					// *.jspx as forward only, but not including *.inc.jspx
					isForward(
						Rules.pathMatch.path.endsWith(".jspx",
							Rules.pathMatch.path.endsWith(".inc.jspx", FORBIDDEN),
							doFilter
						)
					),
					// Drop everything else
					FORBIDDEN
				),
				// /semanticcms-theme-documentation/error-pages/*
				FirewallComponent.newInstance(
					valueOf(PREFIX + SEPARATOR_CHAR + "error-pages" + WILDCARD_SUFFIX),
					// 404 everything on "REQUEST" dispatcher
					isRequest(FORBIDDEN),
					// Allow via "ERROR" dispatcher
					isError(doFilter),
					// Drop everything else
					FORBIDDEN
				),
				// /semanticcms-theme-documentation/error-pages/jslib/yui-(version)/*** (greedy)
				FirewallComponent.newInstance(
					valueOf(PREFIX + SEPARATOR_CHAR + "jslib" + SEPARATOR_CHAR + "yui-" + YUI_VERSION + GREEDY_SUFFIX),
					// Limit file exensions served on request dispatcher
					isRequest(
						// Block access to *.inc.jspx
						Rules.pathMatch.path.endsWith(".inc.jspx", NOT_FOUND),
						// Restrict all other to "GET"
						constrain(GET),
						doFilter
					),
					// *.inc.jspx as include only (different style than above)
					isInclude(
						Rules.pathMatch.path.endsWith(".inc.jspx", doFilter)
					),
					// Drop everything else
					FORBIDDEN
				),
				// /images/*
				// TODO: Move to docs-taglib:
				FirewallComponent.newInstance(
					// TODO: Use String[] overload of newInstance, once it exists
					new Prefix[] {
						valueOf("/images/*"),
						valueOf("/images/list-item/16x16/*"),
						valueOf("/styles/*")
					},
					// Constraint REQUEST dispatcher to GET only
					isRequest(
						constrain(GET),
						doFilter
					),
					// Drop everything else
					FORBIDDEN
				),
				// /semanticcms-theme-documentation/images/*
				// /semanticcms-theme-documentation/styles/*
				// TODO: Move to -styles project
				FirewallComponent.newInstance(
					// TODO: Use String[] overload of newInstance, once it exists
					new Prefix[] {
						valueOf(PREFIX + SEPARATOR_CHAR + "images" + WILDCARD_SUFFIX),
						valueOf(PREFIX + SEPARATOR_CHAR + "styles" + WILDCARD_SUFFIX)
					},
					// Constraint REQUEST dispatcher to GET only
					isRequest(
						constrain(GET),
						doFilter
					),
					// Drop everything else
					FORBIDDEN
				)
			);
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
		args.put("view", view);
		args.put("page", page);
		Dispatcher.forward(
			servletContext,
			JSPX_TARGET,
			request,
			response,
			args
		);
	}
}
