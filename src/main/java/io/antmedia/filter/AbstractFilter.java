package io.antmedia.filter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.catalina.util.NetMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import io.antmedia.AppSettings;
import io.antmedia.settings.ServerSettings;

public abstract class AbstractFilter implements Filter{

	protected static Logger logger = LoggerFactory.getLogger(AbstractFilter.class);
	protected FilterConfig config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;
	}

	public AppSettings getAppSettings() 
	{
		AppSettings appSettings = null;
		ConfigurableWebApplicationContext context = getAppContext();
		if (context != null) {
			appSettings = (AppSettings)context.getBean(AppSettings.BEAN_NAME);
		}
		return appSettings;
	}

	public ServerSettings getServerSetting() 
	{
		ServerSettings serverSettings = null;
		ConfigurableWebApplicationContext context = getAppContext();
		if (context != null) {
			serverSettings = (ServerSettings)context.getBean(ServerSettings.BEAN_NAME);
		}
		return serverSettings;
	}

	public boolean checkCIDRList(List<NetMask> allowedCIDRList, final String remoteIPAdrress) {
		try {
			InetAddress addr = InetAddress.getByName(remoteIPAdrress);
			for (final NetMask nm : allowedCIDRList) {
				if (nm.matches(addr)) {
					return true;
				}
			}
		} catch (UnknownHostException e) {
			// This should be in the 'could never happen' category but handle it
			// to be safe.
			logger.error("error", e);
		}
		return false;
	}

	public ConfigurableWebApplicationContext getAppContext() {
		ConfigurableWebApplicationContext appContext = (ConfigurableWebApplicationContext) getConfig().getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (appContext != null && appContext.isRunning()) {
			return appContext;
		}
		else {
			if (appContext == null) {
				logger.warn("App context not initialized ");
			}
			else {
				logger.warn("App context not running yet." );
			}
		}

		return null;
	}

	public FilterConfig getConfig() {
		return config;
	}

	public void setConfig(FilterConfig config) {
		this.config = config;
	}



	@Override
	public void destroy() {
		//nothing to destroy
	}
}
