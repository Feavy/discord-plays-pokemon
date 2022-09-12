package fr.feavy.discordplayspokemon.controller;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class IndexController {
    @GET
    @Produces("*/*")
    public Uni<Response> getScreen() {
        return Uni.createFrom().item(() -> Response.seeOther(URI.create("https://github.com/Feavy/discord-plays-pokemon")).build());
    }
}
