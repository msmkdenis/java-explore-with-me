CREATE TABLE IF NOT EXISTS apps (
   app_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   app_name VARCHAR(50)                             NOT NULL,
   CONSTRAINT pk_apps PRIMARY KEY (app_id)
);

CREATE TABLE IF NOT EXISTS hits (
    hits_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app_id    BIGINT                                  NOT NULL,
    uri       VARCHAR(254),
    ip        VARCHAR(50),
    timestamp TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_hits PRIMARY KEY (hits_id),
    CONSTRAINT fk_app FOREIGN KEY (app_id) REFERENCES apps (app_id) ON DELETE CASCADE
    );
