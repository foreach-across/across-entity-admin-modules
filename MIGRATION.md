# Spring Boot 2 migration todo

* remove Serializable on repository
* findById with optional instead of findOne -> see about EntityModule signature
* JSON (de-)serialization of EntityQuery?
* improve API, annotate methods that won't return null values
* verify null value for Sort is no longer used