package org.devproof.mubble.dto.version;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Collection;

/**
 * Data transfer object for version compatibility
 *
 * @author Carsten Hufe
 */
public class MubbleVersion implements Serializable {
    @JsonProperty("current_version")
    private String currentVersion;

    @JsonProperty("compatible_versions")
    private Collection<String> compatibleVersion;

    public MubbleVersion() {
    }

    public MubbleVersion(String currentVersion, Collection<String> compatibleVersion) {
        this.currentVersion = currentVersion;
        this.compatibleVersion = compatibleVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Collection<String> getCompatibleVersion() {
        return compatibleVersion;
    }

    public void setCompatibleVersion(Collection<String> compatibleVersion) {
        this.compatibleVersion = compatibleVersion;
    }

    @Override
    public String toString() {
        return "MubbleVersion{" + "currentVersion='" + currentVersion + '\'' + ", compatibleVersion=" + compatibleVersion + '}';
    }
}
