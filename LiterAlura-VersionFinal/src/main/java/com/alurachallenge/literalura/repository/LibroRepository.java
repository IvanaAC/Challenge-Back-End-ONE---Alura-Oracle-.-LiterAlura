package com.alurachallenge.literalura.repository;

import com.alurachallenge.literalura.model.CategoriaIdioma;
import com.alurachallenge.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findLibroBytitulo(String titulo);
    List<Libro> findLibrosByidioma(CategoriaIdioma idioma);
}