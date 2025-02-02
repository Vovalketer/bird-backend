INSERT INTO roles(created_at, updated_at, role_type) VALUES(current_timestamp, current_timestamp, 'USER') ON CONFLICT (role_type) DO NOTHING;
INSERT INTO roles(created_at, updated_at, role_type) VALUES(current_timestamp, current_timestamp, 'MODERATOR') ON CONFLICT (role_type) DO NOTHING;
INSERT INTO roles(created_at, updated_at, role_type) VALUES(current_timestamp, current_timestamp, 'ADMIN') ON CONFLICT (role_type) DO NOTHING;
INSERT INTO roles(created_at, updated_at, role_type) VALUES(current_timestamp, current_timestamp, 'SUPER_ADMIN') ON CONFLICT (role_type) DO NOTHING;