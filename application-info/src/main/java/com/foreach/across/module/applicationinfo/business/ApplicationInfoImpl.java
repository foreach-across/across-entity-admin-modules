package com.foreach.across.module.applicationinfo.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the basic application info
 */
public class ApplicationInfoImpl
{
	private String applicationId;
	private String applicationName;
	private String environment;
	private String hostName;
	private String buildId;

	private Date buildDate, startupDate;

	private Map<String, Object> properties = new HashMap<>();

	public String getApplicationId() {
		return applicationId != null
				? applicationId
				: String.format( "%s-%s:%s", getApplicationName(), getEnvironment(), getHostName() );
	}

	public void setApplicationId( String applicationId ) {
		this.applicationId = applicationId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName( String applicationName ) {
		this.applicationName = applicationName;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment( String environment ) {
		this.environment = environment;
	}

	public Date getBuildDate() {
		return buildDate;
	}

	public void setBuildDate( Date buildDate ) {
		this.buildDate = buildDate;
	}

	public Date getStartupDate() {
		return startupDate;
	}

	public void setStartupDate( Date startupDate ) {
		this.startupDate = startupDate;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName( String hostName ) {
		this.hostName = hostName;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId( String buildId ) {
		this.buildId = buildId;
	}

	public void setProperty( String name, Object value ) {
		properties.put( name, value );
	}

	@SuppressWarnings( "unchecked" )
	public <T> T getProperty( String name ) {
		return (T) properties.get( name );
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "ApplicationInfo{" +
				"applicationId='" + getApplicationId() + '\'' +
				'}';
	}
}
