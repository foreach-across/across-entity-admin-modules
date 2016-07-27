package com.foreach.across.modules.entity.config;

import java.util.function.Consumer;

/**
 * @deprecated Use a regular {@link Consumer} instead.
 */
@Deprecated
@FunctionalInterface
public interface PostProcessor<T>
{
	void process( T configuration );
}
