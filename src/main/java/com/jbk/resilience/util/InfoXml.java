package com.jbk.resilience.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InfoXml {

    private String versaoXml;
    private String chaveAcesso;
    private TipoAmbienteSefaz tipoAmbienteSefaz;
    private String xml;
}
