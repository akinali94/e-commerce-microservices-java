public class HealthCheckResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("service")
    private String service;
    
    @JsonProperty("version")
    private String version;
    
    public HealthCheckResponse() {
        this.timestamp = Instant.now().toString();
    }
    
    public HealthCheckResponse(String status, String service, String version) {
        this.status = status;
        this.service = service;
        this.version = version;
        this.timestamp = Instant.now().toString();
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getService() {
        return service;
    }
    
    public void setService(String service) {
        this.service = service;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
}