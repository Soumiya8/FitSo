-- FitSo Sample Analytical Queries

-- 1. Daily calories burned over the last 7 days for a given user
SELECT workout_date, SUM(calories_burned) AS total_calories
FROM workouts
WHERE user_id = 1
  AND workout_date >= TRUNC(SYSDATE) - 7
GROUP BY workout_date
ORDER BY workout_date ASC;

-- 2. Workouts logged per week for the last 8 weeks
SELECT TRUNC(workout_date, 'IW') AS week_start, COUNT(*) AS workout_count
FROM workouts
WHERE user_id = 1
  AND workout_date >= TRUNC(SYSDATE) - 56
GROUP BY TRUNC(workout_date, 'IW')
ORDER BY week_start ASC;

-- 3. Body weight trend over time
SELECT measured_on, weight_kg, waist_cm, chest_cm, hips_cm
FROM body_measurements
WHERE user_id = 1
ORDER BY measured_on ASC;

-- 4. Total energy, coins, and current level derivation per user
SELECT u.username, 
       ps.total_energy, 
       ps.coins, 
       (1 + FLOOR(ps.total_energy / 50)) AS current_level,
       CASE 
         WHEN (1 + FLOOR(ps.total_energy / 50)) >= 8 THEN 'Adventure Zone'
         WHEN (1 + FLOOR(ps.total_energy / 50)) >= 5 THEN 'Park'
         WHEN (1 + FLOOR(ps.total_energy / 50)) >= 3 THEN 'Backyard'
         ELSE 'Bedroom'
       END AS world_stage
FROM users u
JOIN player_state ps ON u.user_id = ps.user_id;

-- 5. Missions completed vs pending for a user
SELECT m.mission_id, m.title, m.metric_type, m.target_value,
       CASE WHEN um.user_id IS NOT NULL THEN 'COMPLETED' ELSE 'PENDING' END AS status,
       um.completed_on
FROM missions m
LEFT JOIN user_missions um ON m.mission_id = um.mission_id AND um.user_id = 1
ORDER BY m.mission_id ASC;

-- 6. Comprehensive workout summary join across users, workouts, and workout_types
SELECT u.username,
       w.workout_date,
       wt.type_name,
       wt.met_value,
       w.duration_min,
       w.intensity,
       w.calories_burned,
       w.energy_earned,
       w.coins_earned
FROM workouts w
JOIN users u ON w.user_id = u.user_id
JOIN workout_types wt ON w.type_id = wt.type_id
WHERE u.user_id = 1
ORDER BY w.workout_date DESC, w.workout_id DESC;
