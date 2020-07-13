package com.foreach.across.modules.experimental.bulkactions.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkActionSet
{
	private List<BulkAction> bulkActions;

	public static BulkActionSet of( BulkAction... actions ) {
		return new BulkActionSet( List.of( actions ) );
	}
}
