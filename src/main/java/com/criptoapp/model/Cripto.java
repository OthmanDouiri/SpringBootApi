package com.criptoapp.model;

import lombok.Data;

@Data
public class Cripto {
    private String symbol;
    private Double priceChange;
    private Double priceChangePercent;
    private Double lastPrice;
    private Double volume;
}
