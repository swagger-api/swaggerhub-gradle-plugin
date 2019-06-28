package io.swagger.swaggerhub.client;


public class SwaggerHubRequest {
    private final String specification;
    private final String owner;
    private final String version;
    private final String format;
    private final String swagger;
    private final String oas;
    private final String specType;
    private final boolean isPrivate;


    public String getSpecification() {
        return specification;
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

    public String getSpecType() {return specType;}


    private SwaggerHubRequest(Builder builder) {
        this.specification = builder.specification;
        this.owner = builder.owner;
        this.version = builder.version;
        this.format = builder.format;
        this.swagger = builder.swagger;
        this.isPrivate = builder.isPrivate;
        this.oas = builder.oas;
        this.specType = builder.specType;

    }

    public static class Builder {
        private final String specification;
        private final String owner;
        private final String version;
        private String format;
        private String swagger;
        private String oas;
        private String specType;
        private boolean isPrivate;

        public Builder(String specification, String owner, String version) {
            this.specification = specification;
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

        public Builder specType(String specType) {
            this.specType = specType;
            return this;
        }

        public SwaggerHubRequest build() {
            return new SwaggerHubRequest(this);
        }

    }
}
