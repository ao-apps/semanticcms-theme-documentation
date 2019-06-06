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

import com.aoindustries.servlet.http.Dispatcher;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.servlet.Theme;
import com.semanticcms.core.servlet.View;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

/**
 * A theme designed for technical documentation.
 */
public class DocumentationTheme extends Theme {

	static final String THEME_NAME = "semanticcms-theme-documentation";

	static final String PREFIX = "/" + THEME_NAME;

	private static final String JSPX_TARGET = PREFIX + "/theme.inc.jspx";

	// TODO: Version from filtered .xml with maven properties
	static final String YUI_VERSION = "2.9.0";

	@Override
	public String getDisplay() {
		return "SemanticCMS Documentation";
	}

	@Override
	public String getName() {
		return THEME_NAME;
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
