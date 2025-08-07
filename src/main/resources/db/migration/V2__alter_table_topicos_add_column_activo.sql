ALTER TABLE topicos ADD COLUMN activo TINYINT;
UPDATE topicos SET activo = 1;