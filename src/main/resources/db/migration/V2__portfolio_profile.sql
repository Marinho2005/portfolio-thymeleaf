-- ============================================
-- V2 - PERFIL DO PORTFÓLIO
-- Campos adicionais na tabela settings:
-- foto, bio (descrição), redes sociais
-- ============================================

ALTER TABLE settings ADD COLUMN IF NOT EXISTS photo_url text;
ALTER TABLE settings ADD COLUMN IF NOT EXISTS bio text;
ALTER TABLE settings ADD COLUMN IF NOT EXISTS github_url text;
ALTER TABLE settings ADD COLUMN IF NOT EXISTS linkedin_url text;
ALTER TABLE settings ADD COLUMN IF NOT EXISTS instagram_url text;
ALTER TABLE settings ADD COLUMN IF NOT EXISTS twitter_url text;