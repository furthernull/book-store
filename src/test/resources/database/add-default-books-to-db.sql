INSERT INTO categories (id, name, description, is_deleted) VALUES
    (1, 'category', 'category description', false),
    (2, 'different', 'different category', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted) VALUES
    (1, 'Book', 'Author', '1234567890-123', 19.99, 'description', 'https://example.com/image', false),
    (2, 'Another', 'Another', '9876543210-789', 14.99, 'another', 'https://example.com/another', false);

INSERT INTO books_categories (book_id, category_id) VALUES
    (1, 1),
    (2, 2);
