/* Level 1 comment
   /* Level 2 comment
      /* Level 3 comment */
   Level 2 comment */
Level 1 comment
*/

/* Level 1 comment
   /* Level 2 comment */
Level 1 comment
*/
CREATE DATABASE IF NOT EXISTS test_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY,
    name VARCHAR,
    age INT,
    status VARCHAR,
    country VARCHAR
);

CREATE INDEX idx_users_name ON users (name);

CREATE VIEW active_users AS
SELECT name, age, country
FROM users
WHERE status = 'active';

-- ADD COLUMN
ALTER TABLE users ADD COLUMN email VARCHAR;

-- DROP COLUMN
ALTER TABLE users DROP COLUMN age;

-- MODIFY COLUMN (تغيير النوع / التعريف)
ALTER TABLE users MODIFY COLUMN name VARCHAR;

-- CHANGE COLUMN (تغيير الاسم + التعريف)
ALTER TABLE users CHANGE COLUMN name full_name VARCHAR;

-- RENAME COLUMN
ALTER TABLE users RENAME COLUMN status TO state;

-- RENAME TABLE
ALTER TABLE users RENAME TO app_users;

-- ADD PRIMARY KEY CONSTRAINT
ALTER TABLE app_users ADD CONSTRAINT pk_users PRIMARY KEY (id);

-- DROP PRIMARY KEY
ALTER TABLE app_users DROP PRIMARY KEY;

-- ADD UNIQUE CONSTRAINT
ALTER TABLE app_users ADD CONSTRAINT uq_email UNIQUE (email);

-- DROP CONSTRAINT
ALTER TABLE app_users DROP CONSTRAINT uq_email;

-- ADD INDEX
ALTER TABLE app_users ADD INDEX idx_country (country);

-- ADD UNIQUE INDEX
ALTER TABLE app_users ADD UNIQUE INDEX idx_email (email);

-- DROP INDEX
ALTER TABLE app_users DROP INDEX idx_country;

-- ALTER COLUMN DEFAULT (SET)
ALTER TABLE app_users ALTER COLUMN state SET DEFAULT 'active';

-- ALTER COLUMN DEFAULT (DROP)
ALTER TABLE app_users ALTER COLUMN state DROP DEFAULT;

ALTER TABLE users
    ADD COLUMN age INT,
    ADD COLUMN email VARCHAR,
    DROP COLUMN temp,
    MODIFY COLUMN name VARCHAR,
    RENAME COLUMN status TO state,
    ADD CONSTRAINT pk_users PRIMARY KEY (id);


DROP DATABASE IF EXISTS test_db;

DROP TABLE users;
DROP TABLE IF EXISTS users, orders;

DROP VIEW active_users;
DROP VIEW IF EXISTS v_users, v_orders;

DROP INDEX idx_users_name ON users;

TRUNCATE TABLE users;
TRUNCATE TABLE IF EXISTS users;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE users RESTART IDENTITY;
TRUNCATE TABLE users CONTINUE IDENTITY;
TRUNCATE TABLE users CASCADE RESTART IDENTITY;
TRUNCATE TABLE orders, order_items CASCADE;
TRUNCATE TABLE IF EXISTS orders, order_items CASCADE RESTART IDENTITY;

CASCADE;
SELECT 
    u.name, 
    COUNT(o.id) AS order_count,
    ROW_NUMBER() OVER (PARTITION BY u.country ORDER BY u.name) AS row_num
FROM users u
INNER JOIN orders o ON u.id = o.user_id
WHERE u.age >= 18 AND u.status = 'active'
GROUP BY u.name, u.country
HAVING COUNT(o.id) > 0
ORDER BY order_count DESC;

BEGIN TRANSACTION;
IF (EXISTS(SELECT * FROM users WHERE id = 1)) {
    UPDATE users SET status = 'active' WHERE id = 1;
    COMMIT;
} ELSE {
    ROLLBACK;
}
