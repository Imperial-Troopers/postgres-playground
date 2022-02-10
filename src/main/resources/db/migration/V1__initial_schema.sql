CREATE TABLE test
(
    geozone_code VARCHAR(42) NOT NULL,
    code         VARCHAR(42) NOT NULL,
    value        TEXT,
    CONSTRAINT pk PRIMARY KEY (geozone_code, code)
);

CREATE INDEX code_value_idx ON test (code, value);
