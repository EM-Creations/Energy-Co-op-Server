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
* PostgreSQL 17+ database
* Vensys API keys
* Auth0 account
* [UI Project setup](https://github.com/EM-Creations/Energy-Co-op-UI)
* [Podman (optional)](https://podman-desktop.io/)

### Auth0 setup
Authentication is managed from the UI side, the server side checks permissions and that the token from the UI is valid.
1. Create an Auth0 account at [Auth0](https://auth0.com/)
2. Create a new application in the Auth0 dashboard
3. Set the callback URL to `http://localhost:4200`
4. Set the logout URL to `http://localhost:4200`
5. Copy the domain, client id and secret from the Auth0 application settings
6. Update the `src/main/resources/application.yml` file with the Auth0 details, issuer is the domain, client id and
secret are the client id and secret from the Auth0 application settings
7. Create a new API in the Auth0 dashboard and enable RBAC, then set the identifier to a URL (it does not need to be accessible)
8. Update the `src/main/resources/application.yml` file with the Auth0 API identifier as the audience
9. Lastly, create a new Action Trigger for "post-login" in the Auth0 dashboard, this will be used to add the user's email to the token.
   - Use the following code in the Action Trigger:
```javascript
exports.onExecutePostLogin = async (event, api) => {
  const namespace = 'ownerships';
  const { ownerships } = event.user.app_metadata;

  if (event.authorization) {
    // Add site ownership details to the ID token
    api.idToken.setCustomClaim(`${namespace}`, ownerships);

    // Add site ownership details to the access token (may not be required)
    api.accessToken.setCustomClaim(`${namespace}`, ownerships);
  }
};
```

### Installation and development

1. [Install Java 21+](https://www.azul.com/downloads/?package=jdk#zulu)
2. Clone a fork of this repository
3. Update the values in the `src/main/resources/application.yml` file as needed (okta oauth2 auth0, api details etc).
4. Build and run all tests
```bash
gradle clean build
```
5. Create a Run Configuration to run the `gradle bootRun` task
6. Check the server is running by going to the [Swagger UI](http://localhost:8080/swagger-ui/index.html) or the [H2 database console](http://localhost:8080/h2-console/)

### Testing the API
1. The API can be tested by first running the UI, logging in and getting the access token
(you can find this by opening the network tab and finding the response to the Auth0 login)
2. Then use a tool like [Postman](https://www.postman.com/), set the authorisation to "Bearer Token" and paste the access token into the field
3. Then configure the Postman request to point to the API endpoints you want to test

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