package br.gov.mt.seplag.seletivo.exception;


import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;

public abstract class LayerException extends RuntimeException {

    private final LayerEnum layer;
    private final String className;

    protected LayerException(String message, LayerDefinition layer) {
        super(message);
        this.layer = layer.getLayer();
        this.className = layer.getClassName();
    }

    public LayerEnum getLayer() {
        return layer;
    }

    public String getClassName() {
        return className;
    }
}

