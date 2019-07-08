package io.jenkins.plugins.appcenter.model;

public class DestinationId {
    public final String name;
    public final String id;

    public DestinationId(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "DestinationId{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
