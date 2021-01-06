package com.foreach.across.modules.experimental.webutility.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleActionHandlerAttribute<SELF extends SimpleActionHandlerAttribute<SELF>> extends ActionHandlerAttribute<SELF> {
    /**
     * Optional CSS3 selector of the source element holding the input for the handler.
     */
    @JsonProperty
    private String source;

    /**
     * Optional html content of the source element holding the input for the handler.
     */
    @JsonProperty
    private String sourceElement;

    /**
     * CSS3 selector of the target element to which the handler should be applied.
     */
    @JsonProperty
    @NonNull
    private String target;

    public SELF source(String source) {
        this.source = source;
        return self();
    }

    public SELF sourceElement(String sourceElement) {
        this.sourceElement = sourceElement;
        return self();
    }

    public SELF target(String target) {
        this.target = target;
        return self();
    }

    @SuppressWarnings("unchecked")
    public static <T extends SimpleActionHandlerAttribute<T>> SimpleActionHandlerAttribute<T> simpleActionHandler() {
        return (T) new SimpleActionHandlerAttribute();
    }

    /**
     * {@link SimpleActionHandlerAttribute} that moves the source element into the target element.
     */
    public static SimpleActionHandlerAttribute moveHandler() {
        return simpleActionHandler()
                .type(Type.MOVE);
    }

    /**
     * {@link SimpleActionHandlerAttribute} that removes the target element from the DOM.
     */
    public static SimpleActionHandlerAttribute removeHandler(String target) {
        return simpleActionHandler()
                .type(Type.REMOVE)
                .target(target);
    }

    /**
     * {@link SimpleActionHandlerAttribute} that removes the content of the target element from the DOM.
     */
    public static SimpleActionHandlerAttribute clearHandler(String target) {
        return simpleActionHandler()
                .type(Type.CLEAR)
                .target(target);
    }

    /**
     * {@link SimpleActionHandlerAttribute} that closes the bootstrap modal. The target refers to the CSS3 selector of the modal element, usually it's id.
     */
    public static SimpleActionHandlerAttribute closeModalHandler(String target) {
        return simpleActionHandler()
                .type(Type.CLOSE_MODAL)
                .target(target);
    }

    /**
     * {@link SimpleActionHandlerAttribute} that executes {@code EntityModule#initializeFormElements} on the target element.
     */
    public static SimpleActionHandlerAttribute initializeFormElements(String target) {
        return simpleActionHandler()
                .type(Type.INITIALIZE_ELEMENTS)
                .target(target);
    }

    public interface Type {
        String RESPONSE_CONTENT = "exm:response-content";
        String MOVE = "exm:move";
        String CLEAR = "exm:clear";
        String REMOVE = "exm:remove";
        String CLOSE_MODAL = "exm:modal:close";
        String INITIALIZE_ELEMENTS = "exm:initialize-elements";
    }
}
