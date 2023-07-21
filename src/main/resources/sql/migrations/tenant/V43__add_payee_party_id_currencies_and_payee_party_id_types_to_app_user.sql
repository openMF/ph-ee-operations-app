-- Add the new columns to the AppUser table with TEXT data type
ALTER TABLE m_appuser
ADD payee_party_ids TEXT,
ADD currencies TEXT,
ADD payee_party_id_types TEXT;
