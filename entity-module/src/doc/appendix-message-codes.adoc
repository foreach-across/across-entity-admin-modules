[[appendix-message-codes]]
=== Message codes

Labels are resolved using a message code hierarchy.
Simply define one or more message sources specifying the properties you want.
Unless custom `EntityMessageCodeResolver` instances are being used, message codes are generated as follows:

|===
|Message code|Description

| enums.*EnumName*.*EnumValue*
| Message code for a single enum value label. +
Example: _enums.Numbers.ONE_

| *EntityPrefix*.name.singular
| Label for an entity in singular form, for use outside or at the beginning of a sentence. +
Example: _UserModule.entities.user.name.singular_

| *EntityPrefix*.name.plural
| Label for an entity in plural form, for use outside or at the beginning of a sentence. +
Example: _UserModule.entities.user.name.plural_

| *EntityPrefix*.name.singular.inline
| Label for an entity in singular form, for use within a sentence. If not explicitly specified, the label is
generated based by lower-casing the non-inline version. +
Example: _UserModule.entities.user.name.singular.inline_

| *EntityPrefix*.name.plural.inline
| Label for an entity in plural form, for use within a sentence.  If not explicitly specified, the label is
 generated based by lower-casing the non-inline version. +
Example: _UserModule.entities.user.name.plural.inline_

| *EntityPrefix*.properties.*propertyName*
| Label for a single entity property. +
Example: _UserModule.entities.user.properties.username_

| *EntityPrefix*.properties.*propertyName*[description]
| Description text for a property.  If not empty this will be rendered in a help block on forms. +
Example: _UserModule.entities.user.properties.username[description]_

| *EntityPrefix*.properties.*propertyName*[placeholder]
| Placeholder text for a property.  Will be used for certain controle like textbox. +
Example: _UserModule.entities.user.properties.username[placeholder]_

| *EntityPrefix*.validation.*validatorKey*
| Description text for a validation error message.  Optionally can be suffixed with the specific property name. +
Example: _UserModule.entities.user.validation.NotBlank_,  _UserModule.entities.user.validation.alreadyExists.username_

| *EntityPrefix*.adminMenu.general
| Name of the _General_ tab.
Usually the first tab that is also opened when creating a new entity.

| *EntityPrefix*.adminMenu.*associationName*
| Name of the tab for that association. +
Example: _UserModule.entities.group.adminMenu.user.groups_

| *EntityPrefix*.actions.*actionName*
| Name of the actions, usually the buttons or links on a page.
Often you just want to replace these on a global level. +
Example: _EntityModule.entities.actions.save_, _UserModule.entities.group.actions.cancel_

| *EntityPrefix*.pageTitle.*pageName*
| Title of the page.
Supports <<message-code-parameters,message code parameters>>. +
Example: _UserModule.entities.user.pageTitle.update=Updating {1}: {2}_

| *EntityPrefix*.pageTitle.*pageName*.subText
| Additional text that should be added as sub text (small) to the page header.
Supports <<message-code-parameters,message code parameters>>.

| *EntityPrefix*.feedback.*feedbackType*
| Feedback message shown for the given feedback type. +
Example: _UserModule.entities.user.feedback.validationErrors_

| *EntityPrefix*.sortableTable.*
| Sortable table results and pager text keys. +
Example: _UserModule.entities.user.sortableTable.resultsFound_

| *EntityPrefix*.delete.*
| Delete view specific messages. +
Example: _UserModule.entities.user.delete.confirmation_

|===

NOTE: _Entity_ codes are camel cased, eg. `CarBrand` would become *carBrand*

.EntityPrefix
Every code requested results in several codes being tried with a number of prefixes:
The following prefixes are tried in oder:

1. (If association view) _ModuleName_.entities._sourceEntityName_.associations[_associationName_]
2. _ModuleName_.entities._entityName_
3. EntityModule.entities._entityName_
4. EntityModule.entities

When rendering a view, the default prefix will be appended with a view type prefix as well.
Usually of the form _views[viewType]_.

Example lookup of property "name" on the default list view for entity "user":

1. MyModule.entities.user.views[listView].properties.name
2. MyModule.entities.user.properties.name
3. MyModule.entities.views[listView].properties.name
4. MyModule.entities.properties.name
3. EntityModule.entities.views[listView].properties.name
4. EntityModule.entities.properties.name

TIP: To get a better insight in the message codes generated, use the entity browser in the developer tools.

.Default messages
See the {bitbucket-url}/resources/messages/entity/EntityModule.properties[source code of EntityModule.properties] for all default message codes used by EntityModule.

[[message-code-parameters]]
.Message code parameters
Some message codes support parameters, if so, the following could be available:

* {0}: entity name
* {1}: entity name inline
* {2}: label of the entity being modified (if known)

.Debugging message code lookups
You can trace the message codes being resolved by setting the logger named *com.foreach.across.modules.entity.support.EntityMessageCodeResolver* to _TRACE_ level.
