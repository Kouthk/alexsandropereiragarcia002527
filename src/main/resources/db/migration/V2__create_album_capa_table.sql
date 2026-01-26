CREATE TABLE album_capa (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL,
    object_key VARCHAR(255) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_album_capa_album
        FOREIGN KEY (album_id)
        REFERENCES album (id)
        ON DELETE CASCADE
);
