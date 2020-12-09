/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.bootstrapui.elements.autosuggest;

import com.foreach.across.modules.bootstrapui.utils.ElementConfigurationMap;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Configuration class for a {@link AutoSuggestFormElementBuilder} based on
 * <a href="https://github.com/twitter/typeahead.js">typeahead.js</a>.
 * <p/>
 * Optimized to represent the different Typeahead configuration options,
 * may hold multiple datasets where a dataset representation is optimized for a Bloodhound datasource.
 *
 * @author Sander Van Loock, Arne Vandamme
 */
public class AutoSuggestFormElementConfiguration extends ElementConfigurationMap<AutoSuggestFormElementConfiguration>
{
	/**
	 * Attribute that holds the collection of dataset definitions.
	 */
	public static final String ATTR_DATASETS = "_datasets";

	/**
	 * Name of the default data set.
	 */
	public static final String DEFAULT_DATASET = "default";

	public static final String DEFAULT_ENDPOINT = "/autosuggest?query={{query}}&field={{controlName}}";

	private static final String ATTR_ENDPOINT = "endpoint";

	public AutoSuggestFormElementConfiguration() {
		highlight( true ).showHint( true ).minLength( 1 ).setAttribute( ATTR_DATASETS, new ArrayList<>() );
	}

	/**
	 * Sets the remote url endpoint where to fetch suggestions.  Defaults to {@link #DEFAULT_ENDPOINT}.
	 * The parameter {value} will be replaced with the actual input text.
	 *
	 * @param endpoint url
	 */
	public void setEndpoint( @NonNull String endpoint ) {
		put( ATTR_ENDPOINT, endpoint );
	}

	/**
	 * @return configured endpoint url
	 */
	public String getEndpoint() {
		return (String) get( ATTR_ENDPOINT );
	}

	/**
	 * Should the textbox show a hint for auto-completion.
	 *
	 * @param showHint to show
	 * @return current configuration
	 */
	public AutoSuggestFormElementConfiguration showHint( boolean showHint ) {
		return setAttribute( "hint", showHint );
	}

	/**
	 * Highlight the matching input text in the suggestions.
	 *
	 * @param highlight match
	 * @return current configuration
	 */
	public AutoSuggestFormElementConfiguration highlight( boolean highlight ) {
		return setAttribute( "highlight", highlight );
	}

	/**
	 * Minimum length the input value should have before fetching suggestiong.
	 *
	 * @param minLength input should have
	 * @return current configuration
	 */
	public AutoSuggestFormElementConfiguration minLength( int minLength ) {
		return setAttribute( "minLength", minLength );
	}

	/**
	 * Configure the dataset with the given name, if it is not yet present, it will be added first.
	 *
	 * @param dataSetName name of the dataset
	 * @param consumer    to configure the dataset
	 * @return current configuration
	 */
	public AutoSuggestFormElementConfiguration withDataSet( String dataSetName, Consumer<DataSet> consumer ) {
		DataSet dataSet = getDataSet( dataSetName );
		if ( dataSet == null ) {
			dataSet = createDataSet().name( dataSetName );
			addDataSet( dataSet );
		}
		consumer.accept( dataSet );
		return this;
	}

	/**
	 * Remove the dataset with the given name if it is present.
	 *
	 * @param dataSetName name of the dataset to remove
	 * @return current configuration
	 */
	public AutoSuggestFormElementConfiguration removeDataSet( String dataSetName ) {
		DataSet ds = getDataSet( dataSetName );
		if ( ds != null ) {
			getDataSets().remove( ds );
		}
		return this;
	}

	/**
	 * Add a dataset to the collection, if there is another dataset with that name already, it will be removed first.
	 *
	 * @param dataSet to add to the collection
	 * @return current configuration
	 */
	public AutoSuggestFormElementConfiguration addDataSet( @NonNull DataSet dataSet ) {
		removeDataSet( dataSet.getName() );
		getDataSets().add( dataSet );
		return this;
	}

	private DataSet getDataSet( String dataSetName ) {
		List<DataSet> dataSets = getDataSets();
		for ( DataSet ds : dataSets ) {
			if ( StringUtils.equals( ds.getName(), dataSetName ) ) {
				return ds;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<DataSet> getDataSets() {
		return (List<DataSet>) get( ATTR_DATASETS );
	}

	/**
	 * Create a new instance of this configuration, where the links have been translated by the specified builder.
	 *
	 * @param linkBuilder to use
	 * @return new copy of the configuration
	 */
	public AutoSuggestFormElementConfiguration translate( Function<String, String> linkBuilder ) {
		AutoSuggestFormElementConfiguration clone = new AutoSuggestFormElementConfiguration();
		clone.putAll( this );
		List<DataSet> dataSets = clone.getDataSets();
		List<DataSet> translated = new ArrayList<>( dataSets.size() );
		dataSets.stream().map( ds -> ds.translate( linkBuilder ) ).forEach( translated::add );
		clone.put( ATTR_DATASETS, translated );

		return clone;
	}

	/**
	 * Create a new configuration with a default dataset.
	 *
	 * @param consumer to customize the dataset
	 * @return current configuration
	 */
	public static AutoSuggestFormElementConfiguration withDataSet( Consumer<DataSet> consumer ) {
		return new AutoSuggestFormElementConfiguration().withDataSet( DEFAULT_DATASET, consumer );
	}

	/**
	 * @return a new blank dataset
	 */
	public static DataSet createDataSet() {
		return new DataSet();
	}

	/**
	 * Represents a single dataset.
	 */
	public static class DataSet extends ElementConfigurationMap<DataSet>
	{
		private DataSet() {
			name( UUID.randomUUID().toString() );
			setAttribute( "bloodhound", new ElementConfigurationMap<>() );
		}

		String getName() {
			return (String) get( "name" );
		}

		public DataSet name( String name ) {
			return setAttribute( "name", name );
		}

		/**
		 * Short-hand for setting the remote url.
		 *
		 * @param remoteUrl url for fetching remote suggestions
		 * @return current dataset
		 */
		public DataSet remoteUrl( String remoteUrl ) {
			return remote( options -> options.setAttribute( "url", remoteUrl ) );
		}

		/**
		 * Short-hand for setting the max number of results on the dataset
		 */
		public DataSet maximumResults( Integer maximumResults ) {
			setAttribute( "limit", maximumResults );
			return this;
		}

		/**
		 * Short-hand for setting the prefetch url.
		 *
		 * @param prefetchUrl url for prefetch
		 * @return current dataset
		 */
		public DataSet prefetchUrl( String prefetchUrl ) {
			return prefetch( options -> options.setAttribute( "url", prefetchUrl ) );
		}

		/**
		 * Configure the basic bloodhound options.
		 *
		 * @see #remote(Consumer)
		 * @see #prefetch(Consumer)
		 */
		public DataSet bloodhound( Consumer<ElementConfigurationMap> consumer ) {
			consumer.accept( getBloodhound() );
			return this;
		}

		/**
		 * Configure the remote options.
		 *
		 * @param consumer to configure the options
		 * @return current dataset
		 */
		public DataSet remote( Consumer<ElementConfigurationMap> consumer ) {
			ElementConfigurationMap options = (ElementConfigurationMap) getBloodhound().computeIfAbsent( "remote", key -> new ElementConfigurationMap() );
			consumer.accept( options );
			return this;
		}

		/**
		 * Configure the prefetch options.
		 *
		 * @param consumer to configure the options
		 * @return current dataset
		 */
		public DataSet prefetch( Consumer<ElementConfigurationMap> consumer ) {
			ElementConfigurationMap options = (ElementConfigurationMap) getBloodhound().computeIfAbsent( "prefetch", key -> new ElementConfigurationMap() );
			consumer.accept( options );
			return this;
		}

		private ElementConfigurationMap<?> getBloodhound() {
			return (ElementConfigurationMap) get( "bloodhound" );
		}

		/**
		 * Set an attribute on the configuration. Same as calling {@link #put(Object, Object)} except suitable for fluent API,
		 * because it returns the same instance.
		 *
		 * @param key   attribute key
		 * @param value attribute value
		 * @return current dataset
		 */
		public DataSet setAttribute( String key, Object value ) {
			put( key, value );
			return this;
		}

		/**
		 * Create a new copy of this dataset, where all links have been translated using the specified builder.
		 *
		 * @param linkBuilder to use on the urls
		 * @return new dataset
		 */
		@SuppressWarnings("unchecked")
		public DataSet translate( Function<String, String> linkBuilder ) {
			DataSet ds = new DataSet();
			ds.putAll( this );
			val bloodhound = new ElementConfigurationMap();
			bloodhound.putAll( ds.getBloodhound() );
			ds.put( "bloodhound", bloodhound );

			translateOptions( bloodhound, "remote", linkBuilder );
			translateOptions( bloodhound, "prefetch", linkBuilder );

			return ds;
		}

		@SuppressWarnings("unchecked")
		private void translateOptions( ElementConfigurationMap bloodhound, String optionsKey, Function<String, String> linkBuilder ) {
			if ( bloodhound.containsKey( optionsKey ) ) {
				val options = new ElementConfigurationMap();
				options.putAll( (ElementConfigurationMap) bloodhound.get( optionsKey ) );
				bloodhound.put( optionsKey, options );

				if ( options.containsKey( "url" ) ) {
					options.put( "url", linkBuilder.apply( (String) options.get( "url" ) ) );
				}
			}
		}
	}
}
