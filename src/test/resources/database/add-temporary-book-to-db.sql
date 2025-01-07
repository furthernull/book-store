INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted) VALUES
    (3, 'Temporary', 'Temporary', '5555555555-555', 0.01, 'tmp', 'https://example.com/tmp', false);

INSERT INTO books_categories (book_id, category_id) VALUES
    (3, 2);