package com.jbk.resilience.util;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Getter
public enum TipoAmbienteSefaz {

    PRODUCAO("1", "Produção"),
    HOMOLOGACAO("2", "Homologação");

    private static final Logger LOGGER = LoggerFactory.getLogger(TipoAmbienteSefaz.class);

    private final String codigo;
    private final String descricao;

    TipoAmbienteSefaz(final String codigo, final String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static TipoAmbienteSefaz getByCodigo(String codigo) {
        return Arrays.stream(TipoAmbienteSefaz.values())
                .filter(v -> v.getCodigo().equalsIgnoreCase(codigo)).findFirst()
                .orElse(null);
    }

    public static TipoAmbienteSefaz getByCodigo(Integer codigo) {
        return getByCodigo(String.valueOf(codigo));
    }
}
