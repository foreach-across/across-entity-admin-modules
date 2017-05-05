[[appendix-view-processors]]
=== Default EntityViewProcessors

This chapter lists the general purpose `EntityViewProcessor` classes that are provided by EntityModule.
The <<entity-views,default entity views>> are all built on these processors.

You can use these manually to assemble a `DefaultEntityViewFactory`, though some will be configured automatically if you use one of the builders.

The class javadoc gives more detailed information.
For a full list of all processors available, see the {javadoc-processors}/package-summary.html[package summary].

[cols="1,3",options=header,]
|===

| Name
| Purpose

| {javadoc-processors}/ActionAllowedAuthorizationViewProcessor.html[ActionAllowedAuthorizationViewProcessor]
| Verifies an the entity configuration or association being requested is not hidden, and a configured `AllowableAction` is present.

| {javadoc-processors}/DefaultEntityFetchingViewProcessor.html[DefaultEntityFetchingViewProcessor]
| Uses the default repository or query fetcher to fetch all items for the current entity view context.

| {javadoc-processors}/DefaultValidationViewProcessor.html[DefaultValidationViewProcessor]
| Registers the default `EntityViewCommandValidator` and validates the command object if state changing web request (POST, PUT, DELETE, PATCH) is performed.

| {javadoc-processors}/DelegatingEntityFetchingViewProcessor.html[DelegatingEntityFetchingViewProcessor]
| Fetches items using a configured `Function` or `BiFunction`.

| {javadoc-processors}/EntityPropertyRegistryViewProcessor.html[EntityPropertyRegistryViewProcessor]
| Registers a custom `EntityPropertyRegistry` on the view context.

| {javadoc-processors}/EntityQueryFilterProcessor.html[EntityQueryFilterProcessor]
| Adds an <<entity-query-language,EntityQuery language>> based filter to a list view.
Adds both the form with textbox and fetches the items based on the form values.

| {javadoc-processors}/GlobalFeedbackViewProcessor.html[GlobalFeedbackViewProcessor]
| Renders global feedback on a `PageContentStructure` (admin page).
 Global feedback is usually added using `EntityViewPageHelper`.

| {javadoc-processors}/ListFormViewProcessor.html[ListFormViewProcessor]
| Adds a default form at the top of a list view.
Optionally add a create button.

| {javadoc-processors}/ListPageStructureViewProcessor.html[ListPageStructureViewProcessor]
| Generates the page structure for an entity list view.
Add a page title and publishes the `EntityPageStructureRenderedEvent`.

| {javadoc-processors}/MessagePrefixingViewProcessor.html[MessagePrefixingViewProcessor]
| Configures custom prefixes that should be used for message code resolving.

| {javadoc-processors}/PageableExtensionViewProcessor.html[PageableExtensionViewProcessor]
| Creates a `Pageable` from request parameters and binds it to an `EntityViewCommand` extension.

| {javadoc-processors}/PropertyRenderingViewProcessor.html[PropertyRenderingViewProcessor]
| Renders a list of properties: allows the properties to be configured as well as the `ViewElementMode` for rendering.

| {javadoc-processors}/SingleEntityFormViewProcessor.html[SingleEntityFormViewProcessor]
| Creates a form-based layout for an entity view.
Supports configuring the form grid (defaults to 2 columns), adding default actions (save/cancel) and adding global binding error messages.

| {javadoc-processors}/SingleEntityPageStructureViewProcessor.html[SingleEntityPageStructureViewProcessor]
| Generates the page structure for a single entity.
Adds a page title, builds the entity specific menu (renders it as tabs) and publishes the `EntityPageStructureRenderedEvent`.

| {javadoc-processors}/SortableTableRenderingViewProcessor.html[SortableTableRenderingViewProcessor]
| Generates a sortable table for a list of entities.
Allows several configuration options like properties to render, sorting options etc.

| {javadoc-processors}/TemplateViewProcessor.html[TemplateViewProcessor]
| Configures the name of the template that should be rendered as the result of the controller.

|===

