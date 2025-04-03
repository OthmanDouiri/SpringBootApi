# SpringBootApi
Consumir contenido de una API REST (Binance API) y crear nuestro propio contenido web utilizando el framework Spring.

Vamos a seguir paso a paso con la creación del proyecto en Spring Boot para consumir la API de Binance.

## **Paso 1: Crear la Estructura del Proyecto**  
Primero, vamos a configurar las clases principales del proyecto. Aquí cómo hacerlo:

### **1. Crear el Proyecto Spring Boot**
- Ve a [Spring Initializr](https://start.spring.io/).
- Configura el proyecto con:
  - **Project**: Maven Project.
  - **Language**: Java.
  - **Spring Boot Version**: La más reciente.
  - **Group**: `com.criptoapp`.
  - **Artifact**: `criptoapp`.
  - **Name**: `criptoapp`.
  - **Packaging**: Jar.
  - **Java Version**: 17 o superior.
- En las dependencias, agrega:
  - **Spring Web**: Para hacer las peticiones HTTP.
  - **Thymeleaf**: Para renderizar vistas dinámicas.
  - **Lombok**: Para reducir el código repetitivo (opcional pero útil).
  - **Spring Boot DevTools** (opcional, para facilitar la depuración).

Descarga el proyecto generado y ábrelo en IDE favorito (IntelliJ IDEA, Eclipse, VS Code).

### **2. Estructura del Proyecto**
A continuación, crearemos las clases necesarias para organizar el código.
``` css

src
 ├── main
 │   ├── java
 │   │   ├── com
 │   │   │   ├── criptoapp
 │   │   │   │   ├── CriptoApplication.java  ✅  // Clase principal
 │   │   │   │   ├── controller
 │   │   │   │   │   ├── CriptoController.java
 │   │   │   │   ├── model
 │   │   │   │   │   ├── Cripto.java
 │   │   │   │   ├── service
 │   │   │   │   │   ├── CriptoService.java
 │   │   │   │   ├── repository
 │   │   │   │   │   ├── CriptoRepository.java
 │   ├── resources
 │   │   ├── templates
 │   │   │   ├── index.html
 │   │   │   ├── error.html
 │   │   ├── application.properties

```

#### **2.1 Crear el Modelo (`Cripto.java`)**
Esta clase representará los datos que obtenemos de la API de Binance.  

```java
package com.criptoapp.model;

import lombok.Data;

@Data
public class Cripto {
    private String symbol;
    private String name;
    private Double price;
    private Double change24h;
    private Double volume24h;
}
```

- **@Data** es una anotación de Lombok que genera automáticamente los métodos `getter`, `setter`, `toString`, `equals`, y `hashCode`.

#### **2.2 Crear el Servicio (`CriptoService.java`)**
Esta clase será responsable de obtener la información de la API de Binance.

```java
package com.criptoapp.service;

import com.criptoapp.model.Cripto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class CriptoService {

    //Definición de la API de Binance
    private final String apiUrl = "https://api.binance.com/api/v3/ticker/24hr"; // URL de la API de Binance.

    //Inyección de RestTemplate
    private final RestTemplate restTemplate;

    public CriptoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //Método para obtener las criptomonedas
    public List<Cripto> getCriptos() {
        // Se hace la solicitud GET a la API de Binance.
        Cripto[] criptos = restTemplate.getForObject(apiUrl, Cripto[].class);
        return List.of(criptos);
    }
}
```

- **@Service** es una anotación indica que esta clase es un servicio de Spring y puede ser inyectada en otras partes de la aplicación.
- **apiUrl** Esta es la URL de Binance que proporciona información sobre el estado de las criptomonedas en las últimas 24 horas.
- **RestTemplate** se usa para realizar peticiones HTTP a la API de Binance.
- Se recibe RestTemplate como parámetro en el constructor, lo que permite inyectarlo desde la configuración de Spring.
- Se usa **restTemplate.getForObject(apiUrl, Cripto[].class);** para hacer una petición GET a Binance y obtener los datos en forma de un arreglo de Cripto. Luego, List.of(criptos); convierte el arreglo en una lista y lo devuelve.




#### **2.3 Crear el Controlador (`CriptoController.java`)**
El controlador será responsable de gestionar las solicitudes HTTP y devolver los datos a la vista (usando Thymeleaf).

```java
package com.criptoapp.controller;

import com.criptoapp.model.Cripto;
import com.criptoapp.service.CriptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CriptoController {

    //declara el servicio como un atributo inmutable.
    private final CriptoService criptoService;

    //Inyección del Servicio
    @Autowired
    public CriptoController(CriptoService criptoService) {
        this.criptoService = criptoService;
    }

    //Método para Manejar Solicitudes a la Página Principal
    @GetMapping("/")
    public String getCriptos(Model model) {
        model.addAttribute("criptos", criptoService.getCriptos());
        return "index";  // Nombre de la plantilla Thymeleaf.
    }
}
```

- **@Controller** indica que esta clase es un controlador en el patrón MVC.
- CriptoService se inyecta en el constructor mediante **@Autowired**. Esto permite usar el servicio para obtener datos sin necesidad de instanciarlo manualmente.
- **private final CriptoService criptoService;** declara el servicio como un atributo inmutable.
- **@GetMapping("/")**: Indica que este método maneja las solicitudes HTTP GET en la raíz (/).
- **Model model** : Se usa para enviar datos a la vista.
  
- model.addAttribute("criptos", criptoService.getCriptos());
    - Llama a **criptoService.getCriptos()** para obtener la lista de criptomonedas.
    - Guarda la lista en el modelo con la clave "criptos", para que pueda ser utilizada en la vista.

- return "index";
   - Devuelve el nombre de la plantilla **Thymeleaf** (un motor de plantillas para generar HTML dinámico en Spring Boot).
   - Spring buscará un archivo index.html en src/main/resources/templates/


#### **2.4 Crear la Vista (`index.html`)**
La vista será donde mostraremos los datos de las criptomonedas.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Criptomonedas</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.18.3/bootstrap-table.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<style>
    body {
        background-image: url('https://pixelplex.io/next/images/cryptocurrency-exchange-development/cryptocurrency-exchange-development-bg-1600.jpg');
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
        color: white;
        font-family: 'Arial', sans-serif;
        text-align: center;
        padding: 20px;
    }

    td, th {
        color: white;
        font-size: 1.2em;
    }
    .text-warning{
    
        font-weight: bold;
    }
    
   
</style>
<body>
    <div class="container">
        <h1 class="mt-5 text-center">Precios de Criptomonedas</h1>
        <div class="table-responsive mt-3">
            <table class="table table-bordered table-striped"
                   data-toggle="table" 
                   data-pagination="true" 
                   data-page-size="10"
                   data-search="true"
                   data-show-refresh="true"
                   data-show-toggle="true"
                   data-show-columns="true"
                  >
                <caption>Lista de precios de criptomonedas</caption>
                <thead class="thead-dark">
                    <tr>
                        <th>Simbolo</th>
                        <th>Cambio Precio</th>
                        <th>% Cambio</th>
                        <th>Último Precio</th>
                        <th>Volumen</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="cripto : ${criptos}" th:if="${criptos != null}">
                        <td class="text-warning" th:text="${cripto.symbol}"></td>
                        <td th:text="${cripto.priceChange}"></td>
                        <td th:text="${cripto.priceChangePercent}"></td>
                        <td th:text="${cripto.lastPrice}"></td>
                        <td th:text="${cripto.volume}"></td>
                    </tr>
                    <tr th:if="${criptos == null or #lists.isEmpty(criptos)}">
                        <td colspan="5" class="text-center">No hay datos disponibles</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.18.3/bootstrap-table.min.js"></script>
</body>
</html>

```

#### **2.5  Crear la Vista (`error.html`)** y **ErrorController** 

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container text-center mt-5">
    <h1>Oops! Algo salió mal.</h1>
    <p>Lo sentimos, ha ocurrido un error inesperado.</p>
    <a href="/" class="btn btn-primary mt-3">Volver al inicio</a>
</div>
</body>
</html>
```
#### Configuración ErrorController
en src\main\java\com\errors\criptoapp\controller\ 
creamor el controllador **ErrorController.java**


```java
package com.errors.criptoapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Return the error.html template
        return "error";
    }
}

```


#### **2.6 Configuración adicional**
No olvides configurar el `RestTemplate` que utilizamos en el servicio. Para ello, crea una clase de configuración:

##### ¿Por qué se usa esta configuración?
En lugar de hacer esto en cada clase:

```java
package com.criptoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```
- **@Configuration** indica que esta clase contiene configuración de Spring.
- **@Bean** le dice a Spring que este método devuelve un objeto que debe ser administrado como un bean.

- **public RestTemplate restTemplate()** define un método que devuelve una nueva instancia de RestTemplate.
- **return new RestTemplate();** crea y devuelve un objeto **RestTemplat**e, que será reutilizado en la aplicación.
  
---

## **Paso 2: Probar el Proyecto**
Con todas las clases configuradas, ahora puedes ejecutar tu aplicación. Cuando accedas a la URL `http://localhost:8080`, deberías ver la tabla con la información de las criptomonedas.

---

## **Paso 3: Personalización y Estilo**
- Como ya estamos utilizando **Bootstrap**, puedes modificar los estilos en el archivo `index.html` para mejorar la presentación.
- Si quieres agregar más detalles o funcionalidades, puedes extender el modelo `Cripto` o personalizar las consultas a la API de Binance.

---

