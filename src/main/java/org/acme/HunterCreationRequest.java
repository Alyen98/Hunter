package org.acme;

import java.util.List;
import java.util.Set;

public class HunterCreationRequest {
    public String name;
    public int age;

    public List<CardCreationRequest> cards;

    public Set<Long> examIds;
}
