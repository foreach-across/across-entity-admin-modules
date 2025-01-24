package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.dto;

import lombok.*;

import java.util.Map;
import java.util.Set;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class UpdateResponse
{
	private final boolean success;
	private final Map<String, Object> data;
	private final Map<String, PropertyValue> properties;
	private final Map<String, PropertyValue> absoluteProperties;
	private final Map<String, Set<Error>> errors;
}
