package org.apache.fineract.tasklist.dto;

import java.util.Set;

public class ClaimTasksResponse {

    private Set<String> successful;
    private Set<String> failed;

    public Set<String> getSuccessful() {
        return successful;
    }

    public void setSuccessful(Set<String> successful) {
        this.successful = successful;
    }

    public Set<String> getFailed() {
        return failed;
    }

    public void setFailed(Set<String> failed) {
        this.failed = failed;
    }
}
