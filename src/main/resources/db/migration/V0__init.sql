CREATE TABLE IF NOT EXISTS account (
    id        UUID PRIMARY KEY,
    username  TEXT NOT NULL,
    last_seen TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friend_request (
    id          UUID PRIMARY KEY,
    sender      UUID NOT NULL REFERENCES account(id),
    recipient   UUID NOT NULL REFERENCES account(id),
    sent        TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS friend (
    friend1 UUID NOT NULL REFERENCES account(id),
    friend2 UUID NOT NULL REFERENCES account(id)
);

-- TODO:// Link Markers
CREATE TABLE IF NOT EXISTS card (
    id                       TEXT PRIMARY KEY,
    franchise                TEXT NOT NULL,
    name                     TEXT NOT NULL,
    human_readable_card_type TEXT,
    frame_type               TEXT,
    description              TEXT,
    type                     TEXT,
    attack                   int,
    defense                  int,
    lvl                      int,
    race                     TEXT,
    archetype                TEXT,
    attribute                TEXT,
    scale                    int,
    linkval                  int,
    tcg_release              DATE,
    ocg_release              DATE,
    has_effect               boolean
);

CREATE TABLE IF NOT EXISTS print (
    id              TEXT,
    card_id         TEXT NOT NULL REFERENCES card (id),
    set_rarity_code TEXT NOT NULL,
    set_name        TEXT,
    set_rarity      TEXT,
    PRIMARY KEY (id, set_rarity_code)
);

CREATE TABLE IF NOT EXISTS card_image (
    id          int PRIMARY KEY,
    card_id     TEXT NOT NULL REFERENCES card (id),
    image_url   TEXT NOT NULL,
    small_url   TEXT,
    cropped_url TEXT
);

CREATE TABLE IF NOT EXISTS collection (
    id        UUID PRIMARY KEY,
    owner_id  UUID NOT NULL REFERENCES account (id),
    name      TEXT NOT NULL,
    franchise TEXT NOT NULL,
    privacy   TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS collection_card (
    id              UUID PRIMARY KEY,
    collection_id   UUID NOT NULL REFERENCES collection (id) ON DELETE CASCADE,
    card_id         TEXT NOT NULL REFERENCES card (id)
);

CREATE UNIQUE INDEX collection_card_idx ON collection_card (collection_id, card_id);

CREATE TABLE IF NOT EXISTS collection_print (
    id                      UUID PRIMARY KEY,
    collection_card_id      UUID NOT NULL REFERENCES collection_card (id) ON DELETE CASCADE,
    print_id                TEXT NOT NULL,
    print_set_rarity_code   TEXT NOT NULL,
    FOREIGN KEY (print_id, print_set_rarity_code) REFERENCES print (id, set_rarity_code)
);

CREATE UNIQUE INDEX card_print_idx ON collection_print (collection_card_id, print_id, print_set_rarity_code);

-- TODO:// GRADING AUTHORITY (PSA, etc.)
CREATE TABLE IF NOT EXISTS collection_print_occurrence (
   collection_print_id  UUID NOT NULL REFERENCES collection_print (id),
   condition            TEXT NOT NULL,
   quantity             int NOT NULL,
   edition              TEXT NOT NULL,
   grade                int,
   grade_authority      TEXT,
   PRIMARY KEY (collection_print_id, condition, edition)
);

CREATE UNIQUE INDEX print_occurrence_idx ON collection_print_occurrence (collection_print_id, condition, edition, grade);