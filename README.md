[![Build Status](https://travis-ci.org/swagger-api/swaggerhub-gradle-plugin.svg)](https://travis-ci.org/swagger-api/swaggerhub-gradle-plugin)
# swaggerhub-gradle-plugin
A simple gradle plugin to access [SwaggerHub](https:\\swaggerhub.com) hosting of [OpenAPI/Swagger](https://swagger.io/specification/) from a gradle build process, primarily to integrate with other OpenAPI/Swagger gradle tooling.

## Features
* Download/upload API definitions from/to SwaggerHub.
* Supports `json` and `yaml` format for API definitions.
* Authenticate with API key for restricted operations (e.g downloading a private API definition).
* Connects to SwaggerHub cloud by default or local SwaggerHub instance through optional configuration.

The pattern of usage is likely to depend on whether a [code first or design first](https://swaggerhub.com/blog/api-design/design-first-or-code-first-api-development/) approach is followed.

## Example use cases

### Code First
1. Code API implementation.
2. Automatically generate API definition from implementation, e.g. via annotations from [swagger-core](https://github.com/swagger-api/swagger-core).
3. Upload generated API definition to SwaggerHub with swaggerhub-gradle-plugin.

### Design First
1. Write API definition (e.g. in Swagger Editor or SwaggerHub).
2. Download API definition with swaggerhub-gradle-plugin.
3. Pass API definition to another Swagger tool e.g.
    - [swagger-codegen-maven-plugin](https://github.com/swagger-api/swagger-codegen/tree/master/modules/swagger-codegen-maven-plugin) to generate API client and resource classes.
    - [swagger-inflector](https://github.com/swagger-api/swagger-inflector) to automatically wire up the API definition to the implementation and provide out-of-the-box mocking.



## Tasks
### swaggerhubDownload
#### Example Usage
* Download a public API definition in json format from SwaggerHub and save to a local file.
```
plugins {
    id 'io.swagger.swaggerhub.gradle.SwaggerHubPlugin'
}
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
plugins {
    id 'io.swagger.swaggerhub.gradle.SwaggerHubPlugin'
}

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
