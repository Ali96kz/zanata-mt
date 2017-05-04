CREATE TABLE IF NOT EXISTS Locale (
    id            SERIAL PRIMARY KEY,
    localeCode    VARCHAR(128)      NOT NULL,
    name          VARCHAR(191),
    creationDate  DATE              NOT NULL,
    lastChanged   DATE              NOT NULL,
    UNIQUE (localeCode)
);

CREATE TABLE IF NOT EXISTS Document (
    id            SERIAL PRIMARY KEY,
    url           TEXT          NOT NULL,
    fromLocaleId      BIGINT         NOT NULL REFERENCES Locale (id),
    toLocaleId   BIGINT         NOT NULL REFERENCES Locale (id),
    count     INTEGER     DEFAULT 0,
    creationDate  DATE              NOT NULL,
    lastChanged   DATE              NOT NULL,
    urlHash       VARCHAR(128)      NOT NULL,
    UNIQUE (urlHash, fromLocaleId, toLocaleId)
);

CREATE TABLE IF NOT EXISTS TextFlow (
    id            SERIAL PRIMARY KEY,
    contentHash   VARCHAR(128)      NOT NULL,
    localeId      BIGINT            NOT NULL REFERENCES Locale (id),
    content       TEXT              NOT NULL,
    creationDate  DATE              NOT NULL,
    lastChanged   DATE              NOT NULL,
    UNIQUE (localeId, contentHash)
);

CREATE TABLE IF NOT EXISTS TextFlowTarget (
    id            SERIAL PRIMARY KEY,
    textFlowId    BIGINT      NOT NULL REFERENCES TextFlow (id),
    localeId      BIGINT      NOT NULL REFERENCES Locale (id),
    content       TEXT        NOT NULL,
    rawContent    TEXT        NOT NULL,
    backendId      VARCHAR(20) NOT NULL,
    count     INTEGER     DEFAULT 0,
    creationDate  DATE        NOT NULL,
    lastChanged  DATE         NOT NULL,
    UNIQUE (localeId, textFlowId, backendId)
);

CREATE TABLE IF NOT EXISTS Document_TextFlow (
    id            SERIAL PRIMARY KEY,
    documentId    BIGINT      NOT NULL REFERENCES Document (id),
    textFlowId    BIGINT      NOT NULL REFERENCES TextFlow (id),
    UNIQUE (documentId, textFlowId)
)
