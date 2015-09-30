package com.foreach.across.module.applicationinfo.business;

import com.foreach.common.spring.context.MutableApplicationInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class AcrossApplicationInfoImpl implements MutableApplicationInfo, AcrossApplicationInfo
{
	private String applicationId, applicationName, environmentId, environmentName, buildId, hostName, instanceId;
	private Date buildDate, startupDate, bootstrapStartDate, bootstrapEndDate;

	@Override
	public String getApplicationId() {
		return applicationId;
	}

	@Override
	public void setApplicationId( String applicationId ) {
		this.applicationId = applicationId;

		if ( applicationName == null ) {
			setApplicationName( applicationId );
		}
	}

	@Override
	public String getApplicationName() {
		return applicationName;
	}

	@Override
	public void setApplicationName( String applicationName ) {
		this.applicationName = applicationName;
	}

	@Override
	public String getEnvironmentId() {
		return environmentId;
	}

	@Override
	public void setEnvironmentId( String environmentId ) {
		this.environmentId = environmentId;

		if ( environmentName == null ) {
			setEnvironmentName( environmentId );
		}
	}

	@Override
	public String getEnvironmentName() {
		return environmentName;
	}

	@Override
	public void setEnvironmentName( String environmentName ) {
		this.environmentName = environmentName;
	}

	@Override
	public String getBuildId() {
		return buildId;
	}

	@Override
	public void setBuildId( String buildId ) {
		this.buildId = buildId;
	}

	@Override
	public Date getBuildDate() {
		return buildDate;
	}

	@Override
	public void setBuildDate( Date buildDate ) {
		this.buildDate = buildDate;
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public void setHostName( String hostName ) {
		this.hostName = hostName;
	}

	@Override
	public String getInstanceId() {
		if ( instanceId == null ) {
			instanceId = String.format( "%s-%s-%s", getApplicationId(), getEnvironmentId(), getHostName() );
		}
		return instanceId;
	}

	@Override
	public void setInstanceId( String instanceId ) {
		this.instanceId = instanceId;
	}

	@Override
	public Date getStartupDate() {
		return startupDate;
	}

	@Override
	public void setStartupDate( Date startupDate ) {
		this.startupDate = startupDate;
	}

	@Override
	public Date getBootstrapStartDate() {
		return bootstrapStartDate;
	}

	public void setBootstrapStartDate( Date bootstrapStartDate ) {
		this.bootstrapStartDate = bootstrapStartDate;
	}

	@Override
	public Date getBootstrapEndDate() {
		return bootstrapEndDate;
	}

	public void setBootstrapEndDate( Date bootstrapEndDate ) {
		this.bootstrapEndDate = bootstrapEndDate;
	}

	@Override
	public long getUptime() {
		Date bootstrapFinished = getBootstrapEndDate();

		return bootstrapFinished != null ? System.currentTimeMillis() - bootstrapFinished.getTime() : 0;
	}

	@Override
	public long getBootstrapDuration() {
		Date bootstrapStarted = getBootstrapStartDate();
		Date bootstrapFinished = getBootstrapEndDate();

		return bootstrapStarted != null && bootstrapFinished != null
				? bootstrapFinished.getTime() - bootstrapStarted.getTime() : 0;
	}

	/**
	 * @param environmentId Id of the environment to check against.
	 * @return True if the application is running in that environment.
	 */
	@Override
	public boolean isRunningIn( String environmentId ) {
		return StringUtils.equalsIgnoreCase( getEnvironmentId(), environmentId );
	}

	@Override
	public String toString() {
		return "AcrossApplicationInfo{" +
				"instanceId='" + instanceId + '\'' +
				'}';
	}
}
