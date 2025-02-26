CREATE TABLE usuarios (
    user_id BINARY(16) PRIMARY KEY, -- Para suportar UUID
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
