# ⚡ FitSo — Gamified Fitness Tracking System

**FitSo** is a college-grade desktop fitness application combining daily workout tracking with an RPG-style gamification engine. Built using pure **Java Swing/AWT**, **JDBC**, and **Oracle Database**.

---

## 🌟 Key Features

1. **Gamification Engine:**
   - XP / Energy system: Earn energy and coins from logged workouts.
   - Dynamic Leveling: Derived formula `level = 1 + totalEnergy / 50` (capped at 10).
   - World Stage Unlocks: Automatically transitions across **Bedroom → Backyard → Park → Adventure Zone** as you level up.
   - Character & Pet Companion: Derived emotional states (HAPPY, IDLE/NORMAL, TIRED/SLEEPY, CELEBRATION).
2. **Item Shop & Wardrobe:**
   - Spend coins to buy avatar items (Sporty Cap, Running Shoes, Pet Bandana, Room Poster, Workout Outfit).
   - Equipping items enforces a 1-item-per-slot rule inside an ACID transaction.
3. **Data-Driven Missions:**
   - Evaluated live against user workouts and body measurements.
   - Reward loops auto-evaluate until no new missions unlock.
4. **Custom Java2D Graphical Reports (Zero Dependencies):**
   - Daily Calories Burned (Bar Chart).
   - Workouts Logged Per Week (Bar Chart).
   - Weight Progression History (Line Chart).
5. **Academic Standard Architecture & BCNF Database:**
   - 10 BCNF-normalized tables with identity columns, constraints, and candidate keys.
   - PreparedStatement and try-with-resources across all DAOs.
   - Salted SHA-256 password hashing.

---

## 📁 Project Structure

```
FitSo/
├── config.properties          # Oracle DB connection properties
├── compile.bat                # One-click compile script (Windows)
├── run.bat                    # One-click run script (Windows)
├── README.md
├── lib/                       # Place ojdbc11.jar (Oracle JDBC) here
├── src/
│   ├── Main.java
│   ├── model/                 # User, Workout, PlayerState, Item, Goal, Mission...
│   ├── dao/                   # UserDao, WorkoutDao, MeasurementDao, ItemDao...
│   ├── service/               # AuthService, GamificationService, WorkoutService...
│   ├── ui/
│   │   ├── LoginFrame.java    # Sign in / Register glassmorphism frame
│   │   ├── MainFrame.java     # CardLayout host + sidebar navigation
│   │   ├── components/        # Theme, EnergyBar, CharacterView, BarChartPanel, LineChartPanel
│   │   └── panels/            # DashboardPanel, LogPanel, ReportsPanel, ShopPanel, ProfilePanel
│   └── util/                  # DBConnection, PasswordUtil, ImageCache, Session, Validator
├── database/
│   ├── 01_create_tables.sql   # DDL script (10 tables)
│   ├── 02_seed_data.sql       # Seed data (Workout types, missions, items)
│   ├── 03_demo_user.sql       # Demo user with 4 weeks of data (alex_fit / Password123!)
│   └── 04_sample_queries.sql  # 6 analytical sample SQL queries
└── docs/
    ├── normalization.md       # 1NF / 2NF / 3NF / BCNF & functional dependencies write-up
    └── er_diagram.md          # Mermaid ER diagram & cardinality breakdown
```

---

## ⚙️ Step 1 — Oracle JDBC Driver Setup

> **IMPORTANT:** You MUST place the Oracle JDBC driver JAR in the `lib\` folder before compiling.

1. Download **ojdbc11.jar** from Oracle:
   - Go to: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
   - Download `ojdbc11.jar` (for Oracle 21c/23c XE) or `ojdbc8.jar` (for Oracle 12c/18c/19c)
2. Create the `lib\` folder in `e:\FitSo\` and place the JAR inside:
   ```
   e:\FitSo\lib\ojdbc11.jar
   ```

---

## ⚙️ Step 2 — Database Setup (Oracle DB)

1. Open SQL Developer, SQL*Plus, or DBeaver connected to your Oracle Database instance.
2. Run the scripts in order:
   ```sql
   @database/01_create_tables.sql
   @database/02_seed_data.sql
   @database/03_demo_user.sql
   ```
3. Update `config.properties` with your local Oracle DB URL and credentials:
   ```properties
   db.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
   db.user=fitso_user
   db.password=fitso123
   ```

---

## 🚀 Step 3 — Compile & Run

### Option A: One-Click Scripts (Windows)

```bat
# In Command Prompt (cmd), navigate to e:\FitSo\
compile.bat     # Compiles all Java source files
run.bat         # Launches the application
```

### Option B: Manual Command

```bat
# Compile:
javac -d bin -cp "lib\*" src\Main.java src\model\*.java src\dao\*.java src\service\*.java src\util\*.java src\ui\components\*.java src\ui\panels\*.java src\ui\*.java

# Run:
java -cp "bin;lib\*" Main
```

---

## 🎮 Demo Login

Once the app is running:
- Click **⚡ DEMO LOGIN** on the login screen.
- You'll log in as `alex_fit` with **4 weeks of pre-populated workout history**, measurements, goals, and shop items!
- **Username:** `alex_fit`  |  **Password:** `Password123!`

---

## 🗂️ Tech Stack

| Layer | Technology |
|---|---|
| UI | Java Swing/AWT + Custom Java2D Graphics |
| Database | Oracle Database (XE 21c recommended) |
| DB Access | JDBC + PreparedStatement |
| Auth | SHA-256 + Salt (PasswordUtil) |
| Architecture | 3-tier: DAO → Service → UI |
| Build | Plain `javac` (no Maven/Gradle) |
