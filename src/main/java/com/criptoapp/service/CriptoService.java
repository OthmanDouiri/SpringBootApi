package com.criptoapp.service;

import com.criptoapp.model.Cripto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class CriptoService {

    private final String apiUrl = "https://api.binance.com/api/v3/ticker/24hr"; // URL de la API de Binance.

    private final RestTemplate restTemplate;

    public CriptoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Cripto> getCriptos() {
        // Se hace la solicitud GET a la API de Binance.
        Cripto[] criptos = restTemplate.getForObject(apiUrl, Cripto[].class);
        return List.of(criptos);
    }
}
