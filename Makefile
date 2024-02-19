# Define variables
SRC_DIR = src
BIN_DIR = bin
PACKAGE_NAME = src


# Target to run the server
run-server:
	mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) $(SRC_DIR)/Server.java
	java -cp $(BIN_DIR) $(PACKAGE_NAME).Server

# Target to run the client
run-client:
	mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) $(SRC_DIR)/Client.java
	java -cp $(BIN_DIR) $(PACKAGE_NAME).Client

# Target to clean compiled files
clean:
	rm -rf $(BIN_DIR)/

