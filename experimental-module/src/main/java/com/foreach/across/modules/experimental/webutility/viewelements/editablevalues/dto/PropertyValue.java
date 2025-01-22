package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.dto;

import com.foreach.across.modules.entity.views.ViewElementMode;
import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class PropertyValue
{
	private final Object value;
	private final Map<ViewElementMode, String> labels;
}
