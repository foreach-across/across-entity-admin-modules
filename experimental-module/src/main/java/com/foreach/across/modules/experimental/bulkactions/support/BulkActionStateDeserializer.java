package com.foreach.across.modules.experimental.bulkactions.support;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;
import java.util.Collections;
import java.util.Set;

/**
 * Helper class to parse and decode the bulk action state
 * This will always return a set of the provided type {@param type}
 */
public class BulkActionStateDeserializer
{
	static final ObjectMapper objectMapper = new ObjectMapper();

	private static final Base64.Decoder decoder = Base64.getDecoder();

	@SneakyThrows
	@SuppressWarnings("unchecked")
	public static <T> Set<T> parseBulkActionState( String value, Class<T> type ) {
		if ( StringUtils.isBlank( value ) ) {
			return Collections.emptySet();
		}
		JavaType itemType = objectMapper.getTypeFactory().constructCollectionType( Set.class, type );
		return (Set<T>) objectMapper.readValue( decoder.decode( value ), itemType );
	}
}
