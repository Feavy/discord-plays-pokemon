package fr.feavy.discordplayspokemon.controller;

import fr.feavy.discordplayspokemon.service.vba.KeyMap;
import fr.feavy.discordplayspokemon.vba.VBAManager;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.Cache;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
public class VBAController {
    private final KeyMap keyMap = new KeyMap();
    private final VBAManager vbaManager;

    @Context
    HttpHeaders httpHeaders;

    public VBAController(VBAManager vbaManager) {
        this.vbaManager = vbaManager;
    }

    @GET
    @Path("{path : view.*}")
    @Produces("image/png")
    @Cache(maxAge = 0, noCache = true, noStore = true)
    public Uni<Response> getScreen(@PathParam("path") String path) {
        return Uni.createFrom().item(() -> {
            String path2 = path.split("/")[0];

            List<String> userAgent = httpHeaders.getRequestHeader("User-Agent");

            if (path2.length() > 4 && userAgent.size() > 0 && userAgent.get(0).contains("Discordbot")) {
                char keyCode = path2.charAt(4);
                try {
                    vbaManager.queueKey(keyMap.getKeyCode(keyCode));
                } catch (IllegalArgumentException ignored) {
                }
            }

            return Response.ok(vbaManager.getImage(), "image/png").build();
        });
    }
}
