package org.acme;

import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HunterRepresentation {
    public Long id;
    public String name;
    public int age;
    public Map<String, String> _links;

    public static HunterRepresentation from(Hunter hunter, UriInfo uriInfo) {
        HunterRepresentation rep = new HunterRepresentation();
        rep.id = hunter.id;
        rep.name = hunter.name;
        rep.age = hunter.age;

        URI baseUri = uriInfo.getBaseUri();
        rep._links = new HashMap<>();
        rep._links.put("self", baseUri + "hunters/" + hunter.id);
        rep._links.put("all", baseUri + "hunters");
        rep._links.put("cards", baseUri + "hunters/" + hunter.id + "/cards");

        return rep;
    }
}