package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.dto;

import lombok.*;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Error
{
	private final String code;
	private final String message;
}
