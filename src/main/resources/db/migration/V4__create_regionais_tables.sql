CREATE TABLE regional (
                          id BIGSERIAL PRIMARY KEY,
                          id_regional_externo INTEGER NOT NULL,
                          nome VARCHAR(200) NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX uk_regional_externo_ativo
    ON regional (id_regional_externo)
    WHERE ativo = true;

CREATE INDEX idx_regional_externo ON regional (id_regional_externo);
CREATE INDEX idx_regional_ativo ON regional (ativo);