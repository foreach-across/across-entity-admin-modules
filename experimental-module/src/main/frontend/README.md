# Configuring variables with @foreachbe/react-scripts ^7.0.0

Within this major version of react scripts, variable names have moved to environment variables instead of command options.
This indicates that some options have been refactored into a either an environment variable specified within the corresponding command, or for general configuration have moved to the `.env` file.
When defining environment variables, variables defined in `.env` files will never take precedence over variables in the corresponding command.

For more information about the configuration (and the 7.0.0 release), see:

- https://github.com/ForeachOS/create-react-app/releases/tag/%40foreachbe%2Freact-scripts%407.0.0
- https://foreachos-cra.netlify.app/docs/advanced-configuration
