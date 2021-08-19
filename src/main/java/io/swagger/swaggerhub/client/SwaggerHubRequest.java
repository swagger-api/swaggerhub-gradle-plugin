package io.swagger.swaggerhub.client;


public class SwaggerHubRequest {
    private final String api;
    private final String owner;
    private final String version;
    private final String format;
    private final String swagger;
    private final String oas;
    private final boolean isPrivate;
    private final boolean isResolved;
    private final boolean isFlatten;

    public String getApi() {
        return api;
    }

    public String getOwner() {
        return owner;
    }

    public String getVersion() {
        return version;
    }

    public String getFormat() {
        return format;
    }

    public String getSwagger() {
        return swagger;
    }

    public String getOas() { return oas; }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public boolean isFlatten() {
        return isFlatten;
    }

    private SwaggerHubRequest(Builder builder) {
        this.api = builder.api;
        this.owner = builder.owner;
        this.version = builder.version;
        this.format = builder.format;
        this.swagger = builder.swagger;
        this.isPrivate = builder.isPrivate;
        this.oas = builder.oas;
        this.isResolved = builder.isResolved;
        this.isFlatten = builder.isFlatten;
    }

    public static class Builder {
        private final String api;
        private final String owner;
        private final String version;
        private String format;
        private String swagger;
        private String oas;
        private boolean isPrivate;
        private boolean isResolved;
        private boolean isFlatten;

        public Builder(String api, String owner, String version) {
            this.api = api;
            this.owner = owner;
            this.version = version;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder swagger(String swagger) {
            this.swagger = swagger;
            return this;
        }

        public Builder isPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder oas(String oas) {
            this.oas = oas;
            return this;
        }

        public Builder isResolved(boolean isResolved) {
            this.isResolved = isResolved;
            return this;
        }

        public Builder isFlatten(boolean isFlatten) {
            this.isFlatten = isFlatten;
            return this;
        }

        public SwaggerHubRequest build() {
            return new SwaggerHubRequest(this);
        }

    }
}
