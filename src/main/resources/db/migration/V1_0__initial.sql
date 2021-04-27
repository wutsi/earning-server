CREATE TABLE T_EARNING (
  id                      SERIAL NOT NULL,

  user_id                 BIGINT NOT NULL,
  contract_id             BIGINT,
  partner_id              BIGINT,

  year                    INT NOT NULL,
  month                   INT NOT NULL,
  amount                  DECIMAL(20,4) NOT NULL,
  currency                VARCHAR(3) NOT NULL,

  UNIQUE(user_id, year, month),
  PRIMARY KEY(id)
);
