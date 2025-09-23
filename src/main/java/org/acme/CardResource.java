package org.acme;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.MediaType;

@Path("/cards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CardResource {
    @Context
    UriInfo uriInfo;

    private CardRepresentation rep(Card b){
        return CardRepresentation.from(b, uriInfo);
    }

    private List<CardRepresentation> repList(List<Card> cards){
        return cards.stream().map(this::rep).collect(Collectors.toList());
    }

    @GET
    @Operation(
            summary = "Retorna todas as cartas nen com paginação opcional",
            description = "Retorna uma lista de cartas nen por padrão no formato JSON, suportando paginação."
    )
    @APIResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = SchemaType.ARRAY, implementation = CardRepresentation.class))
    )
    public Response list(
            @QueryParam("page") @Min(value = 1) @DefaultValue("1") int page,
            @QueryParam("size") @Min(value = 1) @DefaultValue("10") int size
    ) {
        PanacheQuery<Card> allCards = Card.findAll();
        List<Card> cards = allCards.page(Page.of(page - 1, size)).list();
        return Response.ok(repList(cards)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca uma carta por ID")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CardRepresentation.class)))
    @APIResponse(responseCode = "404", description = "Carta não encontrada")
    public Response getById(
            @Parameter(description = "Id da carta nen a ser pesquisado", required = true)
            @PathParam("id") long id){
        Card entity = Card.findById(id);
        return entity != null ? Response.ok(rep(entity)).build() : Response.status(404).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Busca cartas com filtros, ordenação e paginação")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SearchCardResponse.class)))
    public Response search(
            @Parameter(description = "Termo de busca para hunter, habilidade ou tipo nen") @QueryParam("q") String q,
            @Parameter(description = "Campo para ordenação") @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Direção da ordenação (asc/desc)") @QueryParam("direction") @DefaultValue("asc") String direction,
            @Parameter(description = "Número da página") @QueryParam("page") @Min(value = 1) @DefaultValue("1") int page,
            @Parameter(description = "Tamanho da página") @QueryParam("size") @Min(value = 1) @DefaultValue("10") int size) {

        Set<String> allowed = Set.of("id", "hunter.name", "nenAbility", "nenType", "exam");
        if (!allowed.contains(sort)) {
            sort = "id";
        }
        Sort sortObj = Sort.by(sort, "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending);
        int effectivePage = page - 1;

        PanacheQuery<Card> query = (q == null || q.isBlank())
                ? Card.findAll(sortObj)
                : Card.find("lower(hunter.name) like ?1 or lower(nenAbility) like ?1 or lower(nenType) like ?1",
                sortObj,
                "%" + q.toLowerCase() + "%");

        long totalElements = query.count();
        long totalPages = (long) Math.ceil((double) totalElements / size);
        List<Card> cards = query.page(effectivePage, size).list();
        SearchCardResponse response = SearchCardResponse.from(cards, uriInfo, q, sort, direction, page, size, totalElements, totalPages);
        return Response.ok(response).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Cria uma nova carta nen")
    @RequestBody(required = true, content = @Content(schema = @Schema(implementation = Card.class)))
    @APIResponse(responseCode = "201", description = "Carta criada")
    @APIResponse(responseCode = "400", description = "Requisição inválida (ex: hunterId não encontrado)")
    public Response insert(@Valid Card card){
        if (card.hunter == null || Hunter.findById(card.hunter.id) == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Hunter associado não existe.").build();
        }
        card.persist();
        return Response.status(201).entity(rep(card)).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Deleta uma carta")
    @APIResponse(responseCode = "204", description = "Carta deletada")
    @APIResponse(responseCode = "404", description = "Carta não encontrada")
    public Response delete(@PathParam("id") long id){
        boolean deleted = Card.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(404).build();
    }

    @PUT
    @Transactional
    @Path("/{id}")
    @Operation(summary = "Atualiza uma carta existente")
    @APIResponse(responseCode = "200", description = "Carta atualizada")
    @APIResponse(responseCode = "404", description = "Carta não encontrada")
    public Response update(@PathParam("id") long id, @Valid Card newCard){
        Card entity = Card.findById(id);
        if (entity == null) return Response.status(404).build();
        entity.nenAbility = newCard.nenAbility;
        entity.nenType = newCard.nenType;
        entity.exam = newCard.exam;
        if (newCard.hunter != null && Hunter.findById(newCard.hunter.id) != null) {
            entity.hunter = newCard.hunter;
        }
        return Response.status(200).entity(rep(entity)).build();
    }
}