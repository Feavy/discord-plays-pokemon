package fr.feavy.discordplayspokemon.controller;

import fr.feavy.discordplayspokemon.vba.key.Key;
import fr.feavy.discordplayspokemon.service.vba.EmulatorService;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.Cache;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("{path : (?i)view.*}")
public class EmulatorController {
    private final EmulatorService emulatorService;

    @Context
    HttpHeaders httpHeaders;

    public EmulatorController(EmulatorService emulatorService) {
        this.emulatorService = emulatorService;
    }

    @GET
    @Produces("image/png")
    @Cache(maxAge = 0, noCache = true, noStore = true)
    public Uni<Response> getScreen(@PathParam("path") String path) {
        return Uni.createFrom().item(() -> {
            String path2 = path.split("/")[0].toLowerCase();

            List<String> userAgent = httpHeaders.getRequestHeader("User-Agent");

            if (path2.length() > 4 && userAgent.size() > 0 && userAgent.get(0).contains("Discordbot")) {
                char keyCode = path2.charAt(4);
                Key key = Key.ofLabel(keyCode);
                try {
                    emulatorService.queueKey(key);
                } catch (IllegalArgumentException ignored) {
                }
            }

            return Response.ok(emulatorService.getImage(), "image/png").build();
        });
    }
}
