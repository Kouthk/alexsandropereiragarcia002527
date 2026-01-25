package br.gov.mt.seplag.seletivo.exception;

public class ResourceNotFoundException extends LayerException {

    public ResourceNotFoundException(String message, LayerDefinition layerDefinition) {
        super(message, layerDefinition);
    }
}
