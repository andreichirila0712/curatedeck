# Curate Deck

Curate Deck is a server-side rendered (SSR) web application designed for people to showcase their projects. I built this
MVP mainly to gain experience with Spring Boot, secure authentication flows, and real-world deployment challenges like
containerization and cloud storage.

As of now is in version 1.0.0, the focus was to establish a backend foundation and learn how to deploy an application to
a live server.

## What it does

**Project showcase**
* Users can showcase their work by adding a Title, Role, Challenges, Solutions, Results, and a Tech Stack.
* Supports media uploads for thumbnails, architecture diagrams, and demos.
* Projects can be toggled between public (anyone can view them, no account needed) and private.

**Identity && Account management**
* Integrated **Keycloak** to learn and handle authentication flows.
* Users can request password changes, update their emails, or delete their account safely through Keycloak's email-based
flows; sensitive credentials never touch the application database.

**User interface**
* Minimalist, server-side rendered UI with a built-in light/dark theme toggler.
* Mocked language and date-format settings for future localization.

## Architectural diagram

[![](https://app.eraser.io/workspace/oblAVwMBAPiwE00r6vhj/preview?diagram=ZoQ5bWrr0WOgA_cH6Pmu&type=embed)](https://app.eraser.io/workspace/oblAVwMBAPiwE00r6vhj?diagram=ZoQ5bWrr0WOgA_cH6Pmu)

## Tech stack

**Backend**
* **Java & Spring Boot:** Core application logic and API/
* **Spring Security:** Resource protection and OAuth2/Keycloak integration.
* **PostgreSQL:** Relational database for application data.
* **Flyway:** Database migrations to track schema changes.

**Frontend**
* **Thymeleaf:** Server-side HTML templating.
* **HTMX & _hyperscript:** Added to handle dynamic UI interactions without relying on a heavy JS framework.
* **PicoCSS:**: Minimal usage, for the landing page.
* **Vaadin Web Components:** For UI elements.

**Testing**
* **JUnit 5 & Mockito:** Used for writing unit and integration tests.

## Deployment & infrastructure

I deployed Curate Deck using the following setup:
* **Server:** Hosted on a Hetzner VPS (IPv6-only environment).
* **Reverse Proxy & SSL:** Configured **Caddy** to handle internal IPv6 routing and automatic SSL certificates.
* **Edge Routing:** Placed behind **Cloudflare** for DNS and proxying.
* **Object Storage:** Media uploads are saved to a self-hosted **Garage** (S3-compatible) instance on an AWS EC2 server.
* **Containers:** The application, database, and Keycloak are fully containerized using **Docker** for easier deployment
and environment consistency.

## Roadmap

This MVP is just the first iteration. What I plan next is:
- [] **Frontend Migration:** Rebuilding the frontend as an SPA using **Angular** to have a better understanding of
frontend frameworks.
- [] **Expand media support:** Adding constraints and support for MP4 video and GIF uploads in the demo section.
- [] **Proper Localization:** Implementing the backend logic to make the current mock language and date settings
functional.