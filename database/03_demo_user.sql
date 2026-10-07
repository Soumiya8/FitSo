-- FitSo Demo User Data Script
-- Creates demo user "alex_fit" password "Password123!" with 3-4 weeks of workout & measurement history

DECLARE
  v_user_id NUMBER;
  v_type_walking NUMBER;
  v_type_running NUMBER;
  v_type_cycling NUMBER;
  v_type_strength NUMBER;
  v_type_hiit NUMBER;
BEGIN
  -- Check if user already exists
  BEGIN
    SELECT user_id INTO v_user_id FROM users WHERE username = 'alex_fit';
    DELETE FROM workouts WHERE user_id = v_user_id;
    DELETE FROM body_measurements WHERE user_id = v_user_id;
    DELETE FROM user_missions WHERE user_id = v_user_id;
    DELETE FROM user_items WHERE user_id = v_user_id;
    DELETE FROM goals WHERE user_id = v_user_id;
    DELETE FROM player_state WHERE user_id = v_user_id;
    DELETE FROM users WHERE user_id = v_user_id;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN NULL;
  END;

  -- Create User (password: Password123!, salt: demo_salt_123456)
  -- SHA-256 of "demo_salt_123456Password123!"
  INSERT INTO users (username, email, password_hash, salt, full_name, gender, birth_date, height_cm, created_on)
  VALUES ('alex_fit', 'alex@fitso.com', 
          '0c5415bc26960d738f6236b568393e8e89524bbff460f4e4e94119857d472c6e', 
          'demo_salt_123456', 'Alex Taylor', 'MALE', TO_DATE('1998-05-15', 'YYYY-MM-DD'), 175.0, TRUNC(SYSDATE - 30))
  RETURNING user_id INTO v_user_id;

  -- Create Player State (Calculated energy & coins from history: 210 Energy, 480 Coins)
  INSERT INTO player_state (user_id, total_energy, coins, pet_name)
  VALUES (v_user_id, 210, 480, 'Rocky');

  -- Get Workout Type IDs
  SELECT type_id INTO v_type_walking FROM workout_types WHERE type_name = 'Walking';
  SELECT type_id INTO v_type_running FROM workout_types WHERE type_name = 'Running';
  SELECT type_id INTO v_type_cycling FROM workout_types WHERE type_name = 'Cycling';
  SELECT type_id INTO v_type_strength FROM workout_types WHERE type_name = 'Strength Training';
  SELECT type_id INTO v_type_hiit FROM workout_types WHERE type_name = 'HIIT';

  -- Create Goals
  INSERT INTO goals (user_id, goal_type, target_weight_kg, weekly_workouts, start_date, is_active)
  VALUES (v_user_id, 'LOSE_WEIGHT', 70.0, 5, TRUNC(SYSDATE - 30), 'Y');

  -- Create Body Measurements (Weekly for 4 weeks)
  INSERT INTO body_measurements (user_id, measured_on, weight_kg, waist_cm, chest_cm, hips_cm)
  VALUES (v_user_id, TRUNC(SYSDATE - 28), 75.5, 86.0, 98.0, 96.0);

  INSERT INTO body_measurements (user_id, measured_on, weight_kg, waist_cm, chest_cm, hips_cm)
  VALUES (v_user_id, TRUNC(SYSDATE - 21), 74.8, 85.2, 98.2, 95.5);

  INSERT INTO body_measurements (user_id, measured_on, weight_kg, waist_cm, chest_cm, hips_cm)
  VALUES (v_user_id, TRUNC(SYSDATE - 14), 74.0, 84.5, 98.5, 95.0);

  INSERT INTO body_measurements (user_id, measured_on, weight_kg, waist_cm, chest_cm, hips_cm)
  VALUES (v_user_id, TRUNC(SYSDATE - 7), 73.2, 83.8, 99.0, 94.2);

  INSERT INTO body_measurements (user_id, measured_on, weight_kg, waist_cm, chest_cm, hips_cm)
  VALUES (v_user_id, TRUNC(SYSDATE - 1), 72.5, 83.0, 99.5, 94.0);

  -- Create Workouts over past 3 weeks
  -- Week 3 ago
  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_running, TRUNC(SYSDATE - 21), 30, 'HIGH', 368, 10, 25, 'Morning jog in the park');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_strength, TRUNC(SYSDATE - 19), 45, 'MEDIUM', 281, 20, 50, 'Upper body strength session');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_cycling, TRUNC(SYSDATE - 17), 35, 'HIGH', 324, 20, 50, 'Evening outdoor ride');

  -- Week 2 ago
  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_hiit, TRUNC(SYSDATE - 14), 25, 'HIGH', 247, 10, 25, 'High intensity interval training');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_running, TRUNC(SYSDATE - 12), 40, 'HIGH', 484, 20, 50, 'Pushed for longer distance');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_walking, TRUNC(SYSDATE - 10), 20, 'LOW', 86, 10, 25, 'Light recovery walk');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_strength, TRUNC(SYSDATE - 8), 50, 'HIGH', 308, 20, 50, 'Leg day and core');

  -- Week 1 ago
  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_running, TRUNC(SYSDATE - 6), 35, 'HIGH', 418, 20, 50, 'Tempo run');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_cycling, TRUNC(SYSDATE - 4), 45, 'MEDIUM', 411, 20, 50, 'Scenic route cycling');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_hiit, TRUNC(SYSDATE - 2), 30, 'HIGH', 290, 10, 25, 'Full body HIIT');

  INSERT INTO workouts (user_id, type_id, workout_date, duration_min, intensity, calories_burned, energy_earned, coins_earned, notes)
  VALUES (v_user_id, v_type_running, TRUNC(SYSDATE - 1), 20, 'MEDIUM', 237, 10, 25, 'Quick speed session');

  -- Completed Missions for Demo User
  INSERT INTO user_missions (user_id, mission_id, completed_on) VALUES (v_user_id, 1, TRUNC(SYSDATE - 21));
  INSERT INTO user_missions (user_id, mission_id, completed_on) VALUES (v_user_id, 2, TRUNC(SYSDATE - 21));
  INSERT INTO user_missions (user_id, mission_id, completed_on) VALUES (v_user_id, 3, TRUNC(SYSDATE - 17));
  INSERT INTO user_missions (user_id, mission_id, completed_on) VALUES (v_user_id, 4, TRUNC(SYSDATE - 21));
  INSERT INTO user_missions (user_id, mission_id, completed_on) VALUES (v_user_id, 7, TRUNC(SYSDATE - 28));

  -- Purchased & Equipped Items
  INSERT INTO user_items (user_id, item_id, purchased_on, is_equipped) VALUES (v_user_id, 1, TRUNC(SYSDATE - 15), 'Y'); -- Sporty Cap
  INSERT INTO user_items (user_id, item_id, purchased_on, is_equipped) VALUES (v_user_id, 3, TRUNC(SYSDATE - 10), 'Y'); -- Pet Bandana

  COMMIT;
END;
/
