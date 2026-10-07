# FitSo Database Normalization Write-Up

## 1. Unnormalized Starting Point (UNF)
In an unnormalized design, all user actions, fitness tracking, and gamification rewards might exist in a single flat table or spreadsheet:
`UserSheet(username, email, password, full_name, workout_type, met_value, workout_date, duration, intensity, calories_burned, energy_earned, coins_earned, weight_kg, waist_cm, chest_cm, hips_cm, goal_type, target_weight, mission_title, mission_target, reward_energy, item_name, slot, price, equipped)`

This leads to massive data redundancy, insertion anomalies, update anomalies, and deletion anomalies.

---

## 2. First Normal Form (1NF)
**Requirement:** All attributes must be atomic (no repeating groups, no multi-valued attributes, no arrays/delimited strings).

**Transformations Applied:**
- Exercise lists (e.g. "Running, Pushups") are separated into distinct rows in `WORKOUTS`.
- Body measurements are stored as distinct atomic attributes (`weight_kg`, `waist_cm`, `chest_cm`, `hips_cm`) rather than unstructured key-value blocks.
- Multiple items purchased or equipped are moved out of the user table into `USER_ITEMS`.

---

## 3. Second Normal Form (2NF)
**Requirement:** Must be in 1NF, and every non-key attribute must be fully functionally dependent on the primary key (no partial dependencies on composite keys).

**Transformations Applied:**
- In junction/mapping tables with composite primary keys:
  - `USER_MISSIONS (user_id, mission_id)`: Attribute `completed_on` depends on the **entire** composite key `(user_id, mission_id)`. Mission details (`title`, `reward_energy`) depend only on `mission_id` and are separated into `MISSIONS`.
  - `USER_ITEMS (user_id, item_id)`: Attributes `purchased_on` and `is_equipped` depend on the **entire** composite key `(user_id, item_id)`. Item attributes (`item_name`, `slot`, `price_coins`) depend only on `item_id` and sit in `ITEMS`.
- Single-column surrogate primary keys (`user_id`, `workout_id`, `type_id`, `measurement_id`, `goal_id`, `mission_id`, `item_id`) inherently satisfy 2NF as there are no partial composite keys.

---

## 4. Third Normal Form (3NF) & BCNF
**Requirement:** Must be in 2NF, and no non-key attribute depends transitively on another non-key attribute (X -> Y requires X to be a superkey).

**Transformations Applied:**
- `WORKOUT_TYPES`: `met_value` depends on `type_id` (or `type_name`), not on individual `workout_id` rows. Moving `met_value` to `WORKOUT_TYPES` eliminates transitive dependency `workout_id -> type_id -> met_value`.
- `ITEMS`: `price_coins`, `slot`, and `image_file` depend on `item_id`, eliminating transitive dependency `(user_id, item_id) -> item_id -> price_coins`.
- **Derived Data Elimination:**
  - `level`, `world_stage`, `BMI`, `streak_days`, and `character_state` are **never stored** in database tables. They are calculated dynamically at runtime in Java from raw historical logs (`WORKOUTS`, `PLAYER_STATE`). Storing derived data would introduce transitive dependencies and update anomalies.

---

## 5. Functional Dependencies (FDs)
- `user_id -> username, email, password_hash, salt, full_name, gender, birth_date, height_cm, created_on`
- `username -> user_id`
- `email -> user_id`
- `user_id -> total_energy, coins, pet_name` (in `PLAYER_STATE`)
- `type_id -> type_name, met_value`
- `type_name -> type_id, met_value`
- `workout_id -> user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes`
- `(user_id, measured_on) -> weight_kg, waist_cm, chest_cm, hips_cm`
- `measurement_id -> user_id, measured_on, weight_kg, waist_cm, chest_cm, hips_cm`
- `goal_id -> user_id, goal_type, target_weight_kg, weekly_workouts, start_date, is_active`
- `mission_id -> title, metric_type, target_value, reward_energy, reward_coins`
- `title -> mission_id, metric_type, target_value, reward_energy, reward_coins`
- `(user_id, mission_id) -> completed_on`
- `item_id -> item_name, slot, price_coins, image_file`
- `item_name -> item_id, slot, price_coins, image_file`
- `(user_id, item_id) -> purchased_on, is_equipped`

Every left side of these non-trivial dependencies is a superkey/candidate key, proving **BCNF compliance**.

---

## 6. Documented & Justified Denormalizations
1. `PLAYER_STATE.total_energy` & `PLAYER_STATE.coins`:
   - **Reason:** Stored balances prevent heavy recalculations across all historical records upon every UI render. Spending coins decreases the balance, making absolute balance tracking safer than ledger reconstruction.
   - **Consistency Control:** Updated strictly within the same ACID transaction when workouts or missions earn rewards or shop items are bought.
2. `WORKOUTS.calories_burned`, `energy_earned`, & `coins_earned`:
   - **Reason:** Historical snapshots. If a user updates their body weight or reward rules change in future code updates, past workout log entries must retain their historical reward values.
