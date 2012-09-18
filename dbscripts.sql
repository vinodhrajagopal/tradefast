CREATE EVENT mark_expired_posts ON SCHEDULE EVERY 1 MINUTE STARTS CURRENT_TIMESTAMP
COMMENT 'Mark the posts which have expired'
DO UPDATE tradefast.items SET expired=true WHERE end_time < now() AND expired=false;

