[[form-view]]
=== Form view

==== create and update view

default settings of a form view:

- user must have update allowable action
- renders values as LIST_VALUE
- shows all readable properties
- adds update/delete buttons for every item if the user as update and delete action respectively
- supports paging and sorting
- allows configuring sortable properties and the default sort
- includes a form at the top that can be used for adding filters
- a create button for the entity if the user has create action allowed
- supports global feedback messages set with the `EntityViewPageHelper`

default processors


[delete-view]]
=== Delete view

default settings

default processors

A delete action will be available for all entities where `AllowableAction.DELETE` is present, this is the default unless more explicit permissions are configured.
A delete will always redirect to a confirmation page by default.
Because the possibility to delete an entity often depends on other factors (usually associations), the default `EntityDeleteViewFactory` publishes an event that allows customizing said confirmation page.

By catching the `BuildEntityDeleteViewEvent` your code can:

* suppress the ability to delete (by hiding the delete button)
* add associations to the form
* add custom feedback messages to the form (and optionally remove the associations block)

This should be sufficient for most use cases without having to revert to custom `EntityViewProcessor` implementations.
Of course the latter would work as well.

.Entity associations
The initial `BuildEntityDeleteViewEvent` is configured based on the `EntityAssociation` list of the entity.
If associated items are detected, they influence the form settings depending on the *parentDeleteMode* property of the `EntityAssociation`:

* `ParentDeleteMode.IGNORE`: item information is not printed nor influences the ability to delete
* `ParentDeleteMode.WARN`: item information is printed on the form but does not influence the ability to delete
* `ParentDeleteMode.SUPPRESS`: item information is printed on the form and disables the ability to delete, this is the default setting

The event is published after the initial association information has been set.

.Performing the delete
The {module-name} simply calls the delete method of the `EntityModel`, usually a direct call to a repository `delete()`.
You will have to take care yourself of complex delete scenarios - like deleting the associations - by either modifying the `EntityModel` or using another mechanism like the `EntityInterceptor`.

=== creating an additional form view