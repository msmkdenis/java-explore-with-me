CREATE TABLE IF NOT EXISTS users
(
    user_id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name           VARCHAR(50)                      NOT NULL UNIQUE,
    user_email          VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name VARCHAR(50)                            NOT NULL UNIQUE,
    CONSTRAINT pk_category PRIMARY KEY (category_id),
    CONSTRAINT uq_name UNIQUE (category_name)
    );

CREATE TABLE IF NOT EXISTS locations (
    location_id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat                DOUBLE PRECISION                        NOT NULL,
    lon                DOUBLE PRECISION                        NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (location_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    compilation_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title              VARCHAR(250)                            NOT NULL,
    pinned             BOOLEAN,
    CONSTRAINT pk_compilations PRIMARY KEY (compilation_id)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation          VARCHAR(1000)                           NOT NULL,
    category_id         BIGINT                                  NOT NULL,
    created_on          TIMESTAMP WITHOUT TIME ZONE,
    description         VARCHAR(7000),
    event_date          TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id        BIGINT,
    location_id         BIGINT,
    paid                BOOLEAN                                 NOT NULL,
    participant_limit   INTEGER,
    published_on        TIMESTAMP WITHOUT TIME ZONE,
    request_moderation  BOOLEAN,
    eventStatus         VARCHAR(50),
    title               VARCHAR(100),
    CONSTRAINT pk_events PRIMARY KEY (event_id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE,
    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES locations (location_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id     BIGINT                                  NOT NULL,
    event_id           BIGINT                                  NOT NULL,
    CONSTRAINT pk_compilations_events PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation_event FOREIGN KEY (compilation_id)
        REFERENCES compilations (compilation_id) ON DELETE CASCADE,
    CONSTRAINT fk_event_compilation FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participation_requests (
    participation_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created            TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_id           BIGINT                                  NOT NULL,
    requester_id       BIGINT                                  NOT NULL,
    status             VARCHAR(100),
    CONSTRAINT pk_participation_requests PRIMARY KEY (participation_id),
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE,
    CONSTRAINT fk_requester FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE
);