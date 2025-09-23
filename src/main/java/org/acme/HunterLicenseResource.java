package org.acme;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;
import java.util.stream.Collectors;

@Path("/licenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HunterLicenseResource {
    @Context
    UriInfo uriInfo;

    private HunterLicenseRepresentation rep(HunterLicense license){
        return HunterLicenseRepresentation.from(license, uriInfo);
    }

    @GET
    @Operation(summary = "Lista todas as Licenças Hunter")
    @APIResponse(responseCode = "200")
    public Response listAll() {
        List<HunterLicense> licenses = HunterLicense.listAll();
        List<HunterLicenseRepresentation> repList = licenses.stream()
                .map(this::rep)
                .collect(Collectors.toList());
        return Response.ok(repList).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca uma Licença Hunter por ID")
    @APIResponse(responseCode = "200")
    @APIResponse(responseCode = "404", description = "Licença não encontrada")
    public Response getById(@PathParam("id") Long id) {
        HunterLicense license = HunterLicense.findById(id);
        return license != null ? Response.ok(rep(license)).build() : Response.status(404).build();
    }
}
