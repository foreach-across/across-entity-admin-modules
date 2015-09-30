package com.foreach.across.module.applicationinfo.business;

import com.foreach.common.spring.context.ApplicationInfo;

import java.util.Date;

public interface AcrossApplicationInfo extends ApplicationInfo
{
	long getBootstrapDuration();

	long getUptime();

	Date getBootstrapStartDate();

	Date getBootstrapEndDate();
}
