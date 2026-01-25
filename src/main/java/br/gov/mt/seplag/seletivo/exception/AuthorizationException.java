package br.gov.mt.seplag.seletivo.exception;

public class AuthorizationException extends LayerException {

    public AuthorizationException(String message, LayerDefinition layerDefinition) {
        super(message, layerDefinition);
    }
}
