package fr.feavy.discordplayspokemon.controller;


import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.URI;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {
    private final URI redirectionUrl;

    public ExceptionHandler(@ConfigProperty(name = "redirection.url") URI redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

    @Override
    @Produces("*/*")
    public Response toResponse(Exception exception) {
        return Response.seeOther(redirectionUrl).build();
    }
}
