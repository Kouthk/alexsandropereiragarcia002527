package br.gov.mt.seplag.seletivo.exception;


public class StorageException extends LayerException {

    public StorageException(String message, LayerDefinition layerDefinition) {
        super(message, layerDefinition);
    }
}
