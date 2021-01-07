package com.foreach.across.modules.experimental.webutility.viewelements.createselect;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.FormControlElementSupport;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyles;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.modules.experimental.modals.ui.components.ModalViewElementBuilder;
import com.foreach.across.modules.experimental.webutility.resource.WebUtilityModuleWebResources;
import com.foreach.across.modules.experimental.webutility.support.action.ActionHandlerAttribute;
import com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Collections;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.experimental.modals.support.ModalLoadAttribute.modalLoadAttribute;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionHandlerAttribute.UPDATE_ID_VALUE;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionHandlerAttribute.requestActionHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.ResponseContentHandlerAttribute.responseContentHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionAttribute.simpleAction;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.*;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.data;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@RequiredArgsConstructor
public class CreateSelectViewElementBuilder extends ViewElementBuilderSupport {
    private final ViewElementBuilder originalViewElementBuilder;
    private final SpringTemplateEngine templateEngine;

    @Override
    protected MutableViewElement createElement(ViewElementBuilderContext builderContext) {
        NodeViewElementBuilder wrappedElement = html.builders
                .div(css("create-select-wrapper"))
                .name("create-select-wrapper");

        ViewElement originalElement = originalViewElementBuilder.build(builderContext);
        originalElement.set(css("original-element"));
        wrappedElement.add(originalElement);

        createPlusButtonWithModal(builderContext, wrappedElement, originalElement);

        addWebResources(builderContext);

        return wrappedElement.build(builderContext);
    }

    /**
     * Add a plus button besides the original element. When clicking on the plus button a modal opens to add a new
     * element to the select list.
     */
    private void createPlusButtonWithModal(ViewElementBuilderContext builderContext, NodeViewElementBuilder wrappedElement, ViewElement originalElement) {
        EntityPropertyDescriptor entityPropertyDescriptor = builderContext.getAttribute(EntityPropertyDescriptor.class.getName(), EntityPropertyDescriptor.class);
        EntityViewRequest entityViewRequest = builderContext.getAttribute("entityViewRequest", EntityViewRequest.class);
        EntityViewLinkBuilder entityViewLinkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();
        String urlOfCreateView = entityViewLinkBuilder.root().linkTo(entityPropertyDescriptor.getPropertyType()).createView().toUriString();

        NodeViewElement plusButton = html.builders.button()
                .name("create-select-" + entityPropertyDescriptor.getName())
                .css("ml-2")
                .attribute("type", "button")
                .attribute("aria-label", "Add")
                .add(IconSet.iconSet(ICON_SET_FONT_AWESOME_SOLID).icon("plus"))
                .build(builderContext);
        String modelName = "create-modal-" + entityPropertyDescriptor.getName();

        addAttributesToOpenModal(plusButton, modelName, originalElement, urlOfCreateView, builderContext);

        if (originalElement instanceof FormGroupElement) {
            FormGroupElement formGroupElement = (FormGroupElement) originalElement;

            formGroupElement.setControl(html.div(css("create-select-inner-wrapper d-flex")).addChild(formGroupElement.getControl()).addChild(plusButton));
        } else {
            wrappedElement.add(plusButton);
        }

        wrappedElement.add(createModal(modelName));
    }

    /**
     * Add all the necessary attributes to the plusButton so it opens a modal to the create form of the entity
     * the button is for.
     */
    protected void addAttributesToOpenModal(ViewElement plusButton,
                                            String modelName,
                                            ViewElement originalElement,
                                            String urlOfCreateView,
                                            ViewElementBuilderContext builderContext) {
        plusButton.set(data("toggle", "modal"), data("target", modelName))
                .set(
                        modalLoadAttribute()
                                .target("#" + modelName)
                                .content(
                                        requestAction()
                                                .url(urlOfCreateView)
                                                .partial("content")
                                                .requestConfig(ImmutableMap.of("headers",
                                                        ImmutableMap.of(ModalConfigurers.MODAL_ORIGIN_HEADER, modelName)))
                                                .success(
                                                        clearHandler("#" + modelName + " .modal-title"),
                                                        clearHandler("#" + modelName + " .modal-footer"),
                                                        clearHandler("#" + modelName + " .modal-body"),
                                                        responseContentHandler()
                                                                .source("." + PageContentStructure.CSS_BODY_SECTION)
                                                                .target("#" + modelName + " .modal-body"),
                                                        responseContentHandler()
                                                                .source(".page-header")
                                                                .target("#" + modelName + " .modal-title"),
                                                        responseContentHandler()
                                                                .replace()
                                                                .sourceElement(buildModalSaveButton(urlOfCreateView, modelName, originalElement, builderContext))
                                                                .target("#" + modelName + " #btn-save"),
                                                        responseContentHandler()
                                                                .replace()
                                                                .sourceElement(buildModalCancelButton(modelName, originalElement, builderContext))
                                                                .target("#" + modelName + " #btn-cancel"),
                                                        moveHandler()
                                                                .source("#" + modelName + " .modal-body .em-form-actions")
                                                                .target("#" + modelName + " .modal-footer"),
                                                        initializeFormElements("#" + modelName)
                                                )
                                )
                );
    }

    /**
     * Create the HTML of the cancel button in the modal. This button closes the modal when clicked.
     */
    private String buildModalCancelButton(String modelName, ViewElement originalElement, ViewElementBuilderContext builderContext) {
        EntityViewRequest entityViewRequest = builderContext.getAttribute("entityViewRequest", EntityViewRequest.class);
        EntityMessages messages = entityViewRequest.getEntityViewContext().getEntityMessages();

        LinkViewElement cancelButton = bootstrap.link()
                .setAttribute("data-em-button-role", "cancel")
                .setName("btn-cancel")
                .setHtmlId("btn-cancel")
                .addCssClass("btn btn-link")
                .setText(messages.messageWithFallback("actions.cancel"))
                .set(simpleAction().handlers(closeModalHandler("#" + modelName)));

        return renderViewElement(cancelButton);
    }

    /**
     * Create the HTML of the save button in the modal. This button should do an ajax request to the create form of the
     * entity instead of the normal behavior which is submitting the form.
     */
    private String buildModalSaveButton(String urlOfCreateView, String modelName, ViewElement originalElement, ViewElementBuilderContext builderContext) {
        EntityViewRequest entityViewRequest = builderContext.getAttribute("entityViewRequest", EntityViewRequest.class);
        EntityMessages messages = entityViewRequest.getEntityViewContext().getEntityMessages();

        String originalElementNameName = originalElement.getName();
        String originalControlName = originalElementNameName;

        if (originalElement instanceof FormGroupElement) {
            ViewElement control = ((FormGroupElement) originalElement).getControl();
            originalControlName = control.getName();

            if (control instanceof FormControlElementSupport) {
                originalControlName = ((FormControlElementSupport) control).getControlName();
            }
        }

        ButtonViewElement saveButton = bootstrap.button()
                .setAttribute("data-em-button-role", "save")
                .setName("btn-save")
                .setHtmlId("btn-save")
                .setType(ButtonViewElement.Type.BUTTON_SUBMIT)
                .addCssClass("btn btn-primary")
                .setText(messages.messageWithFallback("actions.save"))
                .set(RequestActionAttribute.requestAction()
                        .url(urlOfCreateView)
                        .method(HttpMethod.POST)
                        .partial("::body")
                        .form("#" + modelName + " .modal-body form")
                        .success(new ActionHandlerAttribute[]{
                                clearHandler("#" + modelName + " .modal-body"),
                                responseContentHandler().target("#" + modelName + " .modal-body"),
                                removeHandler("#" + modelName + " .modal-body .em-form-actions"),
                                initializeFormElements("#" + modelName + " .modal-body")
                        })
                        .redirect(
                                requestActionHandler()
                                        .additionalQueryParameter(originalControlName, UPDATE_ID_VALUE)
                                        .partial("::" + originalElementNameName)
                                        .target(".original-element"),
                                closeModalHandler("#" + modelName),
                                initializeFormElements(".create-select-wrapper")));

        return renderViewElement(saveButton);
    }

    /**
     * Add the resources needed for the modal to open
     */
    private void addWebResources(ViewElementBuilderContext builderContext) {
        WebResourceRegistry webResourceRegistry = builderContext.getAttribute(WebResourceRegistry.class.getName(), WebResourceRegistry.class);
        webResourceRegistry.addPackage(BootstrapUiFormElementsWebResources.NAME);
        webResourceRegistry.apply(
                WebResourceRule.addPackage(WebUtilityModuleWebResources.NAME),
                WebResourceRule.add(
                        WebResource.javascript("@static:/experimental/web/modal-loader.js"))
                        .withKey("modal-loader-js")
                        .after(WebUtilityModuleWebResources.NAME)
                        .toBucket(JAVASCRIPT_PAGE_END)
        );
    }

    /**
     * Create the modal that will be opened when the 'Plus' button is clicked.
     */
    protected ModalViewElementBuilder createModal(String modelName) {
        return new ModalViewElementBuilder()
                .name(modelName)
                .centered(true)
                .header(html.builders.container()
                        .add(html.builders.div(BootstrapStyles.css.modal.title))
                        .add(html.builders.button()
                                .attribute("type", "button")
                                .with(BootstrapStyles.css.close)
                                .data("dismiss", "modal")
                                .attribute("aria-label", "Close")
                                .add(IconSet.iconSet(ICON_SET_FONT_AWESOME_SOLID).icon("times")
                                        .set(AcrossBootstrapStyles.css.text.danger)
                                        .setAttribute("aria-hidden", true))
                        )
                )
                .body()
                .footer();
    }

    /**
     * Helper method to render a {@link ViewElement} to HTML
     *
     * @param viewElement to render
     * @return the html of the viewElement
     */
    public String renderViewElement(ViewElement viewElement) {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        IEngineConfiguration configuration = templateEngine.getConfiguration();
        WebExpressionContext context =
                new WebExpressionContext(
                        configuration,
                        ra.getRequest(),
                        ra.getResponse(),
                        ra.getRequest().getServletContext(),
                        LocaleContextHolder.getLocale(),
                        Collections.singletonMap("element", viewElement)
                );

        return templateEngine.process("th/experimental/inline-view-element", context);
    }
}