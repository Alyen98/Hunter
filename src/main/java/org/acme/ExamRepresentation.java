package org.acme;

import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ExamRepresentation {
    public Long id;
    public String name;
    public int examYear;
    public Map<String, String> _links;

    public static ExamRepresentation from(Exam exam, UriInfo uriInfo) {
        ExamRepresentation rep = new ExamRepresentation();
        rep.id = exam.id;
        rep.name = exam.name;
        rep.examYear = exam.examYear;

        URI baseUri = uriInfo.getBaseUri();
        rep._links = new HashMap<>();
        rep._links.put("self", baseUri + "exams/" + exam.id);
        rep._links.put("all", baseUri + "exams");
        rep._links.put("participants", baseUri + "exams/" + exam.id + "/hunters");

        return rep;
    }
}