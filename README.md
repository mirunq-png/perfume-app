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
  
## Prerequisites

To run this application locally, you will need to set up your environment and database connection. This includes:
* IntelliJ Idea (I'm using version 2025.3.3)
* sqldeveloper (for creating your Oracle database)

### 1. Database Setup
To set up the schema, use the following files:
* `db.sql`: The database skeleton (tables and schema).
* `inserts.sql`: An example file for initial data inserts.
  
*Note: A template will be added soon.*

### 2. Configuration
You will need a `resources` folder in the root directory. Inside, create a `config` file containing your Oracle Database authentication details.

<img width="190" height="93" alt="image" src="https://github.com/user-attachments/assets/46130851-619f-4790-b20e-d8246091ad19" />

*Note: A template will be added soon.*

## Features
* View Collection: Browse all perfumes currently in your database.
* Note Search: Find perfumes based on a specific scent note.
* Layering Recommendations: A complex algorithm that suggests perfume combinations for an harmonious scent profile.
* Add Perfumes: Easily add perfumes without complex SQL inserts.
* Disable Perfumes: Ability to soft delete perfumes from your database.
---

</details>

<details>
  <summary>🌸 <b>Phase 1 branch</b></summary>
  
  ## Self-explanatory.
  
  <img width="833" height="824" alt="image" src="https://github.com/user-attachments/assets/3dce965f-de62-4ab7-8bb9-6ae2bfe45fe7" />
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
    P2["☁️ Phase 2:<br/>???"]
    
    %% Phase 3
    P3["🍓 Phase 3:<br/>???"]
    
    %% Phase 4
    P4["🎂 Phase 4:<br/>???"]
    
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



