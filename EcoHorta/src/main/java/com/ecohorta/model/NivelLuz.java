package com.ecohorta.model;

public enum NivelLuz {
    MINIMA("Pouca luz", "Tolera luz indireta ou até cerca de 3 horas de sol por dia", 2),
    MEDIA("Luz média (meio dia de sol)", "Cerca de 4 a 5 horas de sol por dia, de preferência o da manhã", 4),
    PLENA("Sol pleno (o dia todo)", "6 horas ou mais de sol direto por dia", 6);

    private final String rotulo;
    private final String descricao;
    private final int horasMinimas;

    NivelLuz(String rotulo, String descricao, int horasMinimas) {
        this.rotulo = rotulo;
        this.descricao = descricao;
        this.horasMinimas = horasMinimas;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getHorasMinimas() {
        return horasMinimas;
    }
}
