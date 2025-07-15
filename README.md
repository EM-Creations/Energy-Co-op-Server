[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![CC BY-NC-SA 4.0 License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/EM-Creations/Energy-Co-op-Server">
    <img src="https://www.windcoop.co.uk/wp-content/uploads/go-x/u/14699fc7-4639-4665-9c87-1dbd1f1ef1af/image-160x160.png" alt="Logo" width="160" height="160">
  </a>

<h3 align="center">Energy Co-op Server</h3>

  <p align="center">
    Spring Boot Energy Cooperative management server.
    <br />
    <a href="https://github.com/EM-Creations/Energy-Co-op-Server"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/EM-Creations/Energy-Co-op-Server">View Demo</a>
    ·
    <a href="https://github.com/EM-Creations/Energy-Co-op-Server/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    ·
    <a href="https://github.com/EM-Creations/Energy-Co-op-Server/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[![Energy Co-op Server Screen Shot](https://github.com/EM-Creations/Energy-Co-op-Server/blob/main/public/demo.jpg)](https://github.com/EM-Creations/Energy-Co-op-Server)

Energy Co-op Server is intended to be a Spring Boot server backend to facilitate the management of users accessing Energy Co-op information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![Java][java]][java-url]
* [![Spring Boot][springboot]][springboot-url]
* [![Auth0][auth0]][auth0-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

* Java JDK 21+
* Vensys API keys
* Auth0 account
* [Podman (optional)](https://podman-desktop.io/)

### Installation and development

1. [Install Java 21+](https://www.azul.com/downloads/?package=jdk#zulu)
2. Clone a fork of this repository
3. Update the values in the `src/main/resources/application.yml` file as needed (auth0, api details etc).
4. Build and run all tests
```bash
gradle clean build
```
5. Create a Run Configuration to run the `gradle bootRun` task.
6. Check the server is running by going to the [Swagger UI](http://localhost:8080/swagger-ui/index.html) or the [H2 database console](http://localhost:8080/h2-console/).

## Development server

To start a local development server, run:

```bash
gradle bootRun
```

Once the server is running, open your browser and navigate to `http://localhost:8080/`.

## Building

To build the project run:

```bash
gradle clean build
```

## Running unit tests

To execute unit tests with [JUnit 5](https://junit.org/), use the following command:

```bash
gradle clean build
```

## Building a Podman image
1. First build the project using Gradle:
```bash
gradle clean build
```
2. Download and install [Podman Desktop](https://podman-desktop.io/)
3. Open Podman Desktop
4. Go to Containers > Create
5. Choose "Containerfile or Dockerfile"
6. Select the `Containerfile` in the root of this repository as the Containerfile path
7. Give the image a name, e.g. `energy-coop-server` and build it
8. When running the image, set the port mapping to `8080:8080` on the "Basic" tab
9. Click "Start Container" to run the server

<!-- USAGE EXAMPLES -->
## Usage
For more examples, please refer to the [Documentation](https://github.com/EM-Creations/Energy-Co-op-Server)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Feel free to check out the repository, use it and modify your own use so long as it's not for commercial purposes.

1. Fork the Project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to your own fork's branch (`git push origin feature/AmazingFeature`)
5. Pull requests against this repository won't be accepted

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->
## Acknowledgments
* [Img Shields](https://shields.io)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/EM-Creations/Energy-Co-op-Server.svg?style=for-the-badge
[contributors-url]: https://github.com/EM-Creations/Energy-Co-op-Server/graphs/contributors
[stars-shield]: https://img.shields.io/github/stars/EM-Creations/Energy-Co-op-Server.svg?style=for-the-badge
[stars-url]: https://github.com/EM-Creations/Energy-Co-op-Server/stargazers
[issues-shield]: https://img.shields.io/github/issues/EM-Creations/Energy-Co-op-Server.svg?style=for-the-badge
[issues-url]: https://github.com/EM-Creations/Energy-Co-op-Server/issues
[license-shield]: https://img.shields.io/badge/Creative%20Commons-000000?style=for-the-badge&logo=creativecommons&logoColor=white
[license-url]: https://github.com/EM-Creations/Energy-Co-op-Server/blob/main/LICENSE
[product-screenshot]: images/screenshot.png
[java]: https://img.shields.io/badge/Java-DD0031?style=for-the-badge&logo=java&logoColor=white
[java-url]: https://java.com/
[auth0]: https://img.shields.io/badge/Auth0-black?style=for-the-badge&logo=auth0&logoColor=white
[auth0-url]: https://auth0.com/
[springboot]: https://img.shields.io/badge/Spring%20Boot-green?style=for-the-badge&logo=spring&logoColor=white
[springboot-url]: https://spring.io/projects/spring-boot