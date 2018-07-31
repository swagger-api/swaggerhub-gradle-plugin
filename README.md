[![Build Status](https://travis-ci.org/swagger-api/swaggerhub-gradle-plugin.svg)](https://travis-ci.org/swagger-api/swaggerhub-gradle-plugin)
# swaggerhub-gradle-plugin
A simple gradle plugin to integrate [SwaggerHub](https:\\swaggerhub.com) hosting of [OpenAPI/Swagger](https://swagger.io/specification/) definitions with a gradle build process, using the [SwaggerHub API](https://app.swaggerhub.com/apis/swagger-hub/registry-api).

## Features
* Download/upload API definitions from/to SwaggerHub.
* Supports `json` and `yaml` format for API definitions.
* Authenticate with API key for restricted operations (e.g downloading a private API definition).
* Connects to SwaggerHub cloud by default or local SwaggerHub instance through optional configuration.

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
### swaggerhubDownload
#### Example Usage
* Download a public API definition in json format from SwaggerHub and save to a local file.
```
swaggerhubDownload {
    api 'PetStoreAPI'
    owner 'jsfrench'
    version '1.0.0'
    outputFile 'target/test/petStoreAPI.json'
}
```

#### Parameters
Parameter | Description | Required | Default
--------- | ----------- | --------- | -------
**`api`** | API name | true  | -
**`owner`** | API owner | true | -
**`version`** | API version | true | -  
**`outputFile`** | API definition is written to this file | true | -
**`token`** | SwaggerHub API key, required to access private definitions | false | -
**`format`** | API definition format, `json` or `yaml` | false | `json`
**`host`** | URL of SwaggerHub API | false | `api.swaggerhub.com`
**`protocol`** | Protocol for SwaggerHub API,`http` or `https` | false | `https`
**`port`** | Port to access SwaggerHub API| false | `443`

***

### swaggerhubUpload
#### Example Usage
* Upload an API definition in json format as a public API in SwaggerHub.

```
swaggerhubUpload {
    api 'PetStoreAPI'
    owner 'jsfrench'
    version '1.0.1-SNAPSHOT'
    inputFile 'target/petStoreAPI.json'
    token  'duMmyAPiKEy'
}
```

#### Parameters
Parameter | Description | Required | Default
--------- | ----------- | --------- | -------
**`api`** | API name | true  | -
**`owner`** | API owner | true | -
**`version`** | API version | true | -  
**`inputFile`** | Local file containing the API definition in json or yaml format  | true | -
**`token`** | SwaggerHub API key | true | -
**`format`** | API definition format, `json` or `yaml` | false | `json`
**`isPrivate`** | Defines whether the API should be private on SwaggerHub (using `true` requires a paid plan) | false | `false`
**`host`** | URL of SwaggerHub API | false | `api.swaggerhub.com`
**`protocol`** | Protocol for SwaggerHub API,`http` or `https` | false | `https`
**`port`** | Port to access SwaggerHub API| false | `443`
