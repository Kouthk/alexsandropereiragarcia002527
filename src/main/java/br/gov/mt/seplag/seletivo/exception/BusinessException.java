package br.gov.mt.seplag.seletivo.exception;

public class BusinessException extends LayerException {

    public BusinessException(String message, LayerDefinition layerDefinition) {
        super(message, layerDefinition);
    }
}
