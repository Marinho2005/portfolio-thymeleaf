-- ============================================
-- SCHEMA DO PORTFÓLIO — SUPABASE
-- Com RLS e policies
-- ============================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- --------------------------------------------
-- SETTINGS
-- --------------------------------------------

CREATE TABLE IF NOT EXISTS settings (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  display_name text,
  email text,
  theme text,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_settings_email
  ON settings (email);

-- Ativar RLS
ALTER TABLE settings ENABLE ROW LEVEL SECURITY;

-- Nenhuma policy pública.
-- Por padrão, com RLS ativado, usuários anônimos
-- não poderão SELECT/INSERT/UPDATE/DELETE nessa tabela.


-- --------------------------------------------
-- CONTACT MESSAGES
-- --------------------------------------------

CREATE TABLE IF NOT EXISTS contact_messages (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name text NOT NULL,
  email text NOT NULL,
  message text NOT NULL,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_contact_messages_email
  ON contact_messages (email);

-- Ativar RLS
ALTER TABLE contact_messages ENABLE ROW LEVEL SECURITY;

-- Visitantes podem APENAS enviar uma mensagem.
CREATE POLICY "Anyone can send contact messages"
ON contact_messages
FOR INSERT
TO anon, authenticated
WITH CHECK (
  length(trim(name)) BETWEEN 1 AND 100
  AND length(trim(email)) BETWEEN 3 AND 254
  AND length(trim(message)) BETWEEN 1 AND 5000
);

-- NÃO criar policy de SELECT para anon/authenticated.
-- Assim, visitantes não conseguem ler as mensagens.
--
-- NÃO criar policies públicas de UPDATE ou DELETE.


-- --------------------------------------------
-- PROJECTS
-- --------------------------------------------

CREATE TABLE IF NOT EXISTS projects (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  title text NOT NULL,
  description text,
  link text,
  created_at timestamptz DEFAULT now()
);

-- Ativar RLS
ALTER TABLE projects ENABLE ROW LEVEL SECURITY;

-- Projetos podem ser visualizados publicamente.
CREATE POLICY "Public can view projects"
ON projects
FOR SELECT
TO anon, authenticated
USING (true);

-- Não permitir INSERT/UPDATE/DELETE publicamente.
-- Essas operações devem ser feitas por uma área administrativa
-- usando uma sessão/autorização apropriada.


-- ============================================
-- OBSERVAÇÃO SOBRE POLICIES
-- ============================================
--
-- contact_messages:
--   anon/authenticated -> INSERT
--   anon/authenticated -> NÃO pode SELECT
--   anon/authenticated -> NÃO pode UPDATE
--   anon/authenticated -> NÃO pode DELETE
--
-- projects:
--   anon/authenticated -> SELECT
--   anon/authenticated -> NÃO pode INSERT
--   anon/authenticated -> NÃO pode UPDATE
--   anon/authenticated -> NÃO pode DELETE
--
-- settings:
--   sem acesso público
--
-- ============================================
