# FitSo Entity-Relationship Diagram

## Mermaid ER Diagram

```mermaid
erDiagram
    USERS ||--|| PLAYER_STATE : "1:1 owns"
    USERS ||--o{ WORKOUTS : "1:N logs"
    USERS ||--o{ BODY_MEASUREMENTS : "1:N records"
    USERS ||--o{ GOALS : "1:N sets"
    USERS ||--o{ USER_MISSIONS : "M:N achieves"
    USERS ||--o{ USER_ITEMS : "M:N owns"
    
    WORKOUT_TYPES ||--o{ WORKOUTS : "1:N categorizes"
    MISSIONS ||--o{ USER_MISSIONS : "M:N completed_by"
    ITEMS ||--o{ USER_ITEMS : "M:N purchased_by"

    USERS {
        NUMBER user_id PK
        VARCHAR2 username UK
        VARCHAR2 email UK
        VARCHAR2 password_hash
        VARCHAR2 salt
        VARCHAR2 full_name
        VARCHAR2 gender
        DATE birth_date
        NUMBER height_cm
        DATE created_on
    }

    PLAYER_STATE {
        NUMBER user_id PK, FK
        NUMBER total_energy
        NUMBER coins
        VARCHAR2 pet_name
    }

    WORKOUT_TYPES {
        NUMBER type_id PK
        VARCHAR2 type_name UK
        NUMBER met_value
    }

    WORKOUTS {
        NUMBER workout_id PK
        NUMBER user_id FK
        NUMBER type_id FK
        DATE workout_date
        NUMBER duration_min
        VARCHAR2 intensity
        NUMBER calories_burned
        NUMBER energy_earned
        NUMBER coins_earned
        VARCHAR2 notes
    }

    BODY_MEASUREMENTS {
        NUMBER measurement_id PK
        NUMBER user_id FK
        DATE measured_on
        NUMBER weight_kg
        NUMBER waist_cm
        NUMBER chest_cm
        NUMBER hips_cm
    }

    GOALS {
        NUMBER goal_id PK
        NUMBER user_id FK
        VARCHAR2 goal_type
        NUMBER target_weight_kg
        NUMBER weekly_workouts
        DATE start_date
        CHAR is_active
    }

    MISSIONS {
        NUMBER mission_id PK
        VARCHAR2 title UK
        VARCHAR2 metric_type
        NUMBER target_value
        NUMBER reward_energy
        NUMBER reward_coins
    }

    USER_MISSIONS {
        NUMBER user_id PK, FK
        NUMBER mission_id PK, FK
        DATE completed_on
    }

    ITEMS {
        NUMBER item_id PK
        VARCHAR2 item_name UK
        VARCHAR2 slot
        NUMBER price_coins
        VARCHAR2 image_file
    }

    USER_ITEMS {
        NUMBER user_id PK, FK
        NUMBER item_id PK, FK
        DATE purchased_on
        CHAR is_equipped
    }
```

## Cardinality & Relationship Summary
1. `USERS` (1) to `PLAYER_STATE` (1): Mandatory 1:1 relationship created at user registration.
2. `USERS` (1) to `WORKOUTS` (N): One user can log many workouts.
3. `WORKOUT_TYPES` (1) to `WORKOUTS` (N): One workout type categorizes many workout instances.
4. `USERS` (1) to `BODY_MEASUREMENTS` (N): One user records progress measurements over time.
5. `USERS` (1) to `GOALS` (N): One user sets zero or active/historical goals.
6. `USERS` (M) to `MISSIONS` (N) via `USER_MISSIONS`: Many-to-Many resolution table storing completion date.
7. `USERS` (M) to `ITEMS` (N) via `USER_ITEMS`: Many-to-Many resolution table storing purchase date and active equipped status.
