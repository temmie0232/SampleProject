CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    version BIGINT NOT NULL
);

CREATE TABLE categories (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    version BIGINT NOT NULL
);

CREATE TABLE authors (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    version BIGINT NOT NULL
);

CREATE TABLE books (
    id CHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    cover_path VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    version BIGINT NOT NULL
);

CREATE TABLE book_authors (
    book_id CHAR(36) NOT NULL,
    author_id CHAR(36) NOT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_book_authors_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_book_authors_author FOREIGN KEY (author_id) REFERENCES authors(id)
);

CREATE TABLE book_categories (
    book_id CHAR(36) NOT NULL,
    category_id CHAR(36) NOT NULL,
    PRIMARY KEY (book_id, category_id),
    CONSTRAINT fk_book_categories_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_book_categories_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE book_copies (
    id CHAR(36) PRIMARY KEY,
    book_id CHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_book_copies_book FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE INDEX idx_book_copies_book_status ON book_copies(book_id, status);

CREATE TABLE loans (
    id CHAR(36) PRIMARY KEY,
    copy_id CHAR(36) NOT NULL,
    borrower_user_id CHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    borrowed_at TIMESTAMP NOT NULL,
    due_date DATE NOT NULL,
    returned_at TIMESTAMP NULL,
    renew_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_loans_copy FOREIGN KEY (copy_id) REFERENCES book_copies(id),
    CONSTRAINT fk_loans_user FOREIGN KEY (borrower_user_id) REFERENCES users(id)
);

CREATE INDEX idx_loans_borrower_status ON loans(borrower_user_id, status);
CREATE INDEX idx_loans_copy_status ON loans(copy_id, status);

CREATE TABLE audit_logs (
    id CHAR(36) PRIMARY KEY,
    actor_user_id CHAR(36) NULL,
    type VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id CHAR(36) NULL,
    payload JSON NULL,
    created_at TIMESTAMP NOT NULL
);

