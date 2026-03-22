> [!IMPORTANT]  
> This app is currently **still in development**.

---

## Prerequisites

To run this application locally, you will need to set up your environment and database connection. This includes:
* IntelliJ Idea (I'm using version 2025.3.3)
* sqldeveloper (for creating your database)

### 1. Database Setup
The app uses an **Oracle Database**. To set up the schema, use the following files:
* `db.sql`: The database skeleton (tables and schema).
* `inserts.sql`: An example file for initial data inserts.
  
*Note: Will be added soon.*
### 2. Configuration
You will need a `resources` folder in the root directory. Inside, create a `config` file containing your Oracle Database authentication details.

*Note: A template will be added soon.*

---

## Features

### Currently Implemented
* **View Collection:** Browse all perfumes currently in your database.
* **Note Search:** Find perfumes based on a specific scent note.
* **Layering Recommendations:** A complex algorithm that suggests perfume combinations for an harmonious scent profile.

### In Development
* **Add Perfumes:** Easily add perfumes without complex SQL inserts.
* **Disable Perfumes:** Ability to "delete" perfumes from your database.

> Developed 03/2026->present
