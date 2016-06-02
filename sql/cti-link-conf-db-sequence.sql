-- Sequence: cti_link_enterprise_id_seq

-- DROP SEQUENCE cti_link_enterprise_id_seq;

CREATE SEQUENCE cti_link_enterprise_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 7000000
  CACHE 1;
ALTER TABLE cti_link_enterprise_id_seq
  OWNER TO postgres;

