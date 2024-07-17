package com.alurachallenge.literalura.principal;

import com.alurachallenge.literalura.model.*;
import com.alurachallenge.literalura.repository.AutorRepository;
import com.alurachallenge.literalura.repository.LibroRepository;
import com.alurachallenge.literalura.service.ConsumoAPI;
import com.alurachallenge.literalura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos convierteDatosJson = new ConvierteDatos();
    private List<Libro> libros;
    private List<Autor> autores;
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    //Menu de opciones
    public void muestramenu() {
        System.out.println("""
                \n------------------------------------------------
                *           BIENVENIDO A LITERALURA            *
                ------------------------------------------------
                """);

        int opcion = -1;
        String menu = """
                *-*-*-*-*-*-*-* Menú de Búsqueda *-*-*-*-*-*-*-*
             
                1)- Buscar libro por título 
                2)- Listar libros registrados
                3)- Listar autores registrados
                4)- Listar autores vivos en un determinado año
                5)- Listar libros por idioma
                6)- Mostrar estadísticas
                7)- Top 10 libros más descargados
                8)- Buscar autor por nombre
                          
                0) Salir
                          
                Elija una opción
                *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*
                """;

        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número del 0 al 8.");
                teclado.nextLine();
                continue;
            }
            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresPorYear();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    mostrarEstadisticas();
                    break;
                case 7:
                    top10LibrosMasDescargados();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;

                case 0:
                    System.out.println("Saliendo del buscador...");
                    break;
                default:
                    System.out.printf("Opción inválida. Intente nuevamente.");
            }
            System.out.println();
        }
    }

    //Consulta 5
    private void listarLibrosPorIdioma() {
        String menuIdioma = """
                *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*
                Ingrese las siglas del idioma: 
                \nes >> Español
                en >> Inglés
                fr >> Francés 
                pt >> Portugués
                *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*
                """;
        System.out.println(menuIdioma);
        String idiomaBuscado = teclado.nextLine();
        CategoriaIdioma idioma = null;
        switch (idiomaBuscado) {
            case "es":
                idioma = CategoriaIdioma.fromEspanol("Español");
                break;
            case "en":
                idioma = CategoriaIdioma.fromEspanol("Inglés");
                break;
            case "fr":
                idioma = CategoriaIdioma.fromEspanol("Francés");
                break;
            case "pt":
                idioma = CategoriaIdioma.fromEspanol("Portugués");
                break;
            default:
                System.out.println("Entrada inválida.");
                return;
        }
        buscarPorIdioma(idioma);
    }

    private void buscarPorIdioma(CategoriaIdioma idioma) {
        libros = libroRepository.findLibrosByidioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados");
        } else {
            libros.stream().forEach(System.out::println);
        }
    }

    //Consulta 4
    private void listarAutoresPorYear() {
        System.out.println("Ingrese el año que desea buscar: ");
        try {
            Integer year = teclado.nextInt();
            autores = autorRepository.findAutoresByYear(year);
            if (autores.isEmpty()) {
                System.out.println("No hay autores que hayan vivido en ese lapso de tiempo.");
            } else {
                autores.stream().forEach(System.out::println);
            }
        } catch (InputMismatchException e) {
            System.out.println("Ingrese un año correcto");
            teclado.nextLine();
        }
    }

    //Consulta 3
    private void listarAutoresRegistrados() {
        autores = autorRepository.findAll();
        autores.stream().forEach(System.out::println);
    }

    //Consulta 2
    private void listarLibrosRegistrados() {
        libros = libroRepository.findAll();
        libros.stream().forEach(System.out::println);
    }

    //Consulta 1
    private void buscarLibroPorTitulo() {
        System.out.println("Escriba el nombre del libro que desea buscar: ");
        var nombreLibro = teclado.nextLine();
        String url = URL_BASE + "?search=" + nombreLibro.replace(" ", "%20");
        String respuesta = consumoAPI.obtenerDatosApi(url);
        DatosApi datosConsultaAPI = convierteDatosJson.obtenerDatos(respuesta, DatosApi.class);
        if (datosConsultaAPI.numeroLibros() != 0) {
            DatosLibro primerLibro = datosConsultaAPI.resultado().get(0);
            Autor autorLibro = new Autor(primerLibro.autores().get(0));
            Optional<Libro> libroBase = libroRepository.findLibroBytitulo(primerLibro.titulo());
            if (libroBase.isPresent()) {
                System.out.println("""
                        Ese libro ya fue buscado y registrado.
                        No es posible registrar el mismo líbro.
                        Intente con otro título.
                        """);
            } else {
                Optional<Autor> autorDeBase = autorRepository.findBynombre(autorLibro.getNombre());
                if (autorDeBase.isPresent()) {
                    autorLibro = autorDeBase.get();
                } else {
                    autorRepository.save(autorLibro);
                }
                Libro libro = new Libro(primerLibro);
                libro.setAutor(autorLibro);
                libroRepository.save(libro);
                System.out.println(libro);
            }
        } else {
            System.out.println("Líbro no encontrado.");
        }
    }

    //EXTRAS
    //Consulta 8
    private void buscarAutorPorNombre(){
        System.out.println("Escriba el nombre del autor que desea buscar: ");
        var nombreAutor = teclado.nextLine();
        String url = URL_BASE + "?search=" + nombreAutor.replace(" ", "%20");
        String respuestaAutor = consumoAPI.obtenerDatosApi(url);
        DatosApi datosConsultaAPI = convierteDatosJson.obtenerDatos(respuestaAutor, DatosApi.class);

        List<DatosAutor> autoresEncontrados = datosConsultaAPI.resultado().stream()
                .flatMap(libro -> libro.autores().stream())
                .filter(autor -> autor.nombre().toUpperCase().contains(nombreAutor.toUpperCase()))
                .distinct()
                .collect(Collectors.toList());
        if (!autoresEncontrados.isEmpty()) {
            System.out.println("Estos autores coinciden con el nombre " + nombreAutor + ": ");
            autoresEncontrados.forEach(autor -> {
                System.out.println("Autor: " + autor.nombre());

                List<String> librosDelAutor = datosConsultaAPI.resultado().stream()
                        .filter(libro -> libro.autores().stream().anyMatch(a -> a.nombre().equals(autor.nombre())))
                        .map(DatosLibro::titulo)
                        .collect(Collectors.toList());

                if (!librosDelAutor.isEmpty()) {
                    System.out.println("Libros:");
                    librosDelAutor.forEach(libro -> System.out.println("- " + libro));
                } else {
                    System.out.println("No se encontraron libros para este autor.");
                }
            });
        } else {
            System.out.println("No se encontraron autores con el nombre: " + nombreAutor);
        }
    }

    //Consulta 7
    private void top10LibrosMasDescargados() {
        String url = URL_BASE + "?sort=downloads";
        String respuesta = consumoAPI.obtenerDatosApi(url);
        DatosApi datosConsultaAPI = convierteDatosJson.obtenerDatos(respuesta, DatosApi.class);
        List<DatosLibro> top10Libros = datosConsultaAPI.resultado().stream()
                .sorted(Comparator.comparingInt(DatosLibro::descargas).reversed())
                .limit(10)
                .collect(Collectors.toList());
        System.out.println("Top 10 libros más descargados:");
        top10Libros.forEach(libro -> System.out.println(libro.titulo() + " - Descargas: " + libro.descargas()));
    }

    //Consulta 6
    private void mostrarEstadisticas() {
        String respuesta = consumoAPI.obtenerDatosApi(URL_BASE);
        DatosApi datosConsultaAPI = convierteDatosJson.obtenerDatos(respuesta, DatosApi.class);
        DoubleSummaryStatistics est = datosConsultaAPI.resultado().stream()
                .filter(libro -> libro.descargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibro::descargas));
        System.out.println("Cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad mínima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadísticas: " + est.getCount());
    }
}