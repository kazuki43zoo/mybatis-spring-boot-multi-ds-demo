CREATE TABLE todo (
    todo_id IDENTITY
    ,todo_title TEXT NOT NULL
    ,details TEXT
    ,finished BOOLEAN NOT NULL
);