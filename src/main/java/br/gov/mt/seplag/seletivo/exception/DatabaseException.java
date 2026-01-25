package br.gov.mt.seplag.seletivo.exception;


public class DatabaseException extends LayerException {

    public DatabaseException(String message, LayerDefinition layerDefinition) {
        super(message, layerDefinition);
    }
}
