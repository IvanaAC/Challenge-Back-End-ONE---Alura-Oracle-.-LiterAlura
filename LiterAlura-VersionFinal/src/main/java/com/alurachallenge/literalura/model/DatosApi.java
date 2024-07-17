package com.alurachallenge.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosApi(
        @JsonAlias("count") Integer numeroLibros,
        @JsonAlias("results") List<DatosLibro> resultado) {
}