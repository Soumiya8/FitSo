-- FitSo Seed Data Script
DELETE FROM user_items;
DELETE FROM items;
DELETE FROM user_missions;
DELETE FROM missions;
DELETE FROM workout_types;

-- Workout Types
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

-- Missions
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (1,  'First Steps',     'WORKOUT_COUNT',     1, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (2,  'Quarter Hour',    'SINGLE_MINUTES',   15, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (3,  'Hat Trick',       'WORKOUT_COUNT',     3, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (4,  'Half-Hour Hero',  'SINGLE_MINUTES',   30, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (5,  'Five-Day Streak', 'STREAK_DAYS',       5, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (6,  'Energy 200',      'TOTAL_ENERGY',    200, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (7,  'Measure Up',      'MEASUREMENT_COUNT', 1, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (8,  'Ten Down',        'WORKOUT_COUNT',    10, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (9,  'Burn 1000',       'TOTAL_CALORIES', 1000, 20, 50);
INSERT INTO missions (mission_id, title, metric_type, target_value, reward_energy, reward_coins) VALUES (10, 'Seven-Day Streak','STREAK_DAYS',       7, 20, 50);

-- Shop Items
INSERT INTO items (item_id, item_name, slot, price_coins, image_file) VALUES (1, 'Sporty Cap',      'CAP',     100, 'cap.png');
INSERT INTO items (item_id, item_name, slot, price_coins, image_file) VALUES (2, 'Running Shoes',   'SHOES',   150, 'shoes.png');
INSERT INTO items (item_id, item_name, slot, price_coins, image_file) VALUES (3, 'Pet Bandana',     'PET_ACC', 120, 'pet_bandana.png');
INSERT INTO items (item_id, item_name, slot, price_coins, image_file) VALUES (4, 'Room Poster',     'ROOM',    200, 'room_poster.png');
INSERT INTO items (item_id, item_name, slot, price_coins, image_file) VALUES (5, 'Workout Outfit',  'OUTFIT',  250, 'outfit.png');

COMMIT;
