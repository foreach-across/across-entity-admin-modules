[[appendix-entity-configuration-attributes]]
=== EntityConfiguration attributes
The following table lists commonly present attributes on an `EntityConfiguration`.

|===
|Key |Value

|`Repository.class`
|In case of an entity registered through a Spring Data repository.

|`RepositoryFactoryInformation.class`
|In case of an entity registered through a Spring Data repository.

|`PersistentEntity.class`
|In case of an entity registered through a Spring Data repository that exposed `PersistentEntity` information.

|`EntityQueryExecutor.class`
|Holds the `EntityQueryExecutor` that can be used for <<entity-query,custom `EntityQuery` execution>> and will be used by default for fetching entities.
Available if the `Repository` was supported by one of the <<entity-query-executor,default `EntityQueryExecutor` implementations>>.

|`EntityQueryParser.class`
|Holds the `EntityQueryParser` that should be used for <<entity-query-language,parsing EQL statements>> into a valid `EntityQuery`.
Available if the `EntityQueryExecutor.class` attribute is present.

|`EntityAttributes.TRANSACTION_MANAGER_NAME`
|Optionally holds the name of the `PlatformTransactionManager` that the repository for this entity uses.
EntityModule attempts to detect the transaction manager automatically for every Spring Data repository.
When set, this will enable transaction management for the default create, update and delete views.

|`OptionGenerator.class`
|When set on an `EntityConfiguration`, this will be the default generator used to create the set of options that can be selected for a property that points to the entity configuration.
If all you want to configure is the list of possible options, set the `OptionIterableBuilder.class` attribute instead.

|`OptionIterableBuilder.class`
|When set on an `EntityConfiguration`, this will be the default builder used to create the set of options that can be selected for a property that points to the entity configuration.

|`EntityAttributes.OPTIONS_ENTITY_QUERY`
|When set on an `EntityConfiguration`, contains the EQL statement or `EntityQuery` that should be used to fetch the selectable options for a property that points to the entity configuration.
Will only be used if there is no `OptionGenerator.class` or `OptionIterableBuilder.class` attribute set.

|===

[[appendix-entity-property-descriptor-attributes]]
=== EntityPropertyDescriptor attributes
The following table lists commonly present attributes on an `EntityPropertyDescriptor`.

|===
|Key |Value

|`PersistentProperty.class`
|In case of a property of a `PersistentEntity` registered through a Spring Data repository.

|`Sort.Order.class`
|Contains the default `Sort.Order` if sorting is enabled on this property.
By default strings have an order that ignores case.

|`EntityAttributes.CONTROL_NAME`
|Optional: required to be a `String` value.
When present this value will be used as the form control name instead of the descriptor name.

|`EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR`
|When present holds the Java beans property descriptor that was used to create the `EntityPropertyDescriptor`.
The presence of this attribute indicates that the property is not artificial but corresponds to an actual Java class property.

|`OptionGenerator.class`
|When set on an `EntityPropertyDescriptor`, this will be the generator used to create the set of options that can be selected for that property.
If all you want to configure is the list of possible options, set the `OptionIterableBuilder.class` attribute instead.

|`OptionIterableBuilder.class`
|When set on an `EntityPropertyDescriptor`, this will be the builder used to create the set of options that can be selected for that property.

|`EntityAttributes.OPTIONS_ENTITY_QUERY`
|When set on an `EntityPropertyDescriptor`, contains the EQL statement or `EntityQuery` that should be used to fetch the selectable options for that property.
Will only be used if there is no `OptionGenerator.class` or `OptionIterableBuilder.class` attribute set.

|`EntityAttributes.OPTIONS_ALLOWED_VALUES`
|Only applicable if the property is of an enum type.
When set, the attribute holds the `EnumSet` of selectable values.
If you want to customize selection of a non-enum type, see the other option related attributes.
Will only be used if there is no `OptionGenerator.class` or `OptionIterableBuilder.class` attribute set.

|`SelectFormElementConfiguration.class`
|Can hold the configuration instance that should be used when generating a select control for this propery.
Unless a specific `ViewElement` type has been specified, this will force the control type generated to be a select as well.

|===
