-- =========================================================
-- CaloriePilot — seed data
-- =========================================================

-- ---- Foods catalog (public) ----
INSERT INTO foods (name, brand, serving_size_g, calories, protein_g, carbs_g, fat_g, fiber_g) VALUES
('Banana',          NULL,         118, 105.0,  1.3, 27.0,  0.3, 3.1),
('Apple',           NULL,         182,  95.0,  0.5, 25.0,  0.3, 4.4),
('Boiled Egg',      NULL,          50,  78.0,  6.3,  0.6,  5.3, 0.0),
('Greek Yogurt',    'Generic',    170, 100.0, 17.0,  6.0,  0.7, 0.0),
('Oatmeal (cooked)',NULL,         234, 158.0,  6.0, 27.0,  3.2, 4.0),
('Grilled Chicken Breast', NULL, 100, 165.0, 31.0,  0.0,  3.6, 0.0),
('White Rice (cooked)',    NULL, 158, 205.0,  4.3, 45.0,  0.4, 0.6),
('Brown Rice (cooked)',    NULL, 195, 216.0,  5.0, 45.0,  1.8, 3.5),
('Salmon (cooked)', NULL,         100, 208.0, 20.0,  0.0, 13.0, 0.0),
('Avocado',         NULL,         150, 240.0,  3.0, 12.0, 22.0, 10.0),
('Almonds',         NULL,          28, 164.0,  6.0,  6.0, 14.0, 3.5),
('Peanut Butter',   'Generic',     32, 188.0,  8.0,  6.0, 16.0, 2.0),
('Whole Wheat Bread (1 slice)', NULL, 38, 80.0, 4.0, 14.0, 1.0, 2.0),
('Spinach (raw)',   NULL,         100,  23.0,  2.9,  3.6,  0.4, 2.2),
('Broccoli (cooked)', NULL,       156,  55.0,  3.7, 11.0,  0.6, 5.1),
('Sweet Potato (baked)', NULL,    150, 130.0,  2.0, 30.0,  0.1, 4.0),
('Pasta (cooked)',  NULL,         140, 220.0,  8.0, 43.0,  1.3, 2.5),
('Olive Oil (1 tbsp)', NULL,       14, 119.0,  0.0,  0.0, 14.0, 0.0),
('Milk (whole, 1 cup)', NULL,     244, 149.0,  8.0, 12.0,  8.0, 0.0),
('Coffee (black)',  NULL,         240,   2.0,  0.3,  0.0,  0.0, 0.0);

-- ---- Achievements catalog ----
INSERT INTO achievements (code, title, description, icon, threshold) VALUES
('FIRST_STEP',        'First Steps',          'Log your first day of steps.',         'footprints',       1),
('STEPS_10K',         '10K Club',             'Hit 10,000 steps in a single day.',    'medal',         10000),
('STEPS_15K',         'Mile Crusher',         'Hit 15,000 steps in a single day.',    'medal',         15000),
('STREAK_3',          '3-Day Streak',         'Hit your step goal 3 days in a row.',  'flame',             3),
('STREAK_7',          'Week Warrior',         'Hit your step goal 7 days in a row.',  'flame',             7),
('STREAK_30',         'Monthly Machine',      'Hit your step goal 30 days in a row.', 'flame',            30),
('WATER_HYDRATED',    'Stay Hydrated',        'Log your water goal in a single day.', 'droplet',           1),
('FIRST_MEAL',        'Meal Logger',          'Log your first meal.',                  'utensils',          1),
('WEIGHT_FIRST_LOG',  'On the Scale',         'Log your weight for the first time.',  'scale',             1);
