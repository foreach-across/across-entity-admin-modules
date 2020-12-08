package com.foreach.across.modules.experimental.webutility.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseContentHandlerAttribute<SELF extends ResponseContentHandlerAttribute<SELF>> extends SimpleActionHandlerAttribute<SELF> {
    /**
     * When true, the target will be cleared before inserting the source
     */
    @JsonProperty
    @NonNull
    private boolean replace = false;
    ResponseContentHandlerAttribute() {
        this.type(Type.RESPONSE_CONTENT);
    }
    public SELF responseContentHandlerAttribute(){
        return new ResponseContentHandlerAttribute().self();
    }
    public SELF replace() {
        replace = true;
        return self();
    }
    public SELF insert() {
        replace = false;
        return self();
    }
}