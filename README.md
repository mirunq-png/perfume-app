> [!IMPORTANT]  
> This app is currently still in development.

<table border="0">
  <tr>
    <td><img src="https://github.com/user-attachments/assets/0138b7b0-8bd7-4c36-a1d5-a87dad266ba1" width="300"></td>
    <td><img src="https://github.com/user-attachments/assets/b0d9776e-ce97-45a8-91d5-a58e6951cbec" width="300"></td>
    <td><img src="https://github.com/user-attachments/assets/92085f24-28f3-45b8-983d-a2da08835e84" width="300"></td>
  </tr>
</table>

<details>
  <summary>🧴 <b>Main branch </b></summary>
  <img width="1252" height="394" alt="image" src="https://github.com/user-attachments/assets/738c9f42-6bd4-4c1d-bc62-09fbc8edf126" />

## Prerequisites

To run this application locally, you will need to set up your environment and database connection. This includes:
* IntelliJ Idea (I'm using version 2025.3.3)
* sqldeveloper (for creating your Oracle database)

### 1. Database Setup
To set up the schema, use the following [files](https://gist.github.com/mirunq-png/69a88cca8faa8c2c09a6fe81deb80079):
* `db.sql`: The database skeleton (tables and schema).
* `inserts.sql`: An example file for initial data inserts.
  
### 2. Configuration
You will need a `resources` folder in the root directory. Inside, create a `config` [file](https://gist.github.com/mirunq-png/b139489536fc510969c92f5ee49cbd0b) containing your Oracle Database authentication details.

<img width="190" height="93" alt="image" src="https://github.com/user-attachments/assets/46130851-619f-4790-b20e-d8246091ad19" />

## Features
* View Collection: Browse all perfumes currently in your database.
* Note Search: Find perfumes based on a specific scent note.
* Layering Recommendations: A complex algorithm that suggests perfume combinations for an harmonious scent profile.
* Other CRUD operations
---

</details>

<details>
  <summary>🌸 <b>Phase 1 branch</b></summary>
  
  <img width="820" height="980" alt="image" src="https://github.com/user-attachments/assets/14341bcd-f263-4746-84b8-54ecdc2f99b8" />

  ## What's new
  * Java Servlets
  * HTML, CSS

  ---
  
</details>

<details>
  <summary>☁️ <b>Phase 2 branch</b></summary>
<img width="1399" height="253" alt="image" src="https://github.com/user-attachments/assets/bb4fad56-1fde-4b26-bf3e-45240e28829d" />

  ## What's new
  * Viewing your collection and scent recommandations from UI
  * REST API
  * JavaScript+HTML for UI
  
  ---
  
</details>

<details>
  <summary>☁️ <b>Phase 3 branch</b></summary>
<img width="530" height="940" alt="image" src="https://github.com/user-attachments/assets/9c37b811-0298-4761-91ec-9d8b6e947746" />

  ## Prerequisites

  To run this application locally, you will need to set up your environment and database connection. This includes:
  * IntelliJ Idea (I'm using version 2025.3.3) and JDK21
  * PostgreSQL (I'm using version 18.4.2)
  * Git

  ### 1. Database Setup
  To set up the schema, use the following [files](https://gist.github.com/mirunq-png/38ed932165fc39701b1ed5d1441d0eff):
  * `db.sql`: The database skeleton (tables and schema).
  * `inserts.sql`: An example file for initial data inserts.

  ### 2. Cloning the repo
  Copy the repo URL: `https://github.com/mirunq-png/perfume-app` and clone it in IntelliJ. Make sure you switch to the `phase3` branch.
  
  ### 3. Configuration
  You will need a `resources` folder in `src/main`. Inside, create a `config.properties` [file](https://gist.github.com/mirunq-png/ee19936e8fac68b560fea0333b6b2c9f) containing your PostgreSQL Database authentication details.
  
  You will need to set up the Smart Tomcat plugin. After having downloaded [Apache Tomcat 9.0](https://tomcat.apache.org/download-90.cgi):
  - CTRL+Alt+S -> Plugins -> "smarttomcat" -> Install
  - CTRL+Shift+A -> "Edit configuration" -> Add new run configuration -> Smart Tomcat -> fill in the config fields
  <img width="580" height="429" alt="image" src="https://github.com/user-attachments/assets/fefd0f3b-4b25-469f-b8f4-9fb1e8dd8eec" />
  
  Make sure the deployment directory is the `webapp` folder and that you add a context path.

  ## What's new
  * Adding/modifying/deleting perfumes from UI
  * Migration to PostgreSQL
  ---
  
</details>

<details>
  <summary>🎂 <b>Phase 4 branch</b></summary>

  ## What's new
  N/A
  ---
  
</details>

---

## Roadmap
```mermaid
graph TD
    %% Phase 0
    P0["🧴 Phase 0:<br/>CLI Application"]
    %% Phase 1
    P1["🌸 Phase 1:<br/>Static web server"]
    
    %% Phase 2
    P2["☁️ Phase 2:<br/>Dynamic data & REST API"]
    
    %% Phase 3
    P3["🍓 Phase 3:<br/>HTTP GET/POST/PUT/DELETE"]
    
    %% Phase 4
    P4["🎂 Phase 4:<br/>Spring Boot"]
    
    %% Phase 5
    P5["🍰 Phase 5:<br/>???"]
    
    %% Phase 6
    P6["🎀 Phase 6:<br/>???"]
    
    %% Phase 7
    P7["💖 Phase 7:<br/>???"]

    %% Flow connections
    P0 --> P1
    P1 --> P2
    P2 --> P3
    P3 --> P4
    P4 --> P5
    P5 --> P6
    P6 --> P7

    %% Sweet Pink Styling
    classDef pinkVibe fill:#ffe4e6,stroke:#f43f5e,stroke-width:2px,color:#881337,font-weight:bold,border-radius:10px;
    
    %% Apply styling to all phases
    class P0,P1,P2,P3,P4,P5,P6,P7 pinkVibe;
```
> Developed 03/2026->present



