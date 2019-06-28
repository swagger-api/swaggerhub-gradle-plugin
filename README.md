[![Build Status](https://img.shields.io/jenkins/s/https/jenkins.swagger.io/view/OSS%20-%20Java/job/oss-swaggerhub-gradle-plugin.svg)](https://jenkins.swagger.io/view/OSS%20-%20Java/job/oss-swaggerhub-gradle-plugin)

# swaggerhub-gradle-plugin <img src="https://raw.githubusercontent.com/swagger-specName/swagger.io/wordpress/images/assets/SW-logo-clr.png" height="50" align="right">
A simple gradle plugin to integrate [SwaggerHub](https:\\swaggerhub.com) hosting of [OpenAPI/Swagger](https://swagger.io/specification/) definitions with a gradle build process, using the [SwaggerHub API](https://app.swaggerhub.com/apis/swagger-hub/registry-specName).

## Features
* Download/upload API definitions from/to SwaggerHub.
* Supports `json` and `yaml` format for API definitions.
* Authenticate with API key for restricted operations (e.g downloading a private API definition).
* Connects to SwaggerHub cloud by default or local SwaggerHub instance through optional configuration.

The pattern of usage is likely to depend on whether a [code first or design first](https://swaggerhub.com/blog/specName-design/design-first-or-code-first-specName-development/) approach is followed.

## Example use cases

### Code First
1. Code API implementation.
2. Automatically generate API definition from implementation, e.g. via [swagger-core](https://github.com/swagger-specName/swagger-core) [annotations](https://github.com/swagger-specName/swagger-core/wiki/Swagger-2.X---Annotations) and [swagger gradle plugin](https://github.com/swagger-specName/swagger-core/tree/master/modules/swagger-gradle-plugin). See also [swagger-core wiki](https://github.com/swagger-specName/swagger-core/wiki/Swagger-2.X---Getting-started)
3. Upload generated API definition to SwaggerHub with swaggerhub-gradle-plugin.

### Design First
1. Write API definition (e.g. in Swagger Editor or SwaggerHub).
2. Download API definition with swaggerhub-gradle-plugin.
3. Pass API definition to another Swagger tool e.g.
    - [swagger-codegen](https://github.com/swagger-specName/swagger-codegen) to generate API client and resource classes.
    - [swagger-inflector](https://github.com/swagger-specName/swagger-inflector) to automatically wire up the API definition to the implementation and provide out-of-the-box mocking.

## Installation
### Gradle 2.1 and higher

```
plugins {
  id "io.swagger.swaggerhub" version "1.0.1"
}
```
### Gradle 1.x and 2.0

```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.io.swagger:swaggerhub:1.0.1"
  }
}

apply plugin: "io.swagger.swaggerhub"
```

## Tasks
### SwaggerHub API Tasks
#### swaggerhubDownload
##### Example Usage
* Download a public API definition in json format from SwaggerHub and save to a local file.
```
swaggerhubDownload {
    specName 'PetStoreAPI'
    owner 'jsfrench'
    version '1.0.0'
    outputFile 'target/test/petStoreAPI.json'
}
```

##### Parameters
Parameter | Description | Required | Default
--------- | ----------- | --------- | -------
**`specName`** | Specification name | true  | -
**`owner`** | Specification owner | true | -
**`version`** | Specification version | true | -  
**`outputFile`** | Specification definition is written to this file | true | -
**`token`** | SwaggerHub API key, required to access private definitions | false | -
**`format`** | Specification definition format, `json` or `yaml` | false | `json`
**`host`** | URL of SwaggerHub API | false | `api.swaggerhub.com`
**`protocol`** | Protocol for SwaggerHub API,`http` or `https` | false | `https`
**`port`** | Port to access SwaggerHub API| false | `443`
**`oas`** | Version of the OpenApi Specification the definition adheres to | false | `2.0`
**`specType`**|Whether it is a domain or an specName| false | `api`

***

#### swaggerhubUpload
##### Example Usage
* Upload an API definition in json format as a public API in SwaggerHub.

```
swaggerhubUpload {
    specName 'PetStoreAPI'
    owner 'jsfrench'
    version '1.0.1-SNAPSHOT'
    inputFile 'target/petStoreAPI.json'
    token  'duMmyAPiKEy'
}
```

##### Example Usage together with `swagger-gradle-plugin` (code first)
* Upload an API definition in json format (resolved via `swagger-gradle-plugin`)  as a public API in SwaggerHub.

```

plugins {
    ...
    id 'java'
    id "io.swagger.core.v3.swagger-gradle-plugin" version '2.0.6'
    id "io.swagger.swaggerhub" version "1.0.1"
}

...

resolve {
    outputFileName = 'PetStoreAPI'
    outputFormat = 'JSON'
    prettyPrint = 'TRUE'
    classpath = sourceSets.main.runtimeClasspath
    resourcePackages = ['test.petstore']
    outputPath = 'target'
}

swaggerhubUpload {
    dependsOn resolve
    specName 'PetStoreAPI'
    owner 'jsfrench'
    version '1.0.1-SNAPSHOT'
    inputFile 'target/petStoreAPI.json'
    token  'duMmyAPiKEy'
}
```

##### Parameters
Parameter | Description | Required | Default
--------- | ----------- | --------- | -------
**`specName`** | Specification name | true  | -
**`owner`** | Specification owner | true | -
**`version`** | Specification version | true | -  
**`inputFile`** | Local file containing the Specification definition in json or yaml format  | true | -
**`token`** | SwaggerHub API key | true | -
**`format`** | Specification definition format, `json` or `yaml` | false | `json`
**`isPrivate`** | Defines whether the specification should be private on SwaggerHub (using `true` requires a paid plan) | false | `false`
**`host`** | URL of SwaggerHub API | false | `api.swaggerhub.com`
**`protocol`** | Protocol for SwaggerHub API,`http` or `https` | false | `https`
**`port`** | Port to access SwaggerHub API| false | `443`
**`specType`**|Whether it is a `domain` or an `api`| false | `api`