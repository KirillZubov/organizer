CREATE TABLE files (
   fileID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
   fileName varchar (20) NOT NULL,
   tags varchar (200) NOT NULL,
   PRIMARY KEY (fileID)
);
