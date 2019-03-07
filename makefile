SHELL:=/bin/bash

OUTPUT_NAME=portal
SOURCE_PATH=plugin
BUILD_PATH=build
EXTERNAL_PATH=external
SPIGOT_JAR_FILENAME=spigot-1.13.2.jar
CRAFTBUKKIT_JAR_FILENAME=craftbukkit-1.13.2.jar
JAR_DEPS_PATH=$(EXTERNAL_PATH)/$(SPIGOT_JAR_FILENAME):$(EXTERNAL_PATH)/$(CRAFTBUKKIT_JAR_FILENAME)
GIT_TAG:=$(shell git describe --tags)
OUTPUT_VERSIONED_NAME=$(OUTPUT_NAME)-$(GIT_TAG)
SERVER_PATH=server
SERVER_JAR_FILENAME=craftbukkit-1.13.jar

.PHONY: all
all: plugin server

.PHONY: plugin
plugin:
	# step 1 clean up / erase old version
	-rm -r -f $(BUILD_PATH)
	mkdir $(BUILD_PATH) && mkdir $(BUILD_PATH)/bin
	# step 2 compile the plugin into the bin dir
	javac -cp "$(JAR_DEPS_PATH)" -d $(BUILD_PATH)/bin $(SOURCE_PATH)/me/insanj/$(OUTPUT_NAME)/*.java
	javac -cp "$(JAR_DEPS_PATH)" -d $(BUILD_PATH)/bin $(SOURCE_PATH)/net/wesjd/anvilgui/*.java
	#  jar tf external/craftbukkit-1.13.2.jar > hello.txt
	# step 3 copy config .yml to a new "build in progress" directory
	-cp -r $(SOURCE_PATH)/*.yml $(BUILD_PATH)/bin/	
	# step 4 create JAR file using the "build in progress" folder
	jar -cvf $(BUILD_PATH)/$(OUTPUT_VERSIONED_NAME).jar -C $(BUILD_PATH)/bin .

.PHONY: server
server: plugin
	# step 5 remove any existing plugin on the server in the server folder
	-rm -r -f $(SERVER_PATH)/plugins/$(OUTPUT_NAME)*.jar
	-rm -r -f $(SERVER_PATH)/$(SERVER_JAR_FILENAME)
	# step 6 copy the JAR file into the server to run it!
	cp -r $(EXTERNAL_PATH)/$(SERVER_JAR_FILENAME) $(SERVER_PATH)/$(SERVER_JAR_FILENAME)
	cp -r $(BUILD_PATH)/$(OUTPUT_VERSIONED_NAME).jar $(SERVER_PATH)/plugins/$(OUTPUT_VERSIONED_NAME).jar
	# step 7 run the server!
	cd $(SERVER_PATH) && java -Xms1G -Xmx1G -jar -DIReallyKnowWhatIAmDoingISwear $(SERVER_JAR_FILENAME)
