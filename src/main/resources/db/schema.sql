CREATE TABLE IF NOT EXISTS t_user (
    c_id VARCHAR(32) NOT NULL PRIMARY KEY,
    c_username VARCHAR(50),
    c_password VARCHAR(50),
    c_email VARCHAR(50),
    c_phone VARCHAR(20),
    c_nickname VARCHAR(50),
    c_version BIGINT DEFAULT 0,
    c_unlocked_at BIGINT DEFAULT 0,
    c_try_count BIGINT DEFAULT 0,
    c_first_try_at BIGINT DEFAULT 0,
    c_deleted_at BIGINT NULL,
    c_create_by VARCHAR(64),
    c_created_at BIGINT,
    c_updated_by VARCHAR(64),
    c_updated_at BIGINT,
    UNIQUE INDEX idx_email (c_email),
    UNIQUE INDEX idx_phone (c_phone)
);

CREATE TABLE IF NOT EXISTS t_login_record (
    c_id BIGINT NOT NULL PRIMARY KEY,
    c_user_id VARCHAR(32) NOT NULL,
    c_token VARCHAR(64),
    c_device_type TINYINT,
    c_device_id VARCHAR(64),
    c_client_ip VARCHAR(64),
    c_remote_ip VARCHAR(64),
    c_device_name VARCHAR(32),
    c_device_desc VARCHAR(100),
    c_login_at BIGINT,
    c_result INT,
    c_ext_info VARCHAR(1024),
    c_logout_at BIGINT DEFAULT 9223372036854775807,
    c_deleted_at BIGINT NULL,
    c_create_by VARCHAR(64),
    c_created_at BIGINT,
    c_updated_by VARCHAR(64),
    c_updated_at BIGINT,
    INDEX idx_user_id__logout_at (c_user_id, c_logout_at),
    INDEX idx_token__logout_at (c_token, c_logout_at)
);

CREATE TABLE IF NOT EXISTS t_order (
    c_id BIGINT NOT NULL PRIMARY KEY,
    c_user_id VARCHAR(32) NOT NULL,
    c_total_amount INT NOT NULL DEFAULT 0,
    c_status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    c_payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    c_paid_at DATETIME NULL,
    c_shipped_at DATETIME NULL,
    c_version BIGINT DEFAULT 0,
    c_deleted_at BIGINT NULL,
    c_create_by VARCHAR(64),
    c_created_at BIGINT,
    c_updated_by VARCHAR(64),
    c_updated_at BIGINT
);

CREATE TABLE IF NOT EXISTS t_order_item (
    c_id BIGINT NOT NULL PRIMARY KEY,
    c_order_id BIGINT NULL,
    c_product_id BIGINT NOT NULL,
    c_product_name VARCHAR(255) NOT NULL,
    c_price INT NOT NULL,
    c_quantity INT NOT NULL,
    c_deleted_at BIGINT NULL,
    c_create_by VARCHAR(64),
    c_created_at BIGINT,
    c_updated_by VARCHAR(64),
    c_updated_at BIGINT
);

CREATE TABLE IF NOT EXISTS t_message (
    c_id BIGINT NOT NULL PRIMARY KEY,
    c_sender_id BIGINT NOT NULL,
    c_device_id VARCHAR(255) NOT NULL,
    c_deleted_at BIGINT NULL,
    c_create_by VARCHAR(64),
    c_created_at BIGINT,
    c_updated_by VARCHAR(64),
    c_updated_at BIGINT
);

CREATE TABLE IF NOT EXISTS t_verification_code (
    c_id VARCHAR(32) NOT NULL PRIMARY KEY,
    c_user_id VARCHAR(32),
    c_target VARCHAR(100) NOT NULL,
    c_code VARCHAR(6) NOT NULL,
    c_business_type VARCHAR(32) NOT NULL,
    c_expire_at BIGINT NOT NULL,
    c_sent_at BIGINT NOT NULL,
    c_deleted_at BIGINT NULL,
    c_create_by VARCHAR(64),
    c_created_at BIGINT,
    c_updated_by VARCHAR(64),
    c_updated_at BIGINT,
    INDEX idx_target__business_type__expire_at (c_target, c_business_type, c_expire_at)
);
