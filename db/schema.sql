-- Schema for demo portfolio application

-- settings table: store user preferences from configuracoes
CREATE TABLE IF NOT EXISTS settings (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  display_name text,
  email text,
  theme text,
  created_at timestamptz DEFAULT now()
);

-- contact_messages table: store messages from portfolio contact form
CREATE TABLE IF NOT EXISTS contact_messages (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name text,
  email text,
  message text,
  created_at timestamptz DEFAULT now()
);

-- projects table: optional storage for portfolio projects
CREATE TABLE IF NOT EXISTS projects (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  title text,
  description text,
  link text,
  created_at timestamptz DEFAULT now()
);

-- Optional indexes
CREATE INDEX IF NOT EXISTS idx_settings_email ON settings (email);
CREATE INDEX IF NOT EXISTS idx_contact_messages_email ON contact_messages (email);
