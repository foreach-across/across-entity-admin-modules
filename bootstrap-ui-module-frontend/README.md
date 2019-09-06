# Frontend resources for BootstrapUiModule

This project builds the frontend resources for BootstrapUiModule. 
It allows you to compile the resources for the following builds/releases:
- WebJars
- Npm Registry
- static JS/CSS resources

## Building for development

When building the local resources for development, the resources will be automatically copied to the correct build path.
For example, when building a webjar, the actual compiled resources will be replaced, so that the changes are applied immediately.

| argument | description |
| -------- | ----------- |
| webjar   | Builds the bundle to be used as a webjar. This  will ensure that the resources are bundles as resource files in the META-INF directory |
| npm      | ...         |
| static   | ...         |

When doing local development each argument can also be prefixed using `watch:` or `build:`. 
This will correspondingly automatically recompile the resources or build them once.
If no prefix is specified, the resources will be watched and rebuild changes automatically.

**NOTE:** to ensure that your resources are automatically reloaded when refreshing the page, ensure that the resources have been build *before* starting the application.


## Building for production

...

## Releasing

...