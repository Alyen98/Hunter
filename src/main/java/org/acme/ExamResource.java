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

import java.util.List;
import java.util.stream.Collectors;

@Path("/exams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExamResource {
    @Context
    UriInfo uriInfo;

    private ExamRepresentation rep(Exam e) {
        return ExamRepresentation.from(e, uriInfo);
    }

    private List<ExamRepresentation> repList(List<Exam> exams) {
        return exams.stream().map(this::rep).collect(Collectors.toList());
    }

    @GET
    @Operation(summary = "Lista todos os Exames com paginação")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ExamRepresentation.class)))
    public Response list(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("size") @DefaultValue("10") int size) {
        PanacheQuery<Exam> query = Exam.findAll(Sort.by("examYear").descending());
        List<Exam> exams = query.page(Page.of(page - 1, size)).list();
        return Response.ok(repList(exams)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca um Exame por ID")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ExamRepresentation.class)))
    @APIResponse(responseCode = "404", description = "Exame não encontrado")
    public Response getById(@PathParam("id") long id) {
        Exam exam = Exam.findById(id);
        return exam != null ? Response.ok(rep(exam)).build() : Response.status(404).build();
    }

    @POST
    @Path("/{examId}/hunters/{hunterId}")
    @Transactional
    @Operation(summary = "Adiciona um Hunter a um Exame")
    @APIResponse(responseCode = "200", description = "Hunter adicionado ao exame")
    @APIResponse(responseCode = "404", description = "Exame ou Hunter não encontrado")
    public Response addHunterToExam(@PathParam("examId") Long examId, @PathParam("hunterId") Long hunterId) {
        Exam exam = Exam.findById(examId);
        Hunter hunter = Hunter.findById(hunterId);
        if (exam == null || hunter == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        hunter.exams.add(exam);
        hunter.persist();
        return Response.ok(rep(exam)).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Cria um novo Exame")
    @APIResponse(responseCode = "201", description = "Exame criado")
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public Response create(@Valid Exam exam) {
        exam.persist();
        return Response.status(201).entity(rep(exam)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualiza um Exame existente")
    @APIResponse(responseCode = "200", description = "Exame atualizado com sucesso", content = @Content(schema = @Schema(implementation = ExamRepresentation.class)))
    @APIResponse(responseCode = "404", description = "Exame não encontrado")
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public Response update(@PathParam("id") long id, @Valid Exam newExamData) {
        Exam exam = Exam.findById(id);
        if (exam == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        exam.name = newExamData.name;
        exam.examYear = newExamData.examYear;
        return Response.ok(rep(exam)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Deleta um Exame por ID")
    @APIResponse(responseCode = "204", description = "Exame deletado com sucesso")
    @APIResponse(responseCode = "404", description = "Exame não encontrado")
    public Response delete(@PathParam("id") long id) {
        boolean deleted = Exam.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}