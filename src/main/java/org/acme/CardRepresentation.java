package org.acme;

import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CardRepresentation {
    public Long id;
    public String hunterName; // Alterado de 'hunter' para 'hunterName'
    public String nenability;
    public String exam;
    public NenType nentype;
    public Map<String, String> _links;

    public CardRepresentation() {
    }

    public static CardRepresentation from(Card card, UriInfo uriInfo) {
        CardRepresentation rep = new CardRepresentation();
        rep.id = card.id;
        rep.hunterName = (card.hunter != null) ? card.hunter.name : null;
        rep.nenability = card.nenAbility;
        rep.exam = card.exam;
        rep.nentype = card.nenType;

        rep._links = new HashMap<>();
        URI baseUri = uriInfo.getBaseUri();

        rep._links.put("self", baseUri + "cards/" + card.id);
        if (card.hunter != null) {
            rep._links.put("hunter", baseUri + "hunters/" + card.hunter.id);
        }
        rep._links.put("all", baseUri + "cards");

        return rep;
    }
}