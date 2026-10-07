# FitSo: Implementation Plan (for Antigravity)

**Project:** FitSo, a gamified fitness tracking application (college project)
**Stack (fixed, do not substitute):** Java, Swing/AWT, JDBC, Oracle Database
**Forbidden:** Spring Boot, Hibernate, JPA, React, Node.js, Firebase, JFreeChart or any other charting library, any 3D engine.
**Goal:** a polished, working, bug-resistant college project. Simplicity and reliability beat feature count.

> Note: this plan covers sections 1-8 of the original brief. The "Character Implementation" section (9) and anything after it was cut off in the source prompt and is not covered here.

---

## 1. Mandatory academic requirements (must all be satisfied)

1. Fitness tracker application
2. Log daily workouts
3. Record calories burned
4. Record body measurements
5. Java Swing/AWT interface
6. Data-entry forms
7. Graphical reports
8. JDBC manages user/activity records
9. Oracle Database
10. Historical fitness data stored
11. Database normalization performed
12. Normalization justified

Build order (section 10) ensures steps 1-4 alone satisfy every requirement. The gamification layer sits on top.

---

## 2. Scope decisions (what was cut or simplified, and why)

| Original idea | Decision | Reason |
|---|---|---|
| Stored `level` | **Derived** in Java | Derived data creates update anomalies and breaks 3NF |
| Stored world stage | **Derived** from level | Same |
| Stored character/pet state | **Derived at runtime** from workout history | State is a pure function of recent activity |
| `CHARACTERS`, `PETS` tables | **Removed**, merged into `PLAYER_STATE` (1:1) | One character and one pet per user |
| `USER_PROGRESS` | Renamed `PLAYER_STATE` | Holds energy, coins, pet name |
| `INVENTORY` | Renamed `USER_ITEMS` | Clearer; composite PK |
| Character states (6) | **4:** IDLE, HAPPY, TIRED, CELEBRATION | WORKOUT is meaningless for form-based logging; ENERGETIC overlaps HAPPY |
| Pet states (5) | **4:** NORMAL, HAPPY, SLEEPY, CELEBRATION | EXCITED overlaps HAPPY |
| Item overlays on every state | **Only cap and pet accessory draw on art** | Cap x 6 states x outfits is an art explosion. Other items show as equipped icons |
| 9 screens | **5 screens + login** | See section 7 |
| Missions hardcoded | **Stored in DB**, generic evaluation by `metric_type` | Data-driven; shows normalization |
| Mission progress counters | **Not stored.** Only completions are stored | Progress is computed live, so there are no sync bugs |
| Charting library | **Hand-drawn with `Graphics2D`** | Satisfies "Swing/AWT graphical reports"; no dependency risk |

---

## 3. Gamification rules

### 3.1 Constants (put in one `GameConfig` class)

```
LEVEL_STEP            = 50      // energy per level (configurable; original proposal was 100)
MIN_REWARD_MINUTES    = 5
MAX_REWARDED_PER_DAY  = 3
```

### 3.2 Workout rewards

| Duration | Energy | Coins |
|---|---|---|
| < 5 min | 0 | 0 (still logged) |
| 5-14 min | +5 | +10 |
| 15-30 min | +10 | +25 |
| 31+ min | +20 | +50 |

- Only the first `MAX_REWARDED_PER_DAY` workouts of a day earn rewards. Further ones are still saved with 0 energy and 0 coins. This prevents spam farming.
- Intensity is **logged only**. It does not affect rewards.
- Ranges must not overlap (the original "15-30" and ">30" both included 30).

### 3.3 Mission rewards

Each mission carries its own `reward_energy` and `reward_coins` (default +20 energy, +50 coins).

### 3.4 Level and world (derived, never stored)

```
level = 1 + totalEnergy / LEVEL_STEP          (cap at 10)
```

With `LEVEL_STEP = 50`:

| World | Starts at level | Total energy needed |
|---|---|---|
| Bedroom | 1 | 0 |
| Backyard | 3 | 100 |
| Park | 5 | 200 |
| Adventure Zone | 8 | 350 |

With 100 per level, Park needs 400 energy (about 20+ workouts), which is too slow for a demo. Keep it configurable.

### 3.5 Calories burned

```
calories = MET x weight_kg x (duration_min / 60)
```

- `met_value` comes from `WORKOUT_TYPES`.
- `weight_kg` is the user's latest body measurement. Default to 70 if none exists.
- The user may override the computed value in the form.
- The final value is **stored** in `WORKOUTS.calories_burned` as a historical snapshot.

### 3.6 Character and pet state (derived at runtime)

| Situation | Character | Pet |
|---|---|---|
| Workout just saved, level-up, or mission just completed (this session) | CELEBRATION | CELEBRATION |
| Worked out today | HAPPY | HAPPY |
| No workout today, last workout 2 days ago or less | IDLE | NORMAL |
| No workout in 3+ days (or never) | TIRED | SLEEPY |

Prompt text follows the same rules: "Ready for today's mission?" before a workout, "Nice work! Mission complete." after.

### 3.7 Coin sink / shop

Prices around 100-250 coins. The first purchase comes after roughly 3 workouts and the full shop after about 20. Reasonable pacing.

---

## 4. Missions (stored in DB)

`metric_type` values and how each is evaluated (all computed live from workout/measurement data):

| metric_type | Evaluation |
|---|---|
| `WORKOUT_COUNT` | `COUNT(*)` of the user's workouts |
| `SINGLE_MINUTES` | `MAX(duration_min)` over the user's workouts |
| `STREAK_DAYS` | Longest consecutive-day run computed in Java from `SELECT DISTINCT workout_date` |
| `TOTAL_ENERGY` | `player_state.total_energy` |
| `TOTAL_CALORIES` | `SUM(calories_burned)` |
| `MEASUREMENT_COUNT` | `COUNT(*)` of body measurements |

**Mission check procedure** (run after every workout or measurement save, inside the same transaction):

1. Load all missions not yet in `USER_MISSIONS` for the user.
2. For each, compute the metric and compare it with `target_value`.
3. If met: insert into `USER_MISSIONS`, add the reward to `PLAYER_STATE`.
4. **Repeat the loop until no new mission completes**, because mission rewards add energy and can complete an energy-based mission.

---

## 5. Database

Oracle 12c+ / XE 18c-21c (identity columns). On 11g XE, replace identity columns with sequences + triggers.

### 5.1 `database/01_create_tables.sql`

```sql
CREATE TABLE users (
  user_id        NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  username       VARCHAR2(30)  NOT NULL UNIQUE,
  email          VARCHAR2(100) NOT NULL UNIQUE,
  password_hash  VARCHAR2(128) NOT NULL,          -- salted SHA-256, never plain text
  salt           VARCHAR2(32)  NOT NULL,
  full_name      VARCHAR2(60)  NOT NULL,
  gender         VARCHAR2(10)  CHECK (gender IN ('MALE','FEMALE','OTHER')),
  birth_date     DATE,
  height_cm      NUMBER(5,1)   CHECK (height_cm BETWEEN 50 AND 250),
  created_on     DATE DEFAULT SYSDATE NOT NULL
);

CREATE TABLE player_state (
  user_id        NUMBER PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
  total_energy   NUMBER(7) DEFAULT 0 NOT NULL CHECK (total_energy >= 0),
  coins          NUMBER(7) DEFAULT 0 NOT NULL CHECK (coins >= 0),
  pet_name       VARCHAR2(20) DEFAULT 'Buddy' NOT NULL
);

CREATE TABLE workout_types (
  type_id        NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  type_name      VARCHAR2(40) NOT NULL UNIQUE,
  met_value      NUMBER(4,1)  NOT NULL CHECK (met_value > 0)
);

CREATE TABLE workouts (
  workout_id      NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id         NUMBER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  type_id         NUMBER NOT NULL REFERENCES workout_types(type_id),
  workout_date    DATE   NOT NULL,                 -- always store TRUNC(date)
  duration_min    NUMBER(4) NOT NULL CHECK (duration_min BETWEEN 1 AND 600),
  intensity       VARCHAR2(6) NOT NULL CHECK (intensity IN ('LOW','MEDIUM','HIGH')),
  calories_burned NUMBER(6) NOT NULL CHECK (calories_burned >= 0),
  energy_earned   NUMBER(4) DEFAULT 0 NOT NULL,
  coins_earned    NUMBER(4) DEFAULT 0 NOT NULL,
  notes           VARCHAR2(200)
);
CREATE INDEX ix_workouts_user_date ON workouts(user_id, workout_date);

CREATE TABLE body_measurements (
  measurement_id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id        NUMBER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  measured_on    DATE   NOT NULL,
  weight_kg      NUMBER(5,2) NOT NULL CHECK (weight_kg BETWEEN 20 AND 400),
  waist_cm       NUMBER(5,1) CHECK (waist_cm > 0),
  chest_cm       NUMBER(5,1) CHECK (chest_cm > 0),
  hips_cm        NUMBER(5,1) CHECK (hips_cm > 0),
  CONSTRAINT uq_meas_user_day UNIQUE (user_id, measured_on)
);

CREATE TABLE goals (
  goal_id          NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id          NUMBER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  goal_type        VARCHAR2(15) NOT NULL CHECK (goal_type IN ('LOSE_WEIGHT','GAIN_MUSCLE','STAY_FIT')),
  target_weight_kg NUMBER(5,2),
  weekly_workouts  NUMBER(2) NOT NULL CHECK (weekly_workouts BETWEEN 1 AND 14),
  start_date       DATE DEFAULT SYSDATE NOT NULL,
  is_active        CHAR(1) DEFAULT 'Y' NOT NULL CHECK (is_active IN ('Y','N'))
);

CREATE TABLE missions (
  mission_id     NUMBER PRIMARY KEY,
  title          VARCHAR2(60) NOT NULL UNIQUE,
  metric_type    VARCHAR2(20) NOT NULL CHECK (metric_type IN
     ('WORKOUT_COUNT','SINGLE_MINUTES','STREAK_DAYS','TOTAL_ENERGY','TOTAL_CALORIES','MEASUREMENT_COUNT')),
  target_value   NUMBER(6) NOT NULL CHECK (target_value > 0),
  reward_energy  NUMBER(4) DEFAULT 20 NOT NULL,
  reward_coins   NUMBER(4) DEFAULT 50 NOT NULL
);

CREATE TABLE user_missions (                       -- row exists = completed
  user_id        NUMBER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  mission_id     NUMBER NOT NULL REFERENCES missions(mission_id),
  completed_on   DATE DEFAULT SYSDATE NOT NULL,
  PRIMARY KEY (user_id, mission_id)
);

CREATE TABLE items (
  item_id        NUMBER PRIMARY KEY,
  item_name      VARCHAR2(40) NOT NULL UNIQUE,
  slot           VARCHAR2(12) NOT NULL CHECK (slot IN ('CAP','SHOES','PET_ACC','ROOM','OUTFIT')),
  price_coins    NUMBER(5) NOT NULL CHECK (price_coins > 0),
  image_file     VARCHAR2(60) NOT NULL
);

CREATE TABLE user_items (
  user_id        NUMBER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  item_id        NUMBER NOT NULL REFERENCES items(item_id),
  purchased_on   DATE DEFAULT SYSDATE NOT NULL,
  is_equipped    CHAR(1) DEFAULT 'N' NOT NULL CHECK (is_equipped IN ('Y','N')),
  PRIMARY KEY (user_id, item_id)
);
```

**Rules enforced in Java (not the database):**
- Only one equipped item per `slot` per user. When equipping, un-equip the other item in that slot first (same transaction).
- Level, world, BMI and streaks are never stored.

### 5.2 `database/02_seed_data.sql`

```sql
INSERT INTO workout_types (type_name, met_value) VALUES ('Walking', 3.5);
INSERT INTO workout_types (type_name, met_value) VALUES ('Running', 9.8);
INSERT INTO workout_types (type_name, met_value) VALUES ('Cycling', 7.5);
INSERT INTO workout_types (type_name, met_value) VALUES ('Swimming', 7.0);
INSERT INTO workout_types (type_name, met_value) VALUES ('Strength Training', 5.0);
INSERT INTO workout_types (type_name, met_value) VALUES ('HIIT', 8.0);
INSERT INTO workout_types (type_name, met_value) VALUES ('Yoga', 2.5);
INSERT INTO workout_types (type_name, met_value) VALUES ('Jump Rope', 11.0);
INSERT INTO workout_types (type_name, met_value) VALUES ('Dancing', 5.5);
INSERT INTO workout_types (type_name, met_value) VALUES ('Stretching', 2.3);

INSERT INTO missions VALUES (1,  'First Steps',     'WORKOUT_COUNT',     1, 20, 50);
INSERT INTO missions VALUES (2,  'Quarter Hour',    'SINGLE_MINUTES',   15, 20, 50);
INSERT INTO missions VALUES (3,  'Hat Trick',       'WORKOUT_COUNT',     3, 20, 50);
INSERT INTO missions VALUES (4,  'Half-Hour Hero',  'SINGLE_MINUTES',   30, 20, 50);
INSERT INTO missions VALUES (5,  'Five-Day Streak', 'STREAK_DAYS',       5, 20, 50);
INSERT INTO missions VALUES (6,  'Energy 200',      'TOTAL_ENERGY',    200, 20, 50);
INSERT INTO missions VALUES (7,  'Measure Up',      'MEASUREMENT_COUNT', 1, 20, 50);
INSERT INTO missions VALUES (8,  'Ten Down',        'WORKOUT_COUNT',    10, 20, 50);
INSERT INTO missions VALUES (9,  'Burn 1000',       'TOTAL_CALORIES', 1000, 20, 50);
INSERT INTO missions VALUES (10, 'Seven-Day Streak','STREAK_DAYS',       7, 20, 50);

INSERT INTO items VALUES (1, 'Sporty Cap',      'CAP',     100, 'cap.png');
INSERT INTO items VALUES (2, 'Running Shoes',   'SHOES',   150, 'shoes.png');
INSERT INTO items VALUES (3, 'Pet Bandana',     'PET_ACC', 120, 'pet_bandana.png');
INSERT INTO items VALUES (4, 'Room Poster',     'ROOM',    200, 'room_poster.png');
INSERT INTO items VALUES (5, 'Workout Outfit',  'OUTFIT',  250, 'outfit.png');

COMMIT;
```

### 5.3 `database/03_demo_user.sql`

Create one demo user with roughly 3-4 weeks of workouts and about 6 weekly body measurements, so charts are populated for demonstration. Also insert a matching `player_state` row consistent with the sum of the demo workouts' `energy_earned` and `coins_earned`.

### 5.4 `database/04_sample_queries.sql`

Include examples for: weekly calories per day, workouts per week, weight over time, total energy per user, missions completed per user, and a join across `users`, `workouts`, and `workout_types`.

### 5.5 Relationships and cardinalities

- USERS 1:1 PLAYER_STATE
- USERS 1:N WORKOUTS
- USERS 1:N BODY_MEASUREMENTS
- USERS 1:N GOALS
- WORKOUT_TYPES 1:N WORKOUTS
- USERS M:N MISSIONS (via USER_MISSIONS)
- USERS M:N ITEMS (via USER_ITEMS)

### 5.6 Candidate keys

- USERS: `user_id`, `username`, `email`
- WORKOUT_TYPES: `type_id`, `type_name`
- BODY_MEASUREMENTS: `measurement_id`, `(user_id, measured_on)`
- MISSIONS: `mission_id`, `title`
- ITEMS: `item_id`, `item_name`

---

## 6. Normalization and justification

**Unnormalized starting point:** one wide sheet such as `user_name, email, workout_type, MET, date, duration, "pushups, squats, plank", weight, waist, mission_title, reward, item_name, price ...`

### 1NF
- All columns are atomic.
- A day with several exercises is several `WORKOUTS` rows, not a comma-separated list.
- Measurements are separate columns, not repeating groups inside one cell.

### 2NF
- Tables with a single-column surrogate PK have no partial dependencies.
- The composite-key tables are `USER_MISSIONS (user_id, mission_id)` and `USER_ITEMS (user_id, item_id)`. Their non-key attributes (`completed_on`, `purchased_on`, `is_equipped`) depend on the **whole** key.
- Mission title and rewards, and item name and price, are **not** stored in those tables. They live in `MISSIONS` and `ITEMS`.

### 3NF
- `met_value` depends on `type_id`, not on `workout_id`, so it sits in `WORKOUT_TYPES`.
- `price_coins` and `slot` depend on `item_id`, so they sit in `ITEMS`.
- Level, world stage, BMI, streaks and character/pet state are derived and never stored, so there are no transitive dependencies.
- Height is stored once in `USERS`, not repeated in every measurement.

BCNF also holds: every determinant below is a candidate key.

### Functional dependencies

- `user_id -> username, email, password_hash, salt, full_name, gender, birth_date, height_cm, created_on`
- `email -> user_id`; `username -> user_id`
- `user_id -> total_energy, coins, pet_name` (PLAYER_STATE)
- `type_id -> type_name, met_value`
- `workout_id -> user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes`
- `(user_id, measured_on) -> weight_kg, waist_cm, chest_cm, hips_cm`
- `goal_id -> user_id, goal_type, target_weight_kg, weekly_workouts, start_date, is_active`
- `mission_id -> title, metric_type, target_value, reward_energy, reward_coins`
- `(user_id, mission_id) -> completed_on`
- `item_id -> item_name, slot, price_coins, image_file`
- `(user_id, item_id) -> purchased_on, is_equipped`

### Deliberate, documented denormalization (state this in the report)

- `PLAYER_STATE.total_energy` and `coins` are **stored balances**. Coins decrease when the user spends, so a stored balance is safer than recomputing from all history. They are updated in the same JDBC transaction as the event that changes them.
- `WORKOUTS.energy_earned`, `coins_earned` and `calories_burned` are **historical snapshots**. The reward rules or the user's weight may change later, but the past record must not.

---

## 7. UI

Single `JFrame` with `CardLayout` and a left sidebar. No pop-up windows except small dialogs (reward popup, confirmations).

| # | Screen | Contents |
|---|---|---|
| 0 | **Login / Register** | One window, two tabs |
| 1 | **Dashboard** | World background (by level), character, pet, energy bar to next level, coins, mission panel (progress per mission), level roadmap strip (Bedroom, Backyard, Park, Adventure Zone), "Log Workout" button |
| 2 | **Log** | Tabs: **Workout** form and **Body Measurements** form. After a workout save, show a reward popup and play a ~2s character/pet reaction |
| 3 | **Reports** | Weekly summary (workouts, minutes, calories, energy earned) and charts: calories per day, workouts per week, weight over time |
| 4 | **Shop & Items** | Buy with coins, equip/unequip, owned items |
| 5 | **Profile & Goals** | Edit details, set active goal, set pet name |

The old "World" screen is merged into the Dashboard roadmap. The old "Customization" screen is the Shop & Items screen.

### Data-entry forms

- **Workout form:** type (combo box from DB), date (default today, cannot be in the future), duration, intensity, calories (auto-computed, editable), notes.
- **Measurement form:** date, weight (required), waist, chest, hips (optional). One measurement per day; saving the same day again should offer to update.

### Charts

Two reusable components drawn with `Graphics2D`: `BarChartPanel` and `LineChartPanel`. Include axes, gridlines, labels, a title, and a friendly "no data yet" state.

---

## 8. Java architecture

Layers: **UI (Swing/AWT)** -> **Services** -> **DAO** -> **JDBC** -> **Oracle**.

| Package | Classes |
|---|---|
| `model` | `User`, `Workout`, `WorkoutType`, `BodyMeasurement`, `Goal`, `Mission`, `Item`, `PlayerState` |
| `dao` | `UserDao`, `WorkoutDao`, `MeasurementDao`, `GoalDao`, `MissionDao`, `ItemDao`, `PlayerStateDao` |
| `service` | `AuthService`, `WorkoutService`, `GamificationService`, `MissionService`, `ReportService`, `ShopService` |
| `ui` | `LoginFrame`, `MainFrame`, `panels/*`, `components/*` |
| `util` | `DBConnection`, `Session`, `PasswordUtil`, `ImageCache`, `Validator`, `GameConfig` |

Responsibilities:

- **`AuthService`**: registration, login, salted password hashing.
- **`GamificationService`**: pure rules only (reward calculation, level, world stage, character/pet state, calorie estimate). **No database access**, so it is easy to test.
- **`WorkoutService`**: the orchestrator. Owns the transaction for saving a workout.
- **`MissionService`**: mission evaluation loop (section 4).
- **`ReportService`**: chart and weekly summary queries.
- **`ShopService`**: purchase and equip logic.

### Save-workout transaction

```
setAutoCommit(false)
  1. validate input
  2. compute calories (if not overridden)
  3. count today's rewarded workouts -> compute energy/coins
  4. INSERT workout
  5. UPDATE player_state (energy, coins)
  6. run mission loop (INSERT user_missions, UPDATE player_state)
commit   // on any exception: rollback, show a friendly error
```

### Project structure

```
FitSo/
├── lib/                          ojdbc11.jar (or ojdbc8.jar)
├── config.properties             DB URL, user, password
├── src/
│   ├── Main.java
│   ├── model/
│   ├── dao/
│   ├── service/
│   ├── ui/
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── panels/               DashboardPanel, LogPanel, ReportsPanel,
│   │   │                         ShopPanel, ProfilePanel
│   │   └── components/           BarChartPanel, LineChartPanel, EnergyBar,
│   │                             CharacterView, Theme
│   └── util/
├── assets/
│   ├── characters/               idle.png, happy.png, tired.png, celebration.png
│   ├── pets/                     normal.png, happy.png, sleepy.png, celebration.png
│   ├── worlds/                   bedroom.png, backyard.png, park.png, adventure.png
│   └── items/                    cap.png, shoes.png, pet_bandana.png, ...
├── database/
│   ├── 01_create_tables.sql
│   ├── 02_seed_data.sql
│   ├── 03_demo_user.sql
│   └── 04_sample_queries.sql
└── docs/                         ER diagram, normalization write-up
```

---

## 9. Technical rules and risks (hard requirements for the implementation)

1. **JDBC safety:** `PreparedStatement` everywhere (no string-concatenated SQL), `try-with-resources` for connections/statements/result sets.
2. **Transactions:** one transaction per workout save and per shop purchase. Roll back on any failure.
3. **Passwords:** salted SHA-256 via `PasswordUtil`. Never store or log plain text.
4. **Oracle `DATE` includes a time component.** Always store `TRUNC` dates and compare with `TRUNC`.
5. **Streaks** are computed in Java from `SELECT DISTINCT workout_date`. No stored counter.
6. **Config:** DB URL and credentials come from `config.properties`.
   - XE 21c: `jdbc:oracle:thin:@//localhost:1521/XEPDB1`
   - 11g XE: `jdbc:oracle:thin:@//localhost:1521/XE`
7. **Threading:** direct DB calls on the Swing event thread are acceptable at this scale. Use `SwingWorker` only for login and report loading if anything lags.
8. **Images:** load once via `ImageCache`. Use transparent PNGs on identical canvas sizes per category. If a file is missing, draw a placeholder instead of crashing.
9. **Validation:** all input is checked in `Validator` before reaching a DAO, with friendly error messages (no stack traces in the UI).
10. **No unnecessary technology:** nothing outside Java, Swing/AWT, JDBC and Oracle (plus the Oracle JDBC driver jar).
11. **Mission loop** must repeat until no new mission completes (section 4).
12. **Equip rule:** one item per slot, enforced in Java inside a transaction.

---

## 10. Build order

1. Database scripts + `DBConnection`; verify the connection works.
2. `AuthService` + Login/Register UI.
3. Workout and measurement DAOs + Log screen (forms).
4. Reports screen with the two Graphics2D chart components.
   - **Steps 1-4 satisfy every mandatory academic requirement.**
5. `GamificationService`, `MissionService`, and the Dashboard.
6. Shop & Items and Profile & Goals screens.
7. Art, polish, demo user, and documentation (ER diagram, normalization write-up).

If time gets tight, stop after any step with a fully working application.

---

## 11. Definition of done

- [ ] Register, login, logout work; passwords are hashed.
- [ ] Workouts and measurements can be logged with validation.
- [ ] Calories are auto-estimated and editable.
- [ ] At least 3 graphical reports render from live Oracle data.
- [ ] Saving a workout updates energy, coins, level, missions and character/pet reaction in one transaction.
- [ ] World background changes at levels 3, 5 and 8.
- [ ] Shop purchase and equip work, with insufficient-coins handling.
- [ ] All 10 tables created with PK/FK/CHECK/UNIQUE constraints.
- [ ] Normalization write-up (1NF/2NF/3NF, FDs, documented denormalizations) included in `docs/`.
- [ ] Demo user with historical data so charts are never empty.
- [ ] No unhandled exceptions reach the user.
