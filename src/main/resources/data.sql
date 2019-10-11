DROP TABLE IF EXISTS user_dto;

CREATE TABLE user_dto (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(250) NOT NULL,
  password VARCHAR(250) NOT NULL,
  twofakey VARCHAR(32)  NOT NULL
);

INSERT INTO user_dto (username, password, twofakey) VALUES
  ('user123', '$2a$10$55m0KEvX97XUeQ4nCbPbeOYW0MdtdMQhg/K6fBEfjG.o8OZHL.BaK', 'KBWUUPIJZ4ZOPUMT3OJHHI4JTWPWUKP5');