/*
 * semanticcms-theme-documentation - SemanticCMS theme tailored for technical documentation.
 * Copyright (C) 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.lang.Projects;
import com.aoindustries.util.PropertiesUtils;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author  AO Industries, Inc.
 */
public class Maven {

	@WebListener
	public static class Initializer implements ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent event) {
			event.getServletContext().setAttribute(Maven.class.getName(), new Maven());
		}

		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// Nothing to do
		}
	}

	static final String jqueryVersion;

	static {
		try {
			Properties properties = PropertiesUtils.loadFromResource(Maven.class, "Maven.properties");
			jqueryVersion = Projects.getVersion("org.webjars.npm", "jquery", properties.getProperty("jqueryVersion"));
		} catch(IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private Maven() {}

	public String getJqueryVersion() {
		return jqueryVersion;
	}
}
