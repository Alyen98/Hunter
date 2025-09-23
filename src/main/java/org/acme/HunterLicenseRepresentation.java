package org.acme;

import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HunterLicenseRepresentation {
    public Long id;
    public String licenseNumber;
    public LocalDate issueDate;
    public Map<String, String> _links;

    public static HunterLicenseRepresentation from(HunterLicense license, UriInfo uriInfo) {
        HunterLicenseRepresentation rep = new HunterLicenseRepresentation();
        rep.id = license.id;
        rep.licenseNumber = license.licenseNumber;
        rep.issueDate = license.issueDate;

        URI baseUri = uriInfo.getBaseUri();
        rep._links = new HashMap<>();
        rep._links.put("self", baseUri + "licenses/" + license.id);
        if (license.hunter != null) {
            rep._links.put("hunter", baseUri + "hunters/" + license.hunter.id);
        }
        return rep;
    }
}
