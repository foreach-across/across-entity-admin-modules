# Experimental module 

This module contains a collection of experimental modules. 

# Features
## Entity controls
EntityControlModule is an extension for EntityModule and provides an easy way to render create controls for a given entity / dto.

Simple example to render controls for a dto
```java
Map<String, ViewElement> userCreateControls = entityControlFactory.createControlsForClass(UserResource.class)
                .forInstance(userResource)
                .build(ctx);
```

For more examples see the `EntityControlFactoryDemoController` demo controller in the test project.