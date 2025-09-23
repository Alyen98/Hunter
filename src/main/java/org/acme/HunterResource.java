package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/hunters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HunterResource {
    @Context
    UriInfo uriInfo;

    private HunterRepresentation rep(Hunter h) {
        return HunterRepresentation.from(h, uriInfo);
    }

    private List<HunterRepresentation> repList(List<Hunter> hunters) {
        return hunters.stream().map(this::rep).collect(Collectors.toList());
    }

    @GET
    @Operation(summary = "Lista todos os Hunters com paginação")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = HunterRepresentation.class)))
    public Response list(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("size") @DefaultValue("10") int size) {
        PanacheQuery<Hunter> query = Hunter.findAll(Sort.by("name"));
        List<Hunter> hunters = query.page(Page.of(page - 1, size)).list();
        return Response.ok(repList(hunters)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca um Hunter por ID")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = HunterRepresentation.class)))
    @APIResponse(responseCode = "404", description = "Hunter não encontrado")
    public Response getById(@PathParam("id") long id) {
        Hunter hunter = Hunter.findById(id);
        return hunter != null ? Response.ok(rep(hunter)).build() : Response.status(404).build();
    }

    @GET
    @Path("/{id}/cards")
    @Operation(summary = "Lista os cards de um Hunter específico")
    @APIResponse(responseCode = "200")
    @APIResponse(responseCode = "404", description = "Hunter não encontrado")
    public Response getHunterCards(@PathParam("id") Long id) {
        Hunter hunter = Hunter.findById(id);
        if (hunter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<CardRepresentation> cardReps = hunter.cards.stream()
                .map(card -> CardRepresentation.from(card, uriInfo))
                .collect(Collectors.toList());
        return Response.ok(cardReps).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Cria um novo Hunter (forma simples)")
    @APIResponse(responseCode = "201", description = "Hunter criado")
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public Response create(@Valid Hunter hunter) {
        hunter.persist();
        return Response.status(201).entity(rep(hunter)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualiza um Hunter existente")
    @APIResponse(responseCode = "200", description = "Hunter atualizado")
    @APIResponse(responseCode = "404", description = "Hunter não encontrado")
    public Response update(@PathParam("id") long id, @Valid Hunter newHunterData) {
        Hunter hunter = Hunter.findById(id);
        if (hunter == null) return Response.status(404).build();
        hunter.name = newHunterData.name;
        hunter.age = newHunterData.age;
        return Response.ok(rep(hunter)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Deleta um Hunter")
    @APIResponse(responseCode = "204", description = "Hunter deletado")
    @APIResponse(responseCode = "404", description = "Hunter não encontrado")
    public Response delete(@PathParam("id") long id) {
        Hunter hunter = Hunter.findById(id);
        if (hunter == null) return Response.status(404).build();
        hunter.delete();
        return Response.noContent().build();
    }

    @POST
    @Path("/full")
    @Transactional
    @Operation(summary = "Cria um novo Hunter com seus cards e associações a exames")
    @APIResponse(responseCode = "201", description = "Hunter completo criado")
    @APIResponse(responseCode = "400", description = "Dados inválidos ou ID de exame não encontrado")
    public Response createFullHunter(HunterCreationRequest request) {
        Hunter hunter = new Hunter();
        hunter.name = request.name;
        hunter.age = request.age;

        if (request.cards != null) {
            hunter.cards = request.cards.stream().map(cardReq -> {
                Card card = new Card();
                card.nenAbility = cardReq.nenAbility;
                card.nenType = cardReq.nenType;
                card.exam = cardReq.exam;
                card.hunter = hunter;
                return card;
            }).collect(Collectors.toList());
        }

        if (request.examIds != null) {
            Set<Exam> exams = new HashSet<>(Exam.list("id in ?1", request.examIds));
            if (exams.size() != request.examIds.size()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Um ou mais IDs de exames fornecidos não foram encontrados.").build();
            }
            hunter.exams = exams;
        }

        hunter.persist();

        return Response.status(Response.Status.CREATED).entity(rep(hunter)).build();
    }
}