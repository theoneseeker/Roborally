/* Need to switch of FK check for MySQL since there are crosswise FK references */
SET FOREIGN_KEY_CHECKS = 0;;
CREATE TABLE IF NOT EXISTS Game (
  gameID int NOT NULL UNIQUE AUTO_INCREMENT,
  
  name varchar(50),
  board varchar(50),
  phase tinyint,
  step tinyint,
  currentPlayer tinyint NULL,
  
  PRIMARY KEY (gameID),
  FOREIGN KEY (gameID, currentPlayer) REFERENCES Player(gameID, playerID)
);;
  
CREATE TABLE IF NOT EXISTS Player (
  gameID int NOT NULL,
  playerID tinyint NOT NULL,

  name varchar(255),
  colour varchar(31),
  
  positionX int,
  positionY int,
  heading tinyint,
  checkpoint int,
  energy int,
  upgrade1 ENUM('BRAKES', 'FIREWALL', 'HOVER_UNIT'), /* Actually this is bad style, but do not have time at the moment to setup properly.*/
  upgrade2 ENUM('BRAKES', 'FIREWALL', 'HOVER_UNIT'),
  upgrade3 ENUM('BRAKES', 'FIREWALL', 'HOVER_UNIT'),
  PRIMARY KEY (gameID, playerID),
  FOREIGN KEY (gameID) REFERENCES Game(gameID)
);;

CREATE TABLE IF NOT EXISTS PlayerHand (
                                                 GameID INT NOT NULL,
                                                 PlayerID TINYINT NOT NULL,
                                                 Number INT,
                                                 Ordinal INT,

                                                 PRIMARY KEY(PlayerID, GameID, Number),
                                                 FOREIGN KEY(gameID, playerID) REFERENCES Player(gameID, playerID)
);;

CREATE TABLE IF NOT EXISTS PlayerRegister (
                                                     GameID INT NOT NULL,
                                                     PlayerID TINYINT NOT NULL,
                                                     RegNumber INT,
                                                     Ordinal INT,

                                                     PRIMARY KEY(PlayerID, GameID,RegNumber),
                                                     FOREIGN KEY(gameID, playerID) REFERENCES Player(gameID, playerID)
);;

CREATE TABLE IF NOT EXISTS Spaces(
    GameID INT NOT NULL,
    x INT,
    y INT,
    energy INT,
    PRIMARY KEY(GameID, X, Y),
    FOREIGN KEY(GameID) references Game(gameID)
);;

SET FOREIGN_KEY_CHECKS = 1;;
