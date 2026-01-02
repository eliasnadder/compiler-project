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

BEGIN_TRANSACTION;
IF (EXISTS(SELECT * FROM users WHERE id = 1)) {
    UPDATE users SET status = 'active' WHERE id = 1;
    COMMIT;
} ELSE {
    ROLLBACK;
}
