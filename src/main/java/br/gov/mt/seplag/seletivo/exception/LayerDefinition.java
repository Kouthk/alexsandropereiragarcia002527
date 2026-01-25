package br.gov.mt.seplag.seletivo.exception;


import br.gov.mt.seplag.seletivo.exception.enums.LayerEnum;

public interface LayerDefinition {

    String getClassName();
    LayerEnum getLayer();
}
