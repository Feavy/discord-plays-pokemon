package fr.feavy.discordplayspokemon.controller;

import fr.feavy.discordplayspokemon.vba.key.Key;
import fr.feavy.discordplayspokemon.service.vba.EmulatorService;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.Cache;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("{path : (?i)view.*}")
public class EmulatorController {
    private final URI redirectionUrl;
    private final EmulatorService emulatorService;

    @Context
    HttpHeaders httpHeaders;

    public EmulatorController(@ConfigProperty(name = "redirection.url") URI redirectionUrl,
                              EmulatorService emulatorService) {
        this.redirectionUrl = redirectionUrl;
        this.emulatorService = emulatorService;
    }

    @GET
    @Produces("image/png")
    @Cache(maxAge = 0, noCache = true, noStore = true)
    public Uni<Response> getScreen(@PathParam("path") String path) {
        return Uni.createFrom().item(() -> {
            String path2 = path.split("/")[0].toLowerCase();

            List<String> accept = httpHeaders.getRequestHeader("Accept");
            boolean isUser = accept != null && accept.size() > 0;

            if(isUser) {
                return Response.seeOther(redirectionUrl).build();
            }

            List<String> userAgent = httpHeaders.getRequestHeader("User-Agent");
            boolean isDiscordbot = userAgent != null && userAgent.size() > 0 && userAgent.get(0).contains("Discordbot");

            if(isDiscordbot) {
                if (path2.length() > 4) {
                    char keyCode = path2.charAt(4);
                    Key key = Key.ofLabel(keyCode);
                    try {
                        emulatorService.queueKey(key);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }

            return Response.ok(emulatorService.getImage(), "image/png").build();
        });
    }
}
