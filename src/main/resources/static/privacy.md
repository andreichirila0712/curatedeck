# Privacy Policy

**Effective Date:** June 12, 2026

This Privacy Policy explains how Andrei Chirilă ("I", "my") collects, processes, and protects your personal data when you create an account and use this portfolio platform ("the Website").

This Website is run by an individual developer based in Romania for non-commercial portfolio tracking. It operates strictly under the principles of **Data Minimization** and **Privacy by Design** in compliance with the EU General Data Protection Regulation (GDPR).

---

## 1. Data Controller & Contact
Since this is a personal project, I act as the Data Controller for your information. If you have any questions regarding your data or wish to exercise your data rights, you can contact me directly at:
*   **Email:** contact@curatedeck.com
*   **Location:** Romania

## 2. What Personal Data is Collected
I only collect the bare minimum information required to manage your account secure access:
*   **Account Registration Data:** Email address, First Name, Last Name, Username, and Password hashes.
*   **Project Content Data:** Titles, roles, thumbnails, demo media, architecture diagrams, live URLs, and repository links that you explicitly choose to upload or link.

## 3. Legal Basis for Processing
I process your data under **Article 6(1)(b) of the GDPR (Performance of a Contract)**. Collecting your email and account details is strictly necessary to provide the service you requested: establishing a user account to track, manage, and optionally showcase your projects.

## 4. Privacy by Default & Public Data Exposure
*   **Private by Default:** All accounts and project data are strictly private upon creation. No user or project data is visible to the internet or other users by default.
*   **No User Disclosures:** If you toggle a project to "Public", only the specific project metadata (title, role, diagrams, etc.) becomes visible to visitors. **Your account personal data (Real Name and Email Address) is never exposed on public pages.**

## 5. Sub-processors (Where Your Data Lives)
I own and manage the database architecture, but the physical data is securely hosted by our infrastructure provider:
## 5. Sub-processors (Where Your Data Lives)
I own, manage, and isolate the database and backend architecture, but the physical servers are provided by third-party infrastructure hosts. To ensure security and performance, user data is split across the following providers:

*   **Authentication & User Database:** Hosted on **Oracle Cloud Infrastructure** (OCI). This server securely processes and stores your Keycloak registration data (email, name, password hash).
    *   *Server Location:* eu-frankfurt-1, Frankfurt
*   **Media & Storage:** Hosted on **Amazon Web Services (AWS) EC2**. This server runs our isolated object storage for user-uploaded media (avatars, thumbnails, architecture diagrams).
    *   *Server Location:* eu-north-1, Stockholm
*   **Application Server:** Hosted on **Hetzner**. This server renders the website pages and handles the application logic.
    *   *Server Location:* eu-central, Falkenstein

## 6. Cookies & Tracking
This website does **not** use marketing cookies, tracking pixels, or third-party analytics (like Google Analytics).
*   We use strictly **necessary session cookies** managed through Keycloak to keep you securely authenticated.
*   Because these cookies are essential to the performance of the service, a cookie consent banner is not required.

## 7. Third-Party Links (Donations)
If you click on the "Buy me a coffee" link, you are redirected to an external third-party platform. I do not collect, process, or store any banking, credit card, or financial payment details on this Website.

## 8. Data Retention & Your Right to Deletion
*   **Retention:** Your data is kept for as long as your account remains active.
*   **Total Deletion:** Under GDPR, you have the right to erasure (the "Right to be Forgotten"). You can delete your account directly through the user interface. Doing so will completely wipe your user data from the Keycloak authentication database and permanently delete all associated project records (both public and private).

---